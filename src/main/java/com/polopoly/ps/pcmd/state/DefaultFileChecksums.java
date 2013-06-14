package com.polopoly.ps.pcmd.state;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.Content;
import com.polopoly.cm.client.impl.exceptions.EJBFinderException;
import com.polopoly.cm.client.impl.exceptions.LockException;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.community.util.content.PolicySingletonUtil;
import com.polopoly.ps.pcmd.file.DeploymentFile;
import com.polopoly.ps.pcmd.util.FetchingIterator;

public class DefaultFileChecksums implements FileChecksums {
    public static final String CHECKSUMS_SINGLETON_EXTERNAL_ID_NAME = "hotdeploy.FileChecksumsSingleton";
    public static final String FILE_CHECKSUMS_INPUT_TEMPLATE_NAME = "p.SystemConfig";

    private static final Logger logger = Logger.getLogger(DefaultFileChecksums.class.getName());

    private PolicyCMServer server;
    private FileChecksumsPseudoPolicy checksumsPolicy;
    private String externalId = CHECKSUMS_SINGLETON_EXTERNAL_ID_NAME;

    private Map<DeploymentFile, Checksums> changes = new HashMap<DeploymentFile, Checksums>();

    private class Checksums {
        private long quickChecksum = -1;
        private long slowChecksum = -1;
        private String additionalInformation;
    }

    private class DeleteChecksums extends Checksums {
        // indicates that a checksum should be deleted.
    }

    private FileChecksumsPseudoPolicy getLatestChecksumsPolicy(PolicyCMServer server)
        throws CouldNotFetchChecksumsException {
        try {
            return new FileChecksumsPseudoPolicy(PolicySingletonUtil.getSingleton(server, 17, externalId,
                                                                                  FILE_CHECKSUMS_INPUT_TEMPLATE_NAME,
                                                                                  Policy.class));
        } catch (CMException e) {
            throw new CouldNotFetchChecksumsException(e.getMessage(), e);
        }
    }

    public DefaultFileChecksums(PolicyCMServer server) throws CouldNotFetchChecksumsException {
        this(server, CHECKSUMS_SINGLETON_EXTERNAL_ID_NAME);
    }

    public DefaultFileChecksums(PolicyCMServer server, String externalId) throws CouldNotFetchChecksumsException {
        this.server = server;
        this.externalId = externalId;

        checksumsPolicy = getLatestChecksumsPolicy(server);
    }

    public void clear() {
        try {
            VersionedContentId checksumsId = getLatestChecksumVersion();
            checksumsPolicy = getLatestChecksumsPolicy(server);

            try {
                checksumsPolicy = new FileChecksumsPseudoPolicy(server.createContentVersion(checksumsId));

                changes.clear();
                checksumsPolicy.clear();

                checksumsPolicy.commit();
            } catch (RuntimeException e) {
                failPersisting(e);
            } catch (LockException e) {
                handleSingletonLocked(checksumsId);

                // retry
                persist();
            } catch (CMException e) {
                failPersisting(e);
            }
        } catch (CouldNotUpdateStateException e) {
            logger.log(Level.WARNING, "While deleting file checksums: " + e.getMessage(), e);
        } catch (CouldNotFetchChecksumsException e) {
            logger.log(Level.WARNING, "While deleting file checksums: " + e.getMessage(), e);
        }
    }

    public long getQuickChecksum(DeploymentFile file) {
        Checksums changedChecksum = changes.get(file);

        if (changedChecksum != null) {
            return changedChecksum.quickChecksum;
        } else {
            return checksumsPolicy.getQuickChecksum(file);
        }
    }

    public long getSlowChecksum(DeploymentFile file) {
        Checksums changedChecksum = changes.get(file);

        if (changedChecksum != null) {
            return changedChecksum.slowChecksum;
        } else {
            return checksumsPolicy.getSlowChecksum(file);
        }
    }

    public synchronized void deleteChecksums(DeploymentFile file) {
        changes.put(file, new DeleteChecksums());
    }

    public synchronized void setChecksums(DeploymentFile file, long quickChecksum, long slowChecksum) {
        Checksums checksums = new Checksums();

        try {
            checksums.additionalInformation = getAdditionalInformation(file);
        } catch (NoInformationStoredException e) {
            // fine. leave it empty.
        }

        checksums.quickChecksum = quickChecksum;
        checksums.slowChecksum = slowChecksum;

        changes.put(file, checksums);
    }

