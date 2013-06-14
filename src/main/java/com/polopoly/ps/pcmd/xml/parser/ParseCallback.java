package com.polopoly.ps.pcmd.xml.parser;

import com.polopoly.ps.pcmd.client.Major;
import com.polopoly.ps.pcmd.file.DeploymentFile;

public interface ParseCallback {
    /**
     * A content object with the specified external ID was defined.
     */
    void contentFound(ParseContext context, String externalId, Major major, String inputTemplate);

    /**
     * A content was referenced.
     */
    void contentReferenceFound(ParseContext context, Major major, String externalId);

    /**
     * A class was referenced.
     */
    void classReferenceFound(DeploymentFile file, String string);
}
