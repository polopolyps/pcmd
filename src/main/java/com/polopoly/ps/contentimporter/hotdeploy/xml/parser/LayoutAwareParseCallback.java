package com.polopoly.ps.contentimporter.hotdeploy.xml.parser;

public interface LayoutAwareParseCallback
{
    /**
     * A content list wrapper was found. Called after contentFound for the same template.
     */
    void layoutFound(ParseContext context, String externalId, String layoutClass);
}
