package com.polopoly.ps.pcmd.xml.allcontent;

import java.util.List;

import com.polopoly.ps.pcmd.client.Major;
import com.polopoly.ps.pcmd.file.DeploymentFile;
import com.polopoly.ps.pcmd.xml.parser.ContentXmlParser;
import com.polopoly.ps.pcmd.xml.parser.DeploymentFileParser;
import com.polopoly.ps.pcmd.xml.parser.ParseCallback;
import com.polopoly.ps.pcmd.xml.parser.ParseContext;

public class AllContentFinder {
    public class AllContentFinderParserCallback implements ParseCallback {
        public void classReferenceFound(DeploymentFile file, String string) {
        }

        public void contentFound(ParseContext context, String externalId, Major major, String inputTemplate) {
            result.add(major, externalId);
        }

        public void contentReferenceFound(ParseContext context, Major major, String externalId) {
        }
    }

    private List<DeploymentFile> files;
    private DeploymentFileParser parser;

    private AllContent result = new AllContent();

    public AllContentFinder(List<DeploymentFile> files) {
        this(new ContentXmlParser(), files);
    }

    public AllContentFinder(DeploymentFileParser parser, List<DeploymentFile> files) {
        this.parser = parser;
        this.files = files;
    }

    public AllContent find() {
        AllContentFinderParserCallback callback = new AllContentFinderParserCallback();

        for (DeploymentFile deploymentFile : files) {
            parser.parse(deploymentFile, callback);
        }

        return result;
    }
}
