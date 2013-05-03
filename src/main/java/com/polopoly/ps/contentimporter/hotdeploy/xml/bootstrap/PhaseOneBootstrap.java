package com.polopoly.ps.contentimporter.hotdeploy.xml.bootstrap;
import java.util.Set;

import com.polopoly.ps.contentimporter.hotdeploy.xml.bootstrap.Bootstrap;

@SuppressWarnings("serial")
public class PhaseOneBootstrap
    extends Bootstrap
{
    private Set<String> definedTemplateExternalIds;

    public void setDefinedTemplateExternalIds(final Set<String> definedTemplateExternalIds)
    {
        this.definedTemplateExternalIds = definedTemplateExternalIds;
    }

    public Set<String> getDefinedTemplateExternalIds()
    {
        return definedTemplateExternalIds;
    }
}