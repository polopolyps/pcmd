package com.polopoly.ps.pcmd.xml.present;

public class PresentContentAwareSplitter implements PresentContentAware {
    private PresentContentAware[] delegates;

    public PresentContentAwareSplitter(PresentContentAware... delegates) {
        this.delegates = delegates;
    }

    public void presentContent(String externalId) {
        for (PresentContentAware delegate : delegates) {
            delegate.presentContent(externalId);
        }
    }

    public void presentTemplate(String inputTemplate) {
        for (PresentContentAware delegate : delegates) {
            delegate.presentTemplate(inputTemplate);
        }
    }

}
