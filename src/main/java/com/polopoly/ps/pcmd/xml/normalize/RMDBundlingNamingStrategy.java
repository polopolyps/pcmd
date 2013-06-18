package com.polopoly.ps.pcmd.xml.normalize;

import static com.polopoly.ps.pcmd.client.Major.REFERENCE_METADATA;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.cm.xml.util.export.ExternalIdGenerator;

/**
 * Puts reference metadatas in the same file as the object containing them
 * (their security parent). Otherwise, we have lots of tangled dependencies
 * between the objects.
 */
public class RMDBundlingNamingStrategy extends DefaultNormalizationNamingStrategy {
    private static final Logger LOGGER = Logger.getLogger(RMDBundlingNamingStrategy.class.getName());

    public RMDBundlingNamingStrategy(PolicyCMServer server, ExternalIdGenerator externalIdGenerator, File directory,
                                     String extension) {
        super(server, externalIdGenerator, directory, extension);
    }

    @Override
    public File getFileName(ContentRead content) {
        if (isReferenceMetadata(content)) {
            ContentId parentId = content.getSecurityParentId();

            try {
                ContentRead parent = server.getContent(parentId);

                return getFileName(parent);
            } catch (CMException e) {
                LOGGER.log(Level.WARNING,
                           "Could not get security parent of " + content.getContentId().getContentIdString()
                               + " with ID " + parentId.getContentIdString() + ": " + e.getMessage(), e);
            }
        }

        return super.getFileName(content);
    }

    private boolean isReferenceMetadata(ContentRead content) {
        return content.getContentId().getMajor() == REFERENCE_METADATA.getIntegerMajor();
    }
}
