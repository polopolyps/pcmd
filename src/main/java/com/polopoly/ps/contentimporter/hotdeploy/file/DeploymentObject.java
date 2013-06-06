package com.polopoly.ps.contentimporter.hotdeploy.file;

public interface DeploymentObject
{
    String getName();
    boolean imports(DeploymentObject object);
}
