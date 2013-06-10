package com.polopoly.ps.contentimporter.hotdeploy.xml.parser.cache;

import com.polopoly.ps.contentimporter.hotdeploy.client.Major;
import com.polopoly.ps.contentimporter.hotdeploy.util.SingleObjectHolder;
import com.polopoly.ps.contentimporter.hotdeploy.util.Tuple;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.ParseCallback;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.ParseContext;

public class ContentReferenceMemento extends SingleObjectHolder<Tuple<Major, String>> implements SingleCallMemento {
    private String externalId;
    private Major major;

    public ContentReferenceMemento(Major major, String externalId) {
        super(new Tuple<Major, String>(major, externalId));

        this.major = major;
        this.externalId = externalId;
    }

    public void replay(ParseContext context, SingleCallMemento memento,
            ParseCallback parseCallback) {
         parseCallback.contentReferenceFound(context, major, externalId);
    }
}
