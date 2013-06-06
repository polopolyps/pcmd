package com.polopoly.ps.contentimporter.hotdeploy.xml.bootstrap;

import com.polopoly.ps.contentimporter.hotdeploy.xml.bootstrap.BootstrapContent;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.ParseCallback;

public interface BootstrapGatherer
    extends ParseCallback
{
    public abstract Iterable<BootstrapContent> getBootstrapContent();
    public abstract boolean isDefined(String externalId);
}