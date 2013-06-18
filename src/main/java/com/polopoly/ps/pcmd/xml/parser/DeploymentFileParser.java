package com.polopoly.ps.pcmd.xml.parser;

import com.polopoly.ps.pcmd.file.DeploymentFile;
import com.polopoly.ps.pcmd.text.TextContentSet;

public interface DeploymentFileParser {

    TextContentSet parse(DeploymentFile file, ParseCallback callback);

}
