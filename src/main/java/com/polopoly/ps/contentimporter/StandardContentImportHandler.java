package com.polopoly.ps.contentimporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.cm.xml.DocumentImporter;
import com.polopoly.ps.contentimporter.hotdeploy.text.TextContentParser;
import com.polopoly.ps.contentimporter.hotdeploy.text.TextContentSet;
import com.polopoly.ps.contentimporter.hotdeploy.text.TextContentXmlWriter;

/**
 * Implementation of {@link ContentImportHandler} that uses the Polopoly product
 * code to do the imports. Currently these API:s are not open, so most of the
 * import code is copied into the Testbase project pending a refactoring of the
 * product code.
 * 
 */
public class StandardContentImportHandler implements ContentImportHandler {

    protected static Logger LOGGER = Logger.getLogger(StandardContentImportHandler.class.getName());

    protected static final String WARNING_RESOURCE_SET_WAS_NULL = "The passed set of resources was null";
    protected static final String WARNING_RESOURCE_SET_WAS_EMPTY = "The passed set of resources was empty";
    protected static final String WARNING_RESOURCE_FILE_NOT_FOUND = "Resource file not found: '%1$s'";
    protected static final String WARNING_RESOURCE_TYPE_NOT_SUPPORTED =
        "Resource type not supported: '%1$s'. Supported types are: '.content' and '.xml'";
    protected static final String SEVERE_CONTENT_IMPORT_FAILED = "Content import failed! See error message below:";

    protected static final String INFO_CONTENT_IMPORT_SUCCEDED = "Content import of: '%1$s' succeeded.";

    private final DocumentImporter documentImporter;

    public StandardContentImportHandler(DocumentImporter documentImporter) {
        this.documentImporter = documentImporter;
    }

    public void importContent(Set<URL> resources) {

        if (resources == null) {
            LOGGER.log(Level.WARNING, WARNING_RESOURCE_SET_WAS_NULL);
            System.err.println(WARNING_RESOURCE_SET_WAS_NULL);
        } else if (resources.isEmpty()) {
            LOGGER.log(Level.WARNING, WARNING_RESOURCE_SET_WAS_EMPTY);
            System.err.println(WARNING_RESOURCE_SET_WAS_EMPTY);
        } else {

            for (URL resourceURL : resources) {
                if (isValidResource(resourceURL)) {

                    String fileName = resourceURL.getFile();

                    if (fileName.endsWith(".content")) {

                        try {

                            InputStream inputStream = new FileInputStream(new File(fileName));

                            TextContentParser textContentParser =
                                new TextContentParser(inputStream, resourceURL, fileName);
                            TextContentSet textContentSet = textContentParser.parse();

                            StringWriter writer = new StringWriter();
                            TextContentXmlWriter contentXmlWriter = new TextContentXmlWriter(writer);

                            contentXmlWriter.write(textContentSet);
                            contentXmlWriter.close();

                            String xml = writer.getBuffer().toString();

                            documentImporter.importXML(xml);
                            LOGGER.log(Level.INFO, String.format(INFO_CONTENT_IMPORT_SUCCEDED, fileName));
                            System.out.println(String.format(INFO_CONTENT_IMPORT_SUCCEDED, fileName));

                        } catch (Exception e) {
                            LOGGER.log(Level.SEVERE, SEVERE_CONTENT_IMPORT_FAILED, e);
                            System.err.println(SEVERE_CONTENT_IMPORT_FAILED + e.getMessage());
                        }
                    } else if (fileName.endsWith(".xml")) {
                        try {

                            documentImporter.importXML(new File(fileName));

                            LOGGER.log(Level.INFO, String.format(INFO_CONTENT_IMPORT_SUCCEDED, fileName));
                            System.out.println(String.format(INFO_CONTENT_IMPORT_SUCCEDED, fileName));

                        } catch (Exception e) {
                            LOGGER.log(Level.SEVERE, SEVERE_CONTENT_IMPORT_FAILED, e);
                            System.err.println(SEVERE_CONTENT_IMPORT_FAILED + e.getMessage());
                        }
                    }
                }
            }
        }
    }

    private boolean isValidResource(URL resourceURL) {

        if (resourceURL == null || !(new File(resourceURL.getFile()).exists())) {
            LOGGER.log(Level.WARNING, String.format(WARNING_RESOURCE_FILE_NOT_FOUND,
                                                    resourceURL != null ? resourceURL.toString() : null));
            System.err.println(String.format(WARNING_RESOURCE_FILE_NOT_FOUND,
                                             resourceURL != null ? resourceURL.toString() : null));

            return false;

        } else if (!resourceURL.toString().endsWith(".content") && !resourceURL.toString().endsWith(".xml")) {
            LOGGER.log(Level.WARNING, String.format(WARNING_RESOURCE_TYPE_NOT_SUPPORTED, resourceURL.toString()));
            System.err.println(String.format(WARNING_RESOURCE_TYPE_NOT_SUPPORTED, resourceURL.toString()));
            return false;

        } else {
            return true;
        }
    }
}
