package com.polopoly.ps.pcmd.xml.parser;

import org.w3c.dom.Element;

import com.polopoly.ps.pcmd.file.DeploymentFile;

public class ParseContext {
    private DeploymentFile file;
    private Element xmlElement;

    public ParseContext(DeploymentFile file) {
        this.file = file;
    }

    ParseContext(DeploymentFile file, Element contentElement) {
        this(file);
        this.xmlElement = contentElement;
    }

    public DeploymentFile getFile() throws ContextNotAvailableException {
        if (file == null) {
            throw new ContextNotAvailableException();
        }

        return file;
    }

    public void setFile(DeploymentFile file) {
        this.file = file;
    }

    public Element getXmlElement() throws ContextNotAvailableException {
        if (xmlElement == null) {
            throw new ContextNotAvailableException();
        }

        return xmlElement;
    }

    public void setXmlElement(Element xmlElement) {
        this.xmlElement = xmlElement;
    }
}
