package com.polopoly.ps.pcmd.xml.present;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.ps.pcmd.client.Major;
import com.polopoly.ps.pcmd.xml.allcontent.AllContent;

public class PresentFileWriter {
    private static final Logger LOGGER = Logger.getLogger(PresentFileWriter.class.getName());

    private File rootDirectory;

    public PresentFileWriter(File rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public void writePresentFiles(final AllContent allContent) {
        new PresentFileReader(rootDirectory, new PresentContentAware() {

            public void presentTemplate(String inputTemplate) {
                allContent.add(Major.INPUT_TEMPLATE, inputTemplate);
            }

            public void presentContent(String externalId) {
                allContent.add(Major.UNKNOWN, externalId);
            }
        }).readFromRootDirectory();

        writeContent(allContent);
        writeTemplate(allContent);
    }

    private void writeTemplate(final AllContent allContent) {
        File templateFile = null;

        try {
            templateFile = new File(rootDirectory, PresentFileReader.PRESENT_TEMPLATES_FILE);
            FileWriter templateWriter = new FileWriter(templateFile);

            for (String externalId : allContent.getExternalIds(Major.INPUT_TEMPLATE)) {
                templateWriter.write(externalId);
                templateWriter.write("\n");
            }

            templateWriter.close();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Could not write to "
                                      + (templateFile != null ? templateFile.getAbsoluteFile() : rootDirectory) + ": "
                                      + e.getMessage(), e);
        }
    }

    private void writeContent(final AllContent allContent) {
        File contentFile = null;

        try {
            contentFile = new File(rootDirectory, PresentFileReader.PRESENT_CONTENT_FILE);
            FileWriter contentWriter = new FileWriter(contentFile);

            for (Major major : allContent.getMajors()) {
                if (major == Major.INPUT_TEMPLATE) {
                    continue;
                }

                for (String externalId : allContent.getExternalIds(major)) {
                    contentWriter.write(externalId);
                    contentWriter.write("\n");
                }
            }

            contentWriter.close();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Could not write to "
                                      + (contentFile != null ? contentFile.getAbsoluteFile() : rootDirectory) + ": "
                                      + e.getMessage(), e);
        }
    }
}
