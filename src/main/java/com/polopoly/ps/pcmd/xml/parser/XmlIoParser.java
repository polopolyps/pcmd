package com.polopoly.ps.pcmd.xml.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.polopoly.ps.pcmd.client.Major;
import com.polopoly.ps.pcmd.file.DeploymentFile;
import com.polopoly.ps.pcmd.text.ExternalIdReference;
import com.polopoly.ps.pcmd.text.TextContent;
import com.polopoly.ps.pcmd.text.TextContentSet;

class XmlIoParser extends AbstractParser {
    private static final Logger LOGGER = Logger.getLogger(XmlIoParser.class.getName());
    private boolean readFiles = true;
    private Element root;

    XmlIoParser(DeploymentFile file, Element root, ParseCallback callback) {
        super(file, callback);

        this.root = root;
    }

    TextContentSet parse() {
        return parseBatch(root);
    }

    public void setReadFiles(boolean readFiles) {
        this.readFiles = readFiles;
    }

    private TextContentSet parseBatch(Element contentElement) {
        TextContentSet contentSet = new TextContentSet();

        for (Element content : children(contentElement)) {
            if (content.getNodeName().equals("content")) {
                contentSet.add(parseContent(content));
            } else if (content.getNodeName().equals("batch")) {
                contentSet.addAll(parseBatch(content));
            } else {
                LOGGER.log(Level.WARNING, "Unexpected tag " + content.getNodeName() + " in " + file
                                          + ". Expected \"content\" or \"batch\".");
            }
        }

        return contentSet;
    }

    protected void findContentReferences(ParseContext context, Element content) {
        for (Element child : children(content)) {
            findContentReferences(context, child);
        }

        ParsedContentId contentReference = parseContentId(content);

        if (contentReference != null) {
            callback.contentReferenceFound(context, contentReference.getMajor(), contentReference.getExternalId());
        }
    }

    private TextContent parseContent(Element contentElement) {
        ParseContext context = new ParseContext(file, contentElement);

        TextContent parsedContent = new TextContent();

        for (Element element : children(contentElement)) {
            String nodeName = element.getNodeName();

            if (nodeName.equals("metadata")) {
                parseMetadata(context, element, parsedContent);
            } else {
                if (nodeName.equals("component")) {
                    parsedContent.setComponent(element.getAttribute("group"), element.getAttribute("name"),
                                               element.getTextContent());
                } else if (nodeName.equals("contentref")) {
                    if (!"true".equals(element.getAttribute("clear"))) {
                        parseContentReference(element, parsedContent);
                    }
                } else if (nodeName.equals("file") && readFiles) {
                    if (element.getAttribute("encoding").equals("relative")) {
                        readFile(parsedContent, element.getAttribute("name"), element.getTextContent());
                    } else {
                        // don't want to log this as an error since the caller
                        // might just want the ParseCallback.
                    }
                }

                findContentReferences(context, element);
            }
        }

        return parsedContent;
    }

    private void parseContentReference(Element element, TextContent content) {
        NodeList childNodes = element.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);

            if (childNode instanceof Element) {
                ParsedContentId contentId = parseContentId((Element) childNode);

                content.setReference(element.getAttribute("group"), element.getAttribute("name"),
                                     new ExternalIdReference(contentId.getExternalId()));
            }
        }
    }

    private void readFile(TextContent content, String storeInFile, String readFromFileName) {
        URL baseUrl;
        try {
            baseUrl = file.getBaseUrl();
        } catch (MalformedURLException e) {
            LOGGER.log(Level.WARNING, "Getting base URL from " + file + ": " + e.getMessage(), e);

            return;
        }

        try {
            URL fileUrl;

            if (readFromFileName.startsWith("/")) {
                fileUrl = new URL(baseUrl.getProtocol(), baseUrl.getHost(), baseUrl.getPort(), readFromFileName);
            } else {
                fileUrl = new URL(baseUrl, readFromFileName);
            }

            InputStream stream = fileUrl.openStream();

            try {
                content.addFile(storeInFile, stream, fileUrl);
            } finally {
                try {
                    stream.close();
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "While closing input stream " + fileUrl + ": " + e.getMessage(), e);
                }
            }
        } catch (MalformedURLException e) {
            LOGGER.log(Level.WARNING, "Could not read file " + readFromFileName + " relative to " + baseUrl + ".");
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Could not read file " + readFromFileName + " relative to " + baseUrl + ".");
        }
    }

    private void parseMetadata(ParseContext context, Element metadata, TextContent parsedContent) {
        ParsedContentId contentId = null;
        String inputTemplate = null;
        ParsedContentId securityParentId = null;

        for (Element metadataChild : children(metadata)) {
            String nodeName = metadataChild.getNodeName();

            if (nodeName.equals("input-template")) {
                for (Element inputTemplateElement : children(metadataChild)) {
                    if (inputTemplateElement.getNodeName().equals("externalid")) {
                        inputTemplate = inputTemplateElement.getTextContent().trim();

                        parsedContent.setInputTemplate(new ExternalIdReference(inputTemplate));
                    }
                }
            } else if (nodeName.equals("contentid")) {
                contentId = parseContentId(metadataChild);

                if (contentId != null) {
                    parsedContent.setId(contentId.getExternalId());
                }
            } else if (nodeName.equals("security-parent")) {
                securityParentId = parseContentId(metadataChild);

                if (securityParentId != null) {
                    parsedContent.setSecurityParent(new ExternalIdReference(securityParentId.getExternalId()));
                }
            }
        }

        if (contentId != null) {
            Major major = contentId.getMajor();

            // objects can only be created if the major is specified.
            if (major == Major.UNKNOWN) {
                callback.contentReferenceFound(context, contentId.getMajor(), contentId.getExternalId());
            } else if (contentId.getExternalId() != null) {
                callback.contentFound(context, contentId.getExternalId(), major, inputTemplate);
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
