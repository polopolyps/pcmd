package com.polopoly.ps.contentimporter.hotdeploy.xml.parser;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.polopoly.ps.contentimporter.hotdeploy.client.Major;
import com.polopoly.ps.contentimporter.hotdeploy.file.DeploymentFile;
import com.polopoly.ps.contentimporter.hotdeploy.util.CheckedCast;
import com.polopoly.ps.contentimporter.hotdeploy.util.CheckedClassCastException;
import com.polopoly.ps.contentimporter.hotdeploy.util.FetchingIterator;

public abstract class AbstractParser
{
    private static final Logger logger = Logger.getLogger(AbstractParser.class.getName());

    protected ParseCallback callback;
    protected DeploymentFile file;

    protected AbstractParser(DeploymentFile file, ParseCallback callback)
    {
        this.callback = callback;
        this.file = file;
    }

    protected ParsedContentId parseContentId(Element contentIdElement)
    {
        Major major = null;
        String majorName = null;
        String externalId = null;

        for (Element externalIdElement : children(contentIdElement)) {
            if (externalIdElement.getNodeName().equals("major")) {
                majorName = externalIdElement.getTextContent().trim();

                major = Major.getMajor(majorName);
            }
            else if (externalIdElement.getNodeName().equals("externalid")) {
                externalId = externalIdElement.getTextContent().trim();
            }
        }

        if (externalId != null && !externalId.equals("")) {
            if (major == Major.UNKNOWN) {
                logger.log(Level.SEVERE, "The major \"" + majorName +
                        "\" used to reference the object with external ID \"" + externalId + "\" in file " + file + " was unknown.");
                throw new UnknownMajorException();
            }

            return new ParsedContentId(major, externalId);
        }
        else {
            return null;
        }
    }

    protected void findContentReferences(ParseContext context, Element content)
    {
        for (Element child : children(content)) {
            findContentReferences(context, child);
        }

        ParsedContentId contentReference = parseContentId(content);

        if (contentReference != null) {
            callback.contentReferenceFound(context,
                contentReference.getMajor(), contentReference.getExternalId());
        }
    }

    protected Iterable<Element> children(Element element)
    {
        final NodeList metadataChildren = element.getChildNodes();

        return new Iterable<Element>() {
            public Iterator<Element> iterator() {
                return new FetchingIterator<Element>() {
                    int j = 0;

                    @Override
                    protected Element fetch() {
                        if (j < metadataChildren.getLength()) {
                            try {
                                return CheckedCast.cast(metadataChildren.item(j++), Element.class);
                            } catch (CheckedClassCastException e) {
                                return fetch();
                            }
                        }
                        else {
                            return null;
                        }
                    }};
            }
        };
    }

    protected boolean isMajorInputTemplate(String major)
    {
        return major.equalsIgnoreCase("inputtemplate") ||
                major.equalsIgnoreCase("14");
    }
}
