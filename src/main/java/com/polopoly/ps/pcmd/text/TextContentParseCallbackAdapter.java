package com.polopoly.ps.pcmd.text;

import java.util.List;
import java.util.Map;

import com.polopoly.ps.pcmd.client.Major;
import com.polopoly.ps.pcmd.xml.parser.ParseCallback;
import com.polopoly.ps.pcmd.xml.parser.ParseContext;

public class TextContentParseCallbackAdapter {
    private TextContentSet contentSet;

    public TextContentParseCallbackAdapter(TextContentSet contentSet) {
        this.contentSet = contentSet;
    }

    public void callback(ParseCallback callback, ParseContext context) {
        // first call content found for all object so validation tool
        // understands that it is ok
        // to have objects in any order in a text content file.
        for (TextContent content : contentSet) {
            Reference inputTemplate = content.getInputTemplate();

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

    private void callback(TextContent content, ParseContext context, ParseCallback callback) {
        if (content.getInputTemplate() != null) {
            callback(content.getInputTemplate(), Major.INPUT_TEMPLATE, context, callback);
        }

        if (content.getSecurityParent() != null) {
            callback(content.getSecurityParent(), context, callback);
        }

        for (Map<String, Reference> referenceGroup : content.getReferences().values()) {
            for (Reference reference : referenceGroup.values()) {
                callback(reference, context, callback);
            }
        }

        for (List<Reference> list : content.getLists().values()) {
            for (Reference reference : list) {
                callback(reference, context, callback);
            }
        }

        for (Publishing publishing : content.getPublishings()) {
            callback(publishing.getPublishIn(), context, callback);
        }
    }

    private void callback(Reference reference, Major major, ParseContext context, ParseCallback callback) {
        callback.contentReferenceFound(context, Major.UNKNOWN, ((ExternalIdReference) reference).getExternalId());
    }

    private void callback(Reference reference, ParseContext context, ParseCallback callback) {
        callback(reference, Major.UNKNOWN, context, callback);
    }
}
