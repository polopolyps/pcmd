package com.polopoly.ps.pcmd.xml.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.polopoly.ps.pcmd.file.DeploymentFile;
import com.polopoly.ps.pcmd.text.ParseException;
import com.polopoly.ps.pcmd.text.TextContentParseCallbackAdapter;
import com.polopoly.ps.pcmd.text.TextContentParser;
import com.polopoly.ps.pcmd.text.TextContentSet;

public class ContentXmlParser implements DeploymentFileParser {
    private static final Logger logger = Logger.getLogger(ContentXmlParser.class.getName());

    public ContentXmlParser() {
    }

    private void handleException(DeploymentFile file, Exception e) {
        logger.log(Level.WARNING, "While parsing " + file + ": " + e.getMessage(), e);
    }

    public TextContentSet parse(DeploymentFile file, ParseCallback callback) {
        return parse(file, callback, false);
    }

    public TextContentSet parse(DeploymentFile file, ParseCallback callback, boolean readFiles) {
        InputStream inputStream = null;
        TextContentSet result = new TextContentSet();

        try {
            inputStream = file.getInputStream();

            if (file.getName().endsWith('.' + TextContentParser.TEXT_CONTENT_FILE_EXTENSION)) {
                ParseContext parseContext = new ParseContext(file);

                TextContentParser parser = new TextContentParser(inputStream, file.getBaseUrl(), file.getName());

                parser.setReadFiles(readFiles);

                result = parser.parse();

                new TextContentParseCallbackAdapter(result).callback(callback, parseContext);
            } else {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(inputStream);

                Element root = document.getDocumentElement();

                String rootName = root.getNodeName();

                if (rootName.equals("template-definition")) {
                    new TemplateDefinitionParser(file, root, callback).parse();
                } else if (rootName.equals("batch")) {
                    XmlIoParser parser = new XmlIoParser(file, root, callback);

                    parser.setReadFiles(readFiles);

                    result = parser.parse();
                } else {
                    logger.log(Level.WARNING, "File " + file + " was of unknown type.");
                }
            }
        } catch (FileNotFoundException e) {
            handleException(file, e);
        } catch (ParserConfigurationException e) {
            handleException(file, e);
        } catch (SAXException e) {
            handleException(file, e);
        } catch (IOException e) {
            handleException(file, e);
        } catch (ParseException e) {
            handleException(file, e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }

        return result;
    }
}
