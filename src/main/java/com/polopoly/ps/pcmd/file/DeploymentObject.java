package com.polopoly.ps.pcmd.file;

public interface DeploymentObject {
    String getName();

    boolean imports(DeploymentObject object);
}
