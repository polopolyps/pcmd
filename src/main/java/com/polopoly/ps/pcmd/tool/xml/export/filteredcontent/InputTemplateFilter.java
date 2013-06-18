package com.polopoly.ps.pcmd.tool.xml.export.filteredcontent;

import static com.polopoly.ps.pcmd.client.Major.INPUT_TEMPLATE;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.cm.server.ServerNames;
import com.polopoly.cm.util.ContentIdFilter;
import com.polopoly.ps.pcmd.client.Major;

public class InputTemplateFilter implements ContentIdFilter {
    private static final Logger logger = Logger.getLogger(InputTemplateFilter.class.getName());

    private static final String FIELD_INPUT_TEMPLATE = "p.IT.PolicyWidgetOutputTemplate";

    private static final String DEFAULT_REFERENCE_METADATA = "p.DefaultReferenceMetaData";

    private PolicyCMServer server;

    private VersionedContentId defaultReferenceMetadataId;
    private VersionedContentId fieldInputTemplateId;

    public InputTemplateFilter(PolicyCMServer server) {
        this.server = server;

        try {
            defaultReferenceMetadataId =
                server.findContentIdByExternalId(new ExternalContentId(DEFAULT_REFERENCE_METADATA));
            fieldInputTemplateId = server.findContentIdByExternalId(new ExternalContentId(FIELD_INPUT_TEMPLATE));
        } catch (CMException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
    }

    public boolean accept(ContentId contentId) {
        if (isInputTemplate(contentId)) {
            return true;
        }

        if (isField(contentId)) {
            return true;
        }

        if (isReferenceMetadataForField(contentId)) {
            return true;
        }

        return false;
    }

    private boolean isReferenceMetadataForField(ContentId contentId) {
        if (contentId.getMajor() != Major.REFERENCE_METADATA.getIntegerMajor()) {
            return false;
        }

        try {
            ContentRead content = server.getContent(contentId);

            ContentId referredId =
                content.getContentReference(ServerNames.REFERENCE_ATTRG_SYSTEM,
                                            ServerNames.REFERENCE_ATTR_REFERRED_CONTENT_ID);

            if (referredId.getMajor() != Major.INPUT_TEMPLATE.getIntegerMajor()) {
                return false;
            }

            if (!content.getInputTemplateId().equalsIgnoreVersion(defaultReferenceMetadataId)) {
                return false;
            }

            return true;
        } catch (CMException e) {
            logger.log(Level.WARNING, contentId.getContentIdString() + ": " + e.getMessage(), e);

            return false;
        }
    }

    private boolean isField(ContentId contentId) {
        if (contentId.getMajor() != Major.OUTPUT_TEMPLATE.getIntegerMajor()) {
            return false;
        }

        try {
            ContentRead content = server.getContent(contentId);

            return content.getInputTemplateId().equalsIgnoreVersion(fieldInputTemplateId);
        } catch (CMException e) {
            logger.log(Level.WARNING, contentId.getContentIdString() + ": " + e.getMessage(), e);

            return false;
        }
    }

    private boolean isInputTemplate(ContentId contentId) {
        return contentId.getMajor() == INPUT_TEMPLATE.getIntegerMajor();
    }
}
