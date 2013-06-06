package com.polopoly.ps.contentimporter.hotdeploy.text;

import java.util.List;
import java.util.Map;

import com.polopoly.ps.contentimporter.hotdeploy.client.Major;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.ParseCallback;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.ParseContext;

public class TextContentParseCallbackAdapter
{
    private TextContentSet contentSet;

    public TextContentParseCallbackAdapter(final TextContentSet contentSet)
    {
        this.contentSet = contentSet;
    }

    public void callback(final ParseCallback callback,
                         final ParseContext context)
    {
        // first call content found for all object so validation tool understands that it is ok
        // to have objects in any order in a text content file.

        for (TextContent content : contentSet) {
            ExternalIdReference inputTemplate = content.getInputTemplate();

            if (inputTemplate == null && content.getTemplateId() != null) {
                TextContent template = contentSet.get(content.getTemplateId());
                inputTemplate = template.getInputTemplate();
            }

            if (inputTemplate != null) {
                String inputTemplateExternalId = ((ExternalIdReference) inputTemplate).getExternalId();
                callback.contentFound(context, content.getId(), content.getMajor(), inputTemplateExternalId);
            }
        }

        for (TextContent content : contentSet) {
            callback(content, context, callback);
        }
    }

    private void callback(final TextContent content,
                          final ParseContext context,
                          final ParseCallback callback)
    {
        if (content.getInputTemplate() != null) {
            callback(content.getInputTemplate(), Major.INPUT_TEMPLATE, context, callback);
        }

        if (content.getSecurityParent() != null) {
            callback(content.getSecurityParent(), context, callback);
        }

        for (Map<String, ExternalIdReference> referenceGroup : content.getReferences().values()) {
            for (ExternalIdReference reference : referenceGroup.values()) {
                callback(reference, context, callback);
            }
        }

        for (List<ExternalIdReference> list : content.getLists().values()) {
            for (ExternalIdReference reference : list) {
                callback(reference, context, callback);
            }
        }

        for (Publishing publishing : content.getPublishings()) {
            callback(publishing.getPublishIn(), context, callback);
        }
    }

    private void callback(final ExternalIdReference reference,
                          final Major major,
                          final ParseContext context,
                          final ParseCallback callback)
    {
        callback.contentReferenceFound(context, Major.UNKNOWN, ((ExternalIdReference) reference).getExternalId());
    }

    private void callback(final ExternalIdReference reference,
                          final ParseContext context,
                          final ParseCallback callback)
    {
        callback(reference, Major.UNKNOWN, context, callback);
    }
}
