package com.polopoly.ps.pcmd.tool.xml.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.xml.util.export.DefaultContentContentsExporter;
import com.polopoly.ps.pcmd.xml.export.SingleContentToFileExporter;

public class SingleContentToXMLFileExporter implements SingleContentToFileExporter {
    private static final Logger logger = Logger.getLogger(SingleContentToXMLFileExporter.class.getName());

    private DefaultContentContentsExporter contentsExporter;

    public SingleContentToXMLFileExporter(DefaultContentContentsExporter contentsExporter) {
        this.contentsExporter = contentsExporter;
    }

    @Override
    public void exportContentToFile(List<? extends ContentRead> contents, File file)
        throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerConfigurationException,
        TransformerException, FileNotFoundException {
        Document document = createDocument();
        Element batchElement = createRoot(document);

        for (ContentRead content : contents) {
            Element contentElement = document.createElement("content");
            batchElement.appendChild(contentElement);

            contentsExporter.exportContentContents(contentElement, content);
        }

        writeDocumentToFile(document, file);

        logger.log(Level.INFO, "Wrote " + contents + " to file " + file.getAbsolutePath() + ".");
    }

    public void exportSingleContentToFile(ContentRead content, File file) throws ParserConfigurationException,
        TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException,
        FileNotFoundException {
        exportContentToFile(Collections.singletonList(content), file);
    }

    private void writeDocumentToFile(Document document, File file) throws FileNotFoundException,
        TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(file);

            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(outputStream);
            Transformer serializer = createSerializer();

            serializer.transform(domSource, streamResult);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
            }
        }
    }

    private Transformer createSerializer() throws TransformerFactoryConfigurationError,
        TransformerConfigurationException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();

        serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");

        return serializer;
    }

    private Element createRoot(Document document) {
        Element batchElement = document.createElement("batch");
        batchElement.setAttribute("xmlns", "http://www.polopoly.com/polopoly/cm/xmlio");
        document.appendChild(batchElement);

        return batchElement;
    }

    private Document createDocument() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.newDocument();
        return document;
    }

}
