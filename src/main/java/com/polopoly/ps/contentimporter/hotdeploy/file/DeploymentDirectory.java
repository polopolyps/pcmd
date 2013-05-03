package com.polopoly.ps.contentimporter.hotdeploy.file;

import java.io.FileNotFoundException;

public interface DeploymentDirectory
    extends DeploymentObject
{
    boolean exists();

    DeploymentObject[] listFiles();
    DeploymentObject getFile(String fileName) throws FileNotFoundException;

    /**
     * Returns the file name of the object relative to this directory, if the file
     * is in a subtree of this directory. getFile of the result of this
     * method should result in the file. If the file is not under this directory,
     * the name is returned.
     */
    String getRelativeName(DeploymentObject deploymentObject);
}
