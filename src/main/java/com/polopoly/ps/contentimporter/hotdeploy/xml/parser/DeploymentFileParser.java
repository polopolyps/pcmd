package com.polopoly.ps.contentimporter.hotdeploy.xml.parser;

import com.polopoly.ps.contentimporter.hotdeploy.file.DeploymentFile;

public interface DeploymentFileParser
{
    void parse(DeploymentFile file, ParseCallback callback);
}
