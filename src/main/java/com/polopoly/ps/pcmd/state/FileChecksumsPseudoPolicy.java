package com.polopoly.ps.pcmd.state;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.Content;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.server.ServerNames;
import com.polopoly.community.component.LongComponent;
import com.polopoly.ps.pcmd.file.DeploymentFile;
import com.polopoly.ps.pcmd.state.FileListPseudoPolicy.NoFileAtIndexException;

@SuppressWarnings("deprecation")
/**
 * TODO: remove policy parent class. it's not a policy anymore but a pseudo-policy in order
 * to avoid needing an input template.
 */
public class FileChecksumsPseudoPolicy {
    private static final Logger logger = Logger.getLogger(FileChecksumsPseudoPolicy.class.getName());

    public static final int ATTRIBGROUP_MAXLEN = 128;

    private static final int HALF_ATTRIB_GROUP_MAXLEN = ATTRIBGROUP_MAXLEN / 2;

    private static final String QUICK_CHECKSUM_COMPONENT = "quick";
    private static final String SLOW_CHECKSUM_COMPONENT = "slow";
    private static final String ADDITIONAL_INFO_COMPONENT = "additional";

    private Content content;

    private Policy delegatePolicy;

    @Deprecated
    public FileChecksumsPseudoPolicy() {
    }

    public FileChecksumsPseudoPolicy(Policy delegatePolicy) {
        this.delegatePolicy = delegatePolicy;
        this.content = delegatePolicy.getContent();
    }

    static String getAttributeGroup(DeploymentFile file) {
        String result = file.getName();

        result = result.replaceAll("\\\\", "/");
        if (result.length() > ATTRIBGROUP_MAXLEN) {
            if (result.endsWith(".content")) {
                // keep ".c"
                result = result.substring(0, result.length() - 6);
            }
        }

        if (result.length() > ATTRIBGROUP_MAXLEN) {
            result = eliminate(result, ".jar");
        }

        if (result.length() > ATTRIBGROUP_MAXLEN) {
            result = eliminate(result, "-1.0-SNAPSHOT");
        }

        if (result.length() > ATTRIBGROUP_MAXLEN) {
            int i = result.indexOf('!');

            if (i > 10) {
                result = result.substring(0, 10) + ".." + result.substring(i);
            }
        }

        if (result.length() > ATTRIBGROUP_MAXLEN) {
            result =
                result.substring(0, HALF_ATTRIB_GROUP_MAXLEN) + ".."
                    + result.substring(result.length() - HALF_ATTRIB_GROUP_MAXLEN + 2); // Added
                                                                                        // removal
                                                                                        // of
                                                                                        // ".."
        }

        return result;
    }

    private static String eliminate(String result, String text) {
        int i = result.indexOf(text);

        if (i != -1) {
            result = result.substring(0, i) + result.substring(i + text.length());
        }
        return result;
    }

    private LongComponent getQuickChecksumComponent(DeploymentFile file) {
        LongComponent result = new LongComponent(getAttributeGroup(file), QUICK_CHECKSUM_COMPONENT, -1);
        result.setNullAllowed(true);
        return result;
    }

    private LongComponent getSlowChecksumComponent(DeploymentFile file) {
        LongComponent result = new LongComponent(getAttributeGroup(file), SLOW_CHECKSUM_COMPONENT, -1);
        result.setNullAllowed(true);
        return result;
    }

    public void commit() throws CMException {
        if (content.getName() == null) {
            content.setName("Hot Deploy Content State");

            content.setMetaDataComponent(ServerNames.CMD_ATTRG_SYSTEM, ServerNames.CMD_ATTR_MAXVERSIONS, "1");
        }

        content.commit();
    }

    public long getQuickChecksum(DeploymentFile file) {
        return getQuickChecksumComponent(file).getLongValue(delegatePolicy);
    }

    public long getSlowChecksum(DeploymentFile file) {
        return getSlowChecksumComponent(file).getLongValue(delegatePolicy);
    }

    public void deleteChecksums(DeploymentFile file) throws CouldNotUpdateStateException {
        try {
            getQuickChecksumComponent(file).setStringValue(delegatePolicy, null);
            getSlowChecksumComponent(file).setStringValue(delegatePolicy, null);

            FileListPseudoPolicy fileList = new FileListPseudoPolicy(delegatePolicy);

            for (int i = 0; i < fileList.getHighestIndexInFileList(); i++) {
                try {
                    if (file.equals(fileList.getFile(i))) {
                        fileList.clearFile(i);
                        break;
                    }
                } catch (NoFileAtIndexException e) {
                    // try next.
                }
            }
        } catch (CMException e) {
            throw new CouldNotUpdateStateException("While deleting stored state for " + file + ": " + e, e);
        }
    }

    public void setChecksums(DeploymentFile file, long quickChecksum, long slowChecksum)
        throws CouldNotUpdateStateException {
        try {
            getQuickChecksumComponent(file).setLongValue(delegatePolicy, quickChecksum);
            getSlowChecksumComponent(file).setLongValue(delegatePolicy, slowChecksum);

            FileListPseudoPolicy fileList = new FileListPseudoPolicy(delegatePolicy);

            int index = fileList.getHighestIndexInFileList();

            fileList.setFile(index, file);

            fileList.setHighestIndexInFileList(index + 1);
        } catch (CMException e) {
            throw new CouldNotUpdateStateException("While saving deployment state of " + file + ": " + e, e);
        }
    }

    public void clear() {
        try {
            for (String componentGroup : content.getComponentGroupNames()) {
                for (String component : content.getComponentNames(componentGroup)) {
                    if (component.equals(QUICK_CHECKSUM_COMPONENT) || component.equals(SLOW_CHECKSUM_COMPONENT)) {
                        content.setComponent(componentGroup, component, null);
                    }
                }
            }
        } catch (CMException e) {
            logger.log(Level.WARNING, "While clearing file checksums: " + e.getMessage(), e);
        }
    }

    public Policy getDelegatePolicy() {
        return delegatePolicy;
    }

    public Iterator<DeploymentFile> iterator() {
        return new FileListPseudoPolicy(delegatePolicy).iterator();
    }

    public String getAdditionalInformation(DeploymentFile file) throws NoInformationStoredException {
        String result = null;

        try {
            result = delegatePolicy.getContent().getComponent(getAttributeGroup(file), ADDITIONAL_INFO_COMPONENT);
        } catch (CMException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }

        if (result == null) {
            throw new NoInformationStoredException();
        }

        return result;
    }

    public void setAdditionalInformation(DeploymentFile file, String additionalInformation) {
        try {
            delegatePolicy.getContent().setComponent(getAttributeGroup(file), ADDITIONAL_INFO_COMPONENT,
                                                     additionalInformation);
        } catch (CMException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
    }
}
