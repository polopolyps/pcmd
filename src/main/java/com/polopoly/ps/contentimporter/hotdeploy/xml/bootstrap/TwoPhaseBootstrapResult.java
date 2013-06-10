package com.polopoly.ps.contentimporter.hotdeploy.xml.bootstrap;

import com.polopoly.ps.contentimporter.hotdeploy.discovery.importorder.ImportOrder;
import com.polopoly.ps.contentimporter.hotdeploy.xml.bootstrap.Bootstrap;

public class TwoPhaseBootstrapResult
{
    private Bootstrap phaseOne;
    private Bootstrap phaseTwo;
    private ImportOrder importOrder;

    public TwoPhaseBootstrapResult(final Bootstrap phaseOne,
                                   final Bootstrap phaseTwo,
                                   final ImportOrder importOrder)
    {
        this.phaseOne = phaseOne;
        this.phaseTwo = phaseTwo;
        this.importOrder = importOrder;
    }

    public Bootstrap getTemplateBootstrap()
    {
        return phaseOne;
    }

    public Bootstrap getContentBootstrap()
    {
        return phaseTwo;
    }

    public ImportOrder getImportOrder()
    {
        return importOrder;
    }
}
