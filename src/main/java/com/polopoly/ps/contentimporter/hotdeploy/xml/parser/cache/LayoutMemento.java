package com.polopoly.ps.contentimporter.hotdeploy.xml.parser.cache;

import com.polopoly.ps.contentimporter.hotdeploy.util.SingleObjectHolder;
import com.polopoly.ps.contentimporter.hotdeploy.util.Tuple;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.LayoutAwareParseCallback;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.ParseCallback;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.ParseContext;

public class LayoutMemento extends SingleObjectHolder<Tuple<String, String>>
    implements SingleCallMemento
{
    private String externalId;
    private final String layoutClass;

    public LayoutMemento(String externalId, String layoutClass)
    {
        super(new Tuple<String, String>(externalId, layoutClass));

        this.externalId = externalId;
        this.layoutClass = layoutClass;
    }

    public void replay(ParseContext context, SingleCallMemento memento, ParseCallback parseCallback)
    {
        if (parseCallback instanceof LayoutAwareParseCallback) {
            ((LayoutAwareParseCallback) parseCallback).layoutFound(context, externalId, layoutClass);
        }
    }
}
