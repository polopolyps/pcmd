package com.polopoly.ps.pcmd.discovery;

import java.io.File;

import com.polopoly.ps.pcmd.discovery.importorder.ImportOrderFileDiscoverer;
import com.polopoly.ps.pcmd.file.FileDeploymentDirectory;

public class ImportOrderOrDirectoryFileDiscoverer extends FallbackDiscoverer {

    public ImportOrderOrDirectoryFileDiscoverer(File directory) {
        super(new ImportOrderFileDiscoverer(new FileDeploymentDirectory(directory)), new DirectoryFileDiscoverer(
            new FileDeploymentDirectory(directory)));
    }

}
