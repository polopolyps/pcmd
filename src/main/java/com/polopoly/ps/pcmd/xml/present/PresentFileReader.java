package com.polopoly.ps.pcmd.xml.present;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.ps.pcmd.client.Major;
import com.polopoly.ps.pcmd.discovery.FallbackDiscoverer;
import com.polopoly.ps.pcmd.discovery.ImportOrderOrDirectoryFileDiscoverer;
import com.polopoly.ps.pcmd.discovery.NotApplicableException;
import com.polopoly.ps.pcmd.file.DeploymentFile;
import com.polopoly.ps.pcmd.file.DeploymentObject;
import com.polopoly.ps.pcmd.file.FileDeploymentDirectory;
import com.polopoly.ps.pcmd.file.ResourceFile;
import com.polopoly.ps.pcmd.util.CheckedCast;
import com.polopoly.ps.pcmd.util.CheckedClassCastException;
import com.polopoly.ps.pcmd.xml.parser.ContentXmlParser;

public class PresentFileReader {
    private static final Logger logger = Logger.getLogger(PresentFileReader.class.getName());

    private static final String PRESENT_FILES_RESOURCE_DIRECTORY = "/content/";

    public static final String PRESENT_CONTENT_FILE = "presentContent.txt";

    public static final String PRESENT_TEMPLATES_FILE = "presentTemplates.txt";

    private File rootDirectory;

    private PresentContentAware presentFilesAware;

    public PresentFileReader(File rootDirectory, PresentContentAware presentFilesAware) {
        this.rootDirectory = rootDirectory;
        this.presentFilesAware = presentFilesAware;
    }

    public PresentFileReader(PresentContentAware presentFilesAware) {
        this.presentFilesAware = presentFilesAware;
    }

    public void readAndScanContent() {
        read();

        if (rootDirectory != null) {
            scanContent();
        }
    }

    public void read() {
        if (rootDirectory != null) {
            readFromRootDirectory();
        }

        readFromResource();
    }

    protected void scanContent() {
        try {
            new PresentFileReader(rootDirectory, presentFilesAware).read();

            List<DeploymentFile> deploymentFiles = discoverFilesInDirectory(rootDirectory);

            ContentXmlParser parser = new ContentXmlParser();

            PresentContentAwareToParseCallbackAdapter callback =
                new PresentContentAwareToParseCallbackAdapter(presentFilesAware);

            int fileCount = 0;

            for (DeploymentFile deploymentFile : deploymentFiles) {
                parser.parse(deploymentFile, callback);

                if (++fileCount % 100 == 0) {
                    System.err.println("Scanned " + fileCount + " files...");
                }
            }
        } catch (NotApplicableException e) {
            logger.log(Level.INFO, "The directory " + rootDirectory.getAbsolutePath()
                                   + " did not contain any content files.");
        }
    }

    public static List<DeploymentFile> discoverFilesInDirectory(File directory) throws NotApplicableException {
        FallbackDiscoverer discoverer = new ImportOrderOrDirectoryFileDiscoverer(directory);

        return discoverer.getFilesToImport();
    }

    private void readFromResource() {
        for (String directory : new String[] { PRESENT_FILES_RESOURCE_DIRECTORY, "/" }) {
            DeploymentFile presentContentResourceFile = new ResourceFile(directory + PRESENT_CONTENT_FILE);
            readPresentContent(presentContentResourceFile);

            DeploymentFile presentTemplatesResourceFile = new ResourceFile(directory + PRESENT_TEMPLATES_FILE);
            readPresentTemplates(presentTemplatesResourceFile);
        }
    }

    public void readFromRootDirectory() {
        FileDeploymentDirectory directory = new FileDeploymentDirectory(rootDirectory);

        try {
            DeploymentObject presentContentFile = directory.getFile(PRESENT_CONTENT_FILE);
            readPresentContent(presentContentFile);
        } catch (FileNotFoundException e) {
            // fine.
        }

        try {
            DeploymentObject presentTemplatesFile = directory.getFile(PRESENT_TEMPLATES_FILE);
            readPresentTemplates(presentTemplatesFile);
        } catch (FileNotFoundException e) {
            // fine.
        }
    }

    private void readPresentContent(DeploymentObject presentContentFile) {
        readListOfExternalIds(presentContentFile, Major.UNKNOWN);
    }

    private void readPresentTemplates(DeploymentObject presentTemplatesFile) {
        readListOfExternalIds(presentTemplatesFile, Major.INPUT_TEMPLATE);
    }

    private void readListOfExternalIds(DeploymentObject file, Major major) {
        try {
            DeploymentFile presentContent = CheckedCast.cast(file, DeploymentFile.class);

            BufferedReader reader = new BufferedReader(new InputStreamReader(presentContent.getInputStream(), "UTF-8"));

            String line = reader.readLine();

            while (line != null) {
                line = line.trim();

                if (!ignoreLine(line)) {
                    if (major == Major.INPUT_TEMPLATE) {
                        presentFilesAware.presentTemplate(line);
                    } else {
                        presentFilesAware.presentContent(line);
                    }
                }

                line = reader.readLine();
            }

            reader.close();
        } catch (FileNotFoundException e) {
            // ignore
        } catch (CheckedClassCastException e) {
            logger.log(Level.WARNING, file + " does not seem to be an ordinary file.");
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
    }

    private boolean ignoreLine(String line) {
        return line.equals("") || line.startsWith("#");
    }

}
