package com.polopoly.ps.contentimporter.hotdeploy.text;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class TextContentSet
    implements Iterable<TextContent>
{
    private Map<String, TextContent> contents = new TreeMap<String, TextContent>();

    public TextContent get(final String id)
    {
        return contents.get(id);
    }

    public void add(final TextContent currentContent)
    {
        contents.put(currentContent.getId(), currentContent);
    }

    public Iterator<TextContent> iterator()
    {
        return contents.values().iterator();
    }
}
