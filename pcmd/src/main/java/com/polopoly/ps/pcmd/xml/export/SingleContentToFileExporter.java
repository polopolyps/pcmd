package com.polopoly.ps.pcmd.xml.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import com.polopoly.cm.client.ContentRead;

/**
 * Should be renamed; it can now export multiple files.
 */
public interface SingleContentToFileExporter {
    void exportContentToFile(List<? extends ContentRead> content, File file) throws ParserConfigurationException,
        TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException,
        FileNotFoundException, ExportException;

    void exportSingleContentToFile(ContentRead content, File file) throws ParserConfigurationException,
        TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException,
        FileNotFoundException, ExportException;
}
