package com.polopoly.ps.contentimporter.hotdeploy.xml.bootstrap;

import java.util.ArrayList;
import java.util.List;

import com.polopoly.ps.contentimporter.hotdeploy.client.Major;

@SuppressWarnings("serial")
public class Bootstrap extends ArrayList<BootstrapContent>
{
    private List<BootstrapContent> neverCreatedButReferenced = new ArrayList<BootstrapContent>();

    public void addNeverCreatedButReferenced(final BootstrapContent bootstrapContent)
    {
        neverCreatedButReferenced.add(bootstrapContent);
    }

    public List<BootstrapContent> getNeverCreatedButReferenced()
    {
        return neverCreatedButReferenced;
    }

    public void presentContent(final String externalId)
    {
        BootstrapContent bootstrap = new BootstrapContent(Major.UNKNOWN, externalId);

        remove(bootstrap);
        neverCreatedButReferenced.remove(bootstrap);
    }

    public void presentTemplate(final String inputTemplate)
    {
        BootstrapContent bootstrap = new BootstrapContent(Major.INPUT_TEMPLATE, inputTemplate);

        remove(bootstrap);
        neverCreatedButReferenced.remove(bootstrap);
    }
}
