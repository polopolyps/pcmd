package com.polopoly.ps.pcmd.discovery;

import java.util.List;

import com.polopoly.ps.pcmd.file.DeploymentFile;

public interface FileDiscoverer {
    List<DeploymentFile> getFilesToImport() throws NotApplicableException;
}
