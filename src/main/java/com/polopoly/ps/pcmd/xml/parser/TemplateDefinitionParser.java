package com.polopoly.ps.pcmd.xml.parser;

import static com.polopoly.ps.pcmd.client.Major.INPUT_TEMPLATE;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.polopoly.ps.pcmd.client.Major;
import com.polopoly.ps.pcmd.file.DeploymentFile;
import com.polopoly.ps.pcmd.util.CheckedCast;
import com.polopoly.ps.pcmd.util.CheckedClassCastException;

class TemplateDefinitionParser extends AbstractParser {
    private static final Logger logger = Logger.getLogger(TemplateDefinitionParser.class.getName());
    private Element root;

    TemplateDefinitionParser(DeploymentFile file, Element root, ParseCallback callback) {
        super(file, callback);

        this.root = root;
    }

    public void parse() {
        for (Element inputTemplate : children(root)) {
            if (inputTemplate.getNodeName().equals("input-template")) {
                parseInputTemplate(inputTemplate);
            } else if (inputTemplate.getNodeName().equals("output-template")) {
                parseOutputTemplate(file, callback, inputTemplate);
            }
        }
    }

    private void parseOutputTemplate(DeploymentFile file, ParseCallback callback, Element outputTemplate) {
        String outputTemplatesInputTemplate = outputTemplate.getAttribute("input-template").trim();

        String outputTemplateName = outputTemplate.getAttribute("name").trim();

        ParseContext context = new ParseContext(file, outputTemplate);

        callback.contentFound(context, outputTemplateName, Major.OUTPUT_TEMPLATE, outputTemplatesInputTemplate);

        String policy = outputTemplate.getAttribute("policy").trim();

        if (!policy.equals("")) {
            callback.classReferenceFound(file, policy);
        }

        // it is apparently possible and legal for output templates not to have
        // an input template.
        if (!outputTemplatesInputTemplate.equals("")) {
            callback.contentReferenceFound(context, INPUT_TEMPLATE, outputTemplatesInputTemplate);
        }
    }

    private void parseInputTemplate(Element inputTemplate) {
        String name = inputTemplate.getAttribute("name");

        ParseContext context = new ParseContext(file, inputTemplate);

        callback.contentFound(context, name, INPUT_TEMPLATE, null);

        parseTemplate(context, inputTemplate, name);
    }

    private void parseTemplate(ParseContext context, Element inputTemplate, String name) {
        for (Element field : children(inputTemplate)) {
            String nodeName = field.getNodeName();

            if (nodeName.equals("content-list")) {
                parseContentList(context, field);
            } else if (nodeName.equals("policy") || nodeName.equals("viewer") || nodeName.equals("editor")) {
                callback.classReferenceFound(file, field.getTextContent().trim());
            } else if (nodeName.equals("layout") || nodeName.equals("field")) {
                parseFieldOrLayout(context, name, field);
            } else if (nodeName.equals("content-list-wrapper")) {
                String wrapperClass = field.getTextContent().trim();

                callback.classReferenceFound(file, wrapperClass);
            } else if (nodeName.equals("output-templates")) {
                for (Element templateId : children(field)) {
                    if (templateId.getNodeName().equals("id")) {
                        callback.contentReferenceFound(context, Major.OUTPUT_TEMPLATE, templateId.getTextContent()
                            .trim());
                    }
                }
            } else if (nodeName.equals("idparam")) {
                parseContentIdReference(context, field);
            } else if (nodeName.equals("idparam-list")) {
                for (Element id : children(field)) {
                    parseContentIdReference(context, id);
                }
            }
        }
    }

    private void parseFieldOrLayout(ParseContext context, String name, Element field) {
        String fieldTemplate = field.getAttribute("input-template");

        if (fieldTemplate.equals("")) {
            logger.log(Level.WARNING, "The field " + field.getAttribute("name") + " in " + file
                                      + " has no input template.");
        } else {
            callback.contentReferenceFound(context, INPUT_TEMPLATE, fieldTemplate);
        }

        for (Element param : children(field)) {
            if (param.getNodeName().equals("param")) {
                String paramName = param.getAttribute("name");

                if (paramName.equals("inputTemplateId")) {
                    callback.contentReferenceFound(context, INPUT_TEMPLATE, param.getTextContent().trim());
                } else if (paramName.endsWith(".class")) {
                    callback.classReferenceFound(file, param.getTextContent().trim());
                }
            } else if (param.getNodeName().equals("idparam-list")) {
                NodeList ids = param.getChildNodes();

                for (int k = 0; k < ids.getLength(); k++) {
                    try {
                        Element id = CheckedCast.cast(ids.item(k), Element.class);

                        parseContentIdReference(context, id);
                    } catch (CheckedClassCastException e) {
                    }
                }
            } else if (param.getNodeName().equals("idparam")) {
                parseContentIdReference(context, param);
            }
        }

        parseTemplate(context, field, name);
    }

    private void parseContentList(ParseContext context, Element field) {
        try {
            Node namedItem = field.getAttributes().getNamedItem("input-template");

            if (namedItem != null) {
                String templateName = namedItem.getNodeValue();

                if (templateName != null && !templateName.equals("")) {
                    callback.contentReferenceFound(context, INPUT_TEMPLATE, templateName);
                }
            }
        } catch (DOMException e) {
        }
    }

    private void parseContentIdReference(ParseContext context, Element param) {
        ParsedContentId contentId = parseContentId(param);

        if (contentId == null) {
            return;
        }

        callback.contentReferenceFound(context, contentId.getMajor(), contentId.getExternalId());
    }
}
