package com.polopoly.ps.pcmd.xml.present;

import com.polopoly.ps.pcmd.client.Major;
import com.polopoly.ps.pcmd.file.DeploymentFile;
import com.polopoly.ps.pcmd.xml.parser.ParseCallback;
import com.polopoly.ps.pcmd.xml.parser.ParseContext;

public class PresentContentAwareToParseCallbackAdapter implements ParseCallback {
    private PresentContentAware presentFilesAware;

    public PresentContentAwareToParseCallbackAdapter(PresentContentAware presentFilesAware) {
        this.presentFilesAware = presentFilesAware;
    }

    public void classReferenceFound(DeploymentFile file, String string) {
    }

    public void contentFound(ParseContext context, String externalId, Major major, String inputTemplate) {
        if (major == Major.INPUT_TEMPLATE) {
            presentFilesAware.presentTemplate(externalId);
        } else {
            presentFilesAware.presentContent(externalId);
        }
    }

    public void contentReferenceFound(ParseContext context, Major major, String externalId) {
    }
}
