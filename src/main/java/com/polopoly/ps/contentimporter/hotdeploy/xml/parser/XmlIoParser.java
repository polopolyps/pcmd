package com.polopoly.ps.contentimporter.hotdeploy.xml.parser;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import com.polopoly.ps.contentimporter.hotdeploy.client.Major;
import com.polopoly.ps.contentimporter.hotdeploy.file.DeploymentFile;

public class XmlIoParser
    extends AbstractParser
{
    private static final Logger logger = Logger.getLogger(XmlIoParser.class.getName());

    XmlIoParser(final DeploymentFile file,
                final Element root,
                final ParseCallback callback)
    {
        super(file, callback);
        parseBatch(root);
    }

    private void parseBatch(final Element contentElement)
    {
        for (Element content : children(contentElement)) {
            if (content.getNodeName().equals("content")) {
                parseContent(content);
            } else if (content.getNodeName().equals("batch")) {
                parseBatch(content);
            } else {
                logger.log(Level.WARNING, "Unexpected tag " + content.getNodeName() +
                                          " in " + file + ". Expected \"content\" or \"batch\".");
            }
        }
    }

    private void parseContent(final Element contentElement)
    {
        ParseContext context = new ParseContext(file, contentElement);

        for (Element element : children(contentElement)) {
            if (element.getNodeName().equals("metadata")) {
                parseMetadata(context, element);
            } else {
                findContentReferences(context, element);
            }
        }
    }

    private void parseMetadata(final ParseContext context,
                               final Element metadata)
    {
        ParsedContentId contentId = null;
        String inputTemplate = null;
        ParsedContentId securityParentId = null;

        for (Element metadataChild : children(metadata)) {
            String nodeName = metadataChild.getNodeName();

            if (nodeName.equals("input-template")) {
                for (Element inputTemplateElement : children(metadataChild)) {
                    if (inputTemplateElement.getNodeName().equals("externalid")) {
                        inputTemplate = inputTemplateElement.getTextContent().trim();
                    }
                }
            } else if (nodeName.equals("contentid")) {
                contentId = parseContentId(metadataChild);
            } else if (nodeName.equals("security-parent")) {
                securityParentId = parseContentId(metadataChild);
            }
        }

        if (contentId != null) {
            Major major = contentId.getMajor();

            // objects can only be created if the major is specified.
            if (major == Major.UNKNOWN) {
                callback.contentReferenceFound(context, contentId.getMajor(), contentId.getExternalId());
            } else if (contentId.getExternalId() != null) {
                callback.contentFound(context,
                        contentId.getExternalId(), major, inputTemplate);
            }
        }

        if (securityParentId != null) {
            callback.contentReferenceFound(context, securityParentId.getMajor(), securityParentId.getExternalId());
        }

        if (inputTemplate != null) {
            callback.contentReferenceFound(context, Major.INPUT_TEMPLATE, inputTemplate);
        }
    }
}