    private VersionedContentId getLatestChecksumVersion() throws CouldNotUpdateStateException {
        try {
            return server.translateSymbolicContentId(new ExternalContentId(externalId));
        } catch (EJBFinderException finderException) {
            throw new CouldNotUpdateStateException("Could not find existing checksum policy singleton: "
                                                   + finderException.getMessage());
        } catch (CMException cmException) {
            throw new CouldNotUpdateStateException("Could not fetch existing checksum policy singleton: "
                                                   + cmException.getMessage(), cmException);
        }
    }

    private void handleSingletonLocked(VersionedContentId checksumsId) throws CouldNotUpdateStateException {
        try {
            logger.log(Level.WARNING, "The checksum singleton " + checksumsId.getContentId().getContentIdString()
                                      + " was locked. Forcing an unlock.");

            Content checksumContent = (Content) server.getContent(checksumsId);

            checksumContent.forcedUnlock();
        } catch (CMException unlockException) {
            logger.log(Level.WARNING, "Unlocking failed.");

            failPersisting(unlockException);
        }
    }

    public synchronized void persist() throws CouldNotUpdateStateException {
        if (areAllChangesPersisted()) {
            return;
        }

        VersionedContentId checksumsId = getLatestChecksumVersion();

        try {
            checksumsPolicy = new FileChecksumsPseudoPolicy(server.createContentVersion(checksumsId));

            for (Map.Entry<DeploymentFile, Checksums> change : changes.entrySet()) {
                DeploymentFile changedFile = change.getKey();
                Checksums changedChecksums = change.getValue();

                if (changedChecksums instanceof DeleteChecksums) {
                    checksumsPolicy.deleteChecksums(changedFile);
                } else {
                    checksumsPolicy.setChecksums(changedFile, changedChecksums.quickChecksum,
                                                 changedChecksums.slowChecksum);
                }

                if (changedChecksums.additionalInformation != null) {
                    checksumsPolicy.setAdditionalInformation(changedFile, changedChecksums.additionalInformation);
                }
            }

            changes.clear();

            checksumsPolicy.commit();
        } catch (RuntimeException e) {
            failPersisting(e);
        } catch (LockException e) {
            handleSingletonLocked(checksumsId);

            // retry
            persist();
        } catch (CMException e) {
            failPersisting(e);
        }
    }

    private void failPersisting(Exception e) throws CouldNotUpdateStateException {
        try {
            server.abortContent(checksumsPolicy.getDelegatePolicy(), true);
        } catch (CMException cmException) {
            logger.log(Level.WARNING, "Failed aborting new version of checksums: " + cmException.getMessage(),
                       cmException);
        }

        throw new CouldNotUpdateStateException(e);
    }

    public boolean areAllChangesPersisted() {
        return changes.isEmpty();
    }

    @Override
    public Iterator<DeploymentFile> iterator() {
        return new FetchingIterator<DeploymentFile>() {
            private Iterator<Entry<DeploymentFile, Checksums>> changeIterator = changes.entrySet().iterator();
            private Iterator<DeploymentFile> fileListIterator = checksumsPolicy.iterator();

            @Override
            protected DeploymentFile fetch() {
                while (changeIterator.hasNext()) {
                    Entry<DeploymentFile, Checksums> nextChange = changeIterator.next();

                    if (!(nextChange.getValue() instanceof DeleteChecksums)) {
                        return nextChange.getKey();
                    }
                }

                while (fileListIterator.hasNext()) {
                    DeploymentFile file = fileListIterator.next();

                    if (!changes.containsKey(file)) {
                        return file;
                    }
                }

                return null;
            }

        };
    }

    @Override
    public String getAdditionalInformation(DeploymentFile file) throws NoInformationStoredException {
        Checksums change = changes.get(file);

        if (change != null && change.additionalInformation != null) {
            return change.additionalInformation;
        }

        return checksumsPolicy.getAdditionalInformation(file);
    }

    @Override
    public void setAdditionalInformation(DeploymentFile file, String additionalInformation) {
        Checksums checksums = new Checksums();
        checksums.quickChecksum = getQuickChecksum(file);
        checksums.slowChecksum = getSlowChecksum(file);
        checksums.additionalInformation = additionalInformation;

        changes.put(file, checksums);
    }
}
