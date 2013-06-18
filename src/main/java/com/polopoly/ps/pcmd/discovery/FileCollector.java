package com.polopoly.ps.pcmd.discovery;

import java.util.List;

import com.polopoly.ps.pcmd.file.DeploymentFile;

public interface FileCollector {

    void collect(List<DeploymentFile> result);

}
