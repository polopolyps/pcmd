package com.polopoly.ps.contentimporter.hotdeploy.xml.parser.cache;

import com.polopoly.ps.contentimporter.hotdeploy.client.Major;
import com.polopoly.ps.contentimporter.hotdeploy.util.SingleObjectHolder;
import com.polopoly.ps.contentimporter.hotdeploy.util.Triple;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.ParseCallback;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.ParseContext;

public class ContentMemento extends SingleObjectHolder<Triple<String, Major, String>> implements SingleCallMemento {
    private String externalId;
    private String inputTemplate;
    private Major major;

    public ContentMemento(String externalId, Major major, String inputTemplate) {
        super(new Triple<String, Major, String>(externalId, major, inputTemplate));

        this.externalId = externalId;
        this.major = major;
        this.inputTemplate = inputTemplate;
    }

    public void replay(ParseContext context, SingleCallMemento memento,
            ParseCallback parseCallback) {
        parseCallback.contentFound(
            context, externalId, major, inputTemplate);
    }

}
