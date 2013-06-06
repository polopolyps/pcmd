package com.polopoly.ps.contentimporter.hotdeploy.text;

public class Publishing
{
    private ExternalIdReference publishIn;
    private String publishInGroup;

    public Publishing(final ExternalIdReference publishIn,
                      final String publishInGroup)
    {
        super();

        this.publishIn = publishIn;
        this.publishInGroup = publishInGroup;
    }

    public ExternalIdReference getPublishIn()
    {
        return publishIn;
    }

    public String getPublishInGroup()
    {
        return publishInGroup;
    }
}
