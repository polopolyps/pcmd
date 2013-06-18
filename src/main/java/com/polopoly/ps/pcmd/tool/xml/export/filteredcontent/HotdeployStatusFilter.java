package com.polopoly.ps.pcmd.tool.xml.export.filteredcontent;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.cm.util.ContentIdFilter;
import com.polopoly.ps.pcmd.state.DefaultFileChecksums;

public class HotdeployStatusFilter implements ContentIdFilter {
    private static final Logger logger = Logger.getLogger(HotdeployStatusFilter.class.getName());

    private static final String OLD_HOTDEPLOY_STATE_EXTERNAL_ID = "p.HotDeployDirectoryState";

    private VersionedContentId hotdeployStatusContentId;

    private VersionedContentId oldHotdeployStatusContentId;

    public HotdeployStatusFilter(PolicyCMServer server) {
        try {
            hotdeployStatusContentId =
                server.findContentIdByExternalId(new ExternalContentId(
                    DefaultFileChecksums.CHECKSUMS_SINGLETON_EXTERNAL_ID_NAME));
        } catch (CMException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }

        try {
            oldHotdeployStatusContentId =
                server.findContentIdByExternalId(new ExternalContentId(OLD_HOTDEPLOY_STATE_EXTERNAL_ID));
        } catch (CMException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
    }

    public boolean accept(ContentId contentId) {
        if (contentId == null) {
            return false;
        }

        if (contentId.equalsIgnoreVersion(hotdeployStatusContentId)) {
            return true;
        }

        if (contentId.equalsIgnoreVersion(oldHotdeployStatusContentId)) {
            return true;
        }

        return false;
    }

}
