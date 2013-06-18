package com.polopoly.ps.pcmd.state;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.ContentPolicy;
import com.polopoly.cm.server.ServerNames;
import com.polopoly.community.component.LongComponent;
import com.polopoly.ps.pcmd.file.DeploymentFile;

@Deprecated
/**
 * Replaced by {@link FileChecksumsPseudoPolicy}.
 */
public class FileChecksumsPolicy extends ContentPolicy {
    private static final Logger logger = Logger.getLogger(FileChecksumsPolicy.class.getName());

    public static final int ATTRIBGROUP_MAXLEN = 128;

    private static final String QUICK_CHECKSUM_COMPONENT = "quick";

    private static final String SLOW_CHECKSUM_COMPONENT = "slow";

    private LongComponent getQuickChecksumComponent(DeploymentFile file) {
        return new LongComponent(FileChecksumsPseudoPolicy.getAttributeGroup(file), QUICK_CHECKSUM_COMPONENT, -1);
    }

    private LongComponent getSlowChecksumComponent(DeploymentFile file) {
        return new LongComponent(FileChecksumsPseudoPolicy.getAttributeGroup(file), SLOW_CHECKSUM_COMPONENT, -1);
    }

    @Override
    public void postCreateSelf() throws CMException {
        if (getName() == null) {
            setName("Hot Deploy Content State");

            getContentUnwrapped().setMetaDataComponent(ServerNames.CMD_ATTRG_SYSTEM, ServerNames.CMD_ATTR_MAXVERSIONS,
                                                       "1");
        }
    }

    public long getQuickChecksum(DeploymentFile file) {
        return getQuickChecksumComponent(file).getLongValue(this);
    }

    public long getSlowChecksum(DeploymentFile file) {
        return getSlowChecksumComponent(file).getLongValue(this);
    }

    public void setChecksums(DeploymentFile file, long quickChecksum, long slowChecksum)
        throws CouldNotUpdateStateException {
        try {
            getQuickChecksumComponent(file).setLongValue(this, quickChecksum);
            getSlowChecksumComponent(file).setLongValue(this, slowChecksum);
        } catch (CMException e) {
            throw new CouldNotUpdateStateException("While saving deployment state of " + file + ": " + e, e);
        }
    }

    public void clear() {
        try {
            for (String componentGroup : getComponentGroupNames()) {
                for (String component : getComponentNames(componentGroup)) {
                    if (component.equals(QUICK_CHECKSUM_COMPONENT) || component.equals(SLOW_CHECKSUM_COMPONENT)) {
                        setComponent(componentGroup, component, null);
                    }
                }
            }
        } catch (CMException e) {
            logger.log(Level.WARNING, "While clearing file checksums: " + e.getMessage(), e);
        }
    }
}
