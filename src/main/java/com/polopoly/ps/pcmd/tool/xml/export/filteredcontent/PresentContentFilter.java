package com.polopoly.ps.pcmd.tool.xml.export.filteredcontent;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.cm.util.ContentIdFilter;
import com.polopoly.ps.pcmd.xml.present.PresentContentAware;

public class PresentContentFilter implements PresentContentAware, ContentIdFilter {
    private static final Logger logger = Logger.getLogger(PresentContentFilter.class.getName());

    private Set<ContentId> presentIds = new HashSet<ContentId>(100);

    private PolicyCMServer server;

    public PresentContentFilter(PolicyCMServer server) {
        this.server = server;
    }

    public void presentContent(String externalId) {
        present(externalId);
    }

    public void presentTemplate(String inputTemplate) {
        present(inputTemplate);
    }

    private void present(String externalId) {
        try {
            VersionedContentId contentId = server.findContentIdByExternalId(new ExternalContentId(externalId));

            if (contentId != null) {
                presentIds.add(contentId.getContentId());
            } else {
                logger.log(Level.FINE, "Purportedly present content " + externalId + " could not be found.");
            }
        } catch (CMException e) {
            logger.log(Level.WARNING, "While looking up present content " + externalId + ": " + e.getMessage());
        }
    }

    public Set<ContentId> getPresentIds() {
        return presentIds;
    }

    public boolean accept(ContentId contentId) {
        return presentIds.contains(contentId.getContentId());
    }
}
