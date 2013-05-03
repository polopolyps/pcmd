package com.polopoly.ps.contentimporter.hotdeploy.text;

public class ExternalIdReference
{
    private String externalId;
    private String metadataExternalId;

    public ExternalIdReference(final String externalId,
                               final String metadataExternalId)
    {
        this(externalId);
        this.metadataExternalId = metadataExternalId;
    }

    public ExternalIdReference(final String externalId)
    {
        this.externalId = externalId;
    }

    public String getExternalId()
    {
        return externalId;
    }

    public void setExternalId(final String externalId)
    {
        this.externalId = externalId;
    }

    public String getMetadataExternalId()
    {
        return metadataExternalId;
    }

    @Override
    public String toString()
    {
        return externalId;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof ExternalIdReference && notNull(metadataExternalId).equals(((ExternalIdReference) obj).metadataExternalId)
            && ((ExternalIdReference) obj).externalId.equals(externalId);
    }

    @Override
    public int hashCode()
    {
        return notNull(metadataExternalId).hashCode() * 13 + externalId.hashCode();
    }

    private String notNull(final String string)
    {
        if (string == null) {
            return "";
        } else {
            return string;
        }
    }
}
