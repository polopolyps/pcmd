package com.polopoly.ps.pcmd.parser;

import com.polopoly.ps.pcmd.util.ContentReference;

public class ContentRefParser implements Parser<ContentReference> {

    public String getHelp() {
        return "<group>:<content reference>";
    }

    public ContentReference parse(String string) throws ParseException {
        int i = string.indexOf(':');

        if (i == -1) {
            throw new ParseException(this, string, "Expected a colon between reference group and content reference");
        }

        return new ContentReference(string.substring(0, i), string.substring(i+1));
    }
}
