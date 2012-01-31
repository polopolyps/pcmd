package com.polopoly.util.policy;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.client.InputTemplate;
import com.polopoly.cm.collections.ContentList;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.content.ContentUtilImpl;

public class InputTemplateUtilImpl extends ContentUtilImpl implements InputTemplateUtil {

    private InputTemplate inputTemplate;

    public InputTemplateUtilImpl(InputTemplate inputTemplate, PolopolyContext context) {
        super(inputTemplate, context);

        this.inputTemplate = inputTemplate;
    }

    public String[] getAvailableOutputTemplateModes() throws CMException {
        return inputTemplate.getAvailableOutputTemplateModes();
    }

    public ContentId getEditorId(String contextName) throws CMException {
        return inputTemplate.getEditorId(contextName);
    }

    public ContentId getLayoutId(String contextName) throws CMException {
        return inputTemplate.getLayoutId(contextName);
    }

    public ContentList getOutputTemplates(String mode) throws CMException {
        return inputTemplate.getOutputTemplates(mode);
    }

    public String getPolicyClassName() {
	try {
	    return inputTemplate.getPolicyClassName();
	} catch (CMException e) {
	    throw new CMRuntimeException("While getting policy class name of " + this + ": " + e.getMessage(), e);
	}
    }

    public ContentList getSubTemplates() throws CMException {
        return inputTemplate.getSubTemplates();
    }

    public ContentId getViewerId(String contextName) throws CMException {
        return inputTemplate.getViewerId(contextName);
    }

    public void setEditorId(String contextName, ContentId outputTemplateId)
            throws CMException {
        inputTemplate.setEditorId(contextName, outputTemplateId);
    }

    public void setLayoutId(String contextName, ContentId outputTemplateId)
            throws CMException {
        inputTemplate.setLayoutId(contextName, outputTemplateId);
    }

    public void setPolicyClassName(String className) throws CMException {
        inputTemplate.setPolicyClassName(className);
    }

    public void setViewerId(String contextName, ContentId outputTemplateId)
            throws CMException {
        inputTemplate.setViewerId(contextName, outputTemplateId);
    }
}
