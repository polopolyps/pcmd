package com.polopoly.ps.contentimporter.hotdeploy.xml.parser.cache;

import com.polopoly.ps.contentimporter.hotdeploy.util.SingleObjectHolder;
import com.polopoly.ps.contentimporter.hotdeploy.util.Tuple;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.ContentListWrapperAwareParseCallback;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.ParseCallback;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.ParseContext;

public class ContentListWrapperMemento extends SingleObjectHolder<Tuple<String, String>> implements SingleCallMemento {
    private String externalId;
    private final String contentListWrapperClass;

    public ContentListWrapperMemento(String externalId, String contentListWrapperClass) {
        super(new Tuple<String, String>(externalId, contentListWrapperClass));

        this.externalId = externalId;
        this.contentListWrapperClass = contentListWrapperClass;
    }

    public void replay(ParseContext context, SingleCallMemento memento,
            ParseCallback parseCallback)
    {
        if (parseCallback instanceof ContentListWrapperAwareParseCallback) {
            ((ContentListWrapperAwareParseCallback) parseCallback).contentListWrapperFound(context, externalId, contentListWrapperClass);
        }
    }
}
