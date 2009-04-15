package com.polopoly.pcmd.field;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.pcmd.tool.PolopolyContext;

public class InputTemplateField extends AbstractContentIdField {
    @Override
    protected ContentId getContentId(ContentRead content,
            PolopolyContext context) throws CMException {
        return content.getInputTemplateId();
    }
}
