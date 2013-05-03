package com.polopoly.ps.contentimporter.hotdeploy.xml.parser.cache;

import static java.util.Arrays.asList;

import java.util.List;

import com.polopoly.ps.contentimporter.hotdeploy.client.Major;
import com.polopoly.ps.contentimporter.hotdeploy.file.DeploymentFile;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.ContentListWrapperAwareParseCallback;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.LayoutAwareParseCallback;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.ParseCallback;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.ParseContext;

public class ParseCallbackMultiplexer
    implements ParseCallback, ContentListWrapperAwareParseCallback, LayoutAwareParseCallback
{
    private List<ParseCallback> callbacks;

    public ParseCallbackMultiplexer(ParseCallback... parseCallbacks)
    {
        callbacks = asList(parseCallbacks);
    }

    public void classReferenceFound(DeploymentFile file, String klass)
    {
        for (ParseCallback callback : callbacks) {
            callback.classReferenceFound(file, klass);
        }
    }

    public void contentFound(ParseContext context, String externalId, Major major, String inputTemplate)
    {
        for (ParseCallback callback : callbacks) {
            callback.contentFound(context, externalId, major, inputTemplate);
        }
    }

    public void contentReferenceFound(ParseContext context, Major major, String externalId)
    {
        for (ParseCallback callback : callbacks) {
            callback.contentReferenceFound(context, major, externalId);
        }
    }

    public void contentListWrapperFound(ParseContext context, String externalId, String contentListWrapperClass)
    {
        for (ParseCallback callback : callbacks) {
            if (callback instanceof ContentListWrapperAwareParseCallback) {
                ((ContentListWrapperAwareParseCallback)callback).contentListWrapperFound(context, externalId, contentListWrapperClass);
            }
        }
    }

    public void layoutFound(ParseContext context, String externalId, String layoutClass)
    {
        for (ParseCallback callback : callbacks) {
            if (callback instanceof LayoutAwareParseCallback) {
                ((LayoutAwareParseCallback)callback).layoutFound(context, externalId, layoutClass);
            }
        }
    }
}
