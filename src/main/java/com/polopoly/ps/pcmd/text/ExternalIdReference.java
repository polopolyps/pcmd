package com.polopoly.ps.pcmd.text;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.cm.ContentReference;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.ps.pcmd.validation.ValidationContext;
import com.polopoly.ps.pcmd.validation.ValidationException;

public class ExternalIdReference implements Reference {
    private static final Logger LOGGER = Logger.getLogger(ExternalIdReference.class.getName());

    private String externalId;

    private String metadataExternalId;

    public ExternalIdReference(String externalId, String metadataExternalId) {
        this(externalId);

        this.metadataExternalId = metadataExternalId;
    }

    public ExternalIdReference(String externalId) {
        this.externalId = externalId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getMetadataExternalId() {
        return metadataExternalId;
    }

    public void validate(ValidationContext context) throws ValidationException {
        context.validateContentExistence(externalId);

        if (metadataExternalId != null) {
            context.validateContentExistence(metadataExternalId);
        }
    }

    public void validateTemplate(ValidationContext context) throws ValidationException {
        context.validateTemplateExistence(externalId);
    }

    @Override
    public String toString() {
        return externalId;
    }

    public ContentReference resolveReference(PolicyCMServer server) throws CMException {
        VersionedContentId referredId = resolveId(server);

        VersionedContentId metadata = null;

        if (metadataExternalId != null) {
            metadata = server.findContentIdByExternalId(new ExternalContentId(metadataExternalId));
        }

        if (metadata != null && referredId.getMajor() == 13 && metadata.getMajor() != 13) {
            LOGGER.log(Level.WARNING,
                       "The referred ID and the major seem to be swapped in a reference to "
                           + metadata.getContentIdString() + " with metadata " + this + ". Swapping them back.");

            VersionedContentId temp = metadata;
            metadata = referredId;
            referredId = temp;
        }

        return new ContentReference(referredId.getContentId(), metadata);
    }

    public VersionedContentId resolveId(PolicyCMServer server) throws CMException {
        VersionedContentId referredId = server.findContentIdByExternalId(new ExternalContentId(externalId));

        if (referredId == null) {
            throw new CMException("Could not find content with external ID \"" + externalId + "\".");
        }

        return referredId;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ExternalIdReference
               && notNull(metadataExternalId).equals(((ExternalIdReference) obj).metadataExternalId)
               && ((ExternalIdReference) obj).externalId.equals(externalId);
    }

    private String notNull(String string) {
        if (string == null) {
            return "";
        } else {
            return string;
        }
    }

    @Override
    public int hashCode() {
        return notNull(metadataExternalId).hashCode() * 13 + externalId.hashCode();
    }

}
