package com.polopoly.ps.pcmd.tool.xml.export;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.cm.xml.util.export.ExternalIdGenerator;

public class PreserveExistingPrefixOthersExternalIdGenerator implements ExternalIdGenerator {
    private static final Logger logger = Logger.getLogger(PreserveExistingPrefixOthersExternalIdGenerator.class
        .getName());

    private String prefix;

    private PolicyCMServer server;

    public PreserveExistingPrefixOthersExternalIdGenerator(PolicyCMServer server, String prefix) {
        this.prefix = prefix;
        this.server = server;
    }

    @Override
    public String generateExternalId(ContentRead content) {
        try {
            ExternalContentId externalId = content.getExternalId();

            if (externalId != null) {
                return externalId.getExternalId();
            }
        } catch (CMException e) {
            logger.log(Level.WARNING, "Could not get external ID of " + content.getContentId().getContentIdString()
                                      + ": " + e.getMessage(), e);
        }

        return generateExternalIdFromContentId(content);
    }

    private String generateExternalIdFromContentId(ContentRead content) {
        String contentIdString = prefix + content.getContentId().getContentId().getContentIdString();

        if (!externalIdExists(contentIdString)) {
            return contentIdString;
        } else {
            return addSuffixToFindUnusedId(contentIdString);
        }
    }

    private String addSuffixToFindUnusedId(String contentIdString) {
        int count = 0;

        do {
            String tryId = contentIdString + '.' + Integer.toString(count);

            if (!externalIdExists(tryId)) {
                return tryId;
            }

            count++;
        } while (true);
    }

    private boolean externalIdExists(String externalId) {
        try {
            return server.findContentIdByExternalId(new ExternalContentId(externalId)) != null;
        } catch (CMException e) {
            logger.log(Level.WARNING, "While resolving external ID \"" + externalId + "\": " + e.getMessage(), e);

            return true;
        }
    }
}
