package com.polopoly.ps.contentimporter.hotdeploy.xml.parser;

public interface ContentListWrapperAwareParseCallback {
    /**
     * A content list wrapper was found. Called after contentFound for the same template.
     */
    void contentListWrapperFound(ParseContext context, String externalId, String contentListWrapperClass);
}
