package com.polopoly.ps.contentimporter.hotdeploy.xml.parser;

import org.w3c.dom.Element;

import com.polopoly.ps.contentimporter.hotdeploy.file.DeploymentFile;

public class ParseContext
{
    private DeploymentFile file;
    private Element xmlElement;

    public ParseContext(final DeploymentFile file)
    {
        this.file = file;
    }

    ParseContext(final DeploymentFile file,
                 final Element contentElement)
    {
        this(file);
        this.xmlElement = contentElement;
    }

    public DeploymentFile getFile()
        throws ContextNotAvailableException
    {
        if (file == null) {
            throw new ContextNotAvailableException();
        }

        return file;
    }

    public void setFile(final DeploymentFile file)
    {
        this.file = file;
    }

    public Element getXmlElement()
        throws ContextNotAvailableException
    {
        if (xmlElement == null) {
            throw new ContextNotAvailableException();
        }

        return xmlElement;
    }

    public void setXmlElement(final Element xmlElement)
    {
        this.xmlElement = xmlElement;
    }
}
