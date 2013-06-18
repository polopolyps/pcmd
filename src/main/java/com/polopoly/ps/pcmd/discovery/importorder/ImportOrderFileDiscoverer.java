package com.polopoly.ps.pcmd.discovery.importorder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.ps.pcmd.discovery.FileDiscoverer;
import com.polopoly.ps.pcmd.discovery.NotApplicableException;
import com.polopoly.ps.pcmd.file.DeploymentDirectory;
import com.polopoly.ps.pcmd.file.DeploymentFile;
import com.polopoly.ps.pcmd.file.DeploymentObject;
import com.polopoly.ps.pcmd.file.FileDeploymentDirectory;
import com.polopoly.ps.pcmd.text.TextContentParser;
import com.polopoly.ps.pcmd.util.CheckedCast;
import com.polopoly.ps.pcmd.util.CheckedClassCastException;

public class ImportOrderFileDiscoverer implements FileDiscoverer {
    private static final Logger logger = Logger.getLogger(ImportOrderFileDiscoverer.class.getName());

    public static final String IMPORT_ORDER_FILE_NAME = "_import_order";
    private static final String XML_EXTENSION = ".xml";
    private static final String TEXT_EXTENSION = '.' + TextContentParser.TEXT_CONTENT_FILE_EXTENSION;

    private DeploymentDirectory directory;

    public ImportOrderFileDiscoverer(DeploymentDirectory directory) {
        this.directory = directory;
    }

    public ImportOrderFileDiscoverer(File directory) {
        this.directory = new FileDeploymentDirectory(directory);
    }

    public ImportOrder getFilesToImport() throws NotApplicableException {
        if (directory == null) {
            throw new NotApplicableException("No directory available.");
        }

        ImportOrder importOrderResult = new ImportOrder(directory);

        ImportOrderFile parsedFile;

        try {
            DeploymentFile importOrderFileAsDeploymentFile =
                CheckedCast.cast(directory.getFile(IMPORT_ORDER_FILE_NAME), DeploymentFile.class);

            parsedFile = new ImportOrderFileParser(directory, importOrderFileAsDeploymentFile).parse();
        } catch (FileNotFoundException e) {
            throw new NotApplicableException("The import order file could not be read: " + e.getMessage());
        } catch (IOException e) {
            throw new NotApplicableException("Could not read file " + IMPORT_ORDER_FILE_NAME + " in " + directory + ".");
        } catch (CheckedClassCastException e) {
            throw new NotApplicableException("The import order file " + IMPORT_ORDER_FILE_NAME + " in " + directory
                                             + " is not an ordinary file.");
        }

        for (DeploymentObject file : parsedFile) {
            if (file instanceof DeploymentDirectory) {
                addDirectory(importOrderResult, (DeploymentDirectory) file);
            } else if (file instanceof DeploymentFile) {
                addFile(importOrderResult, (DeploymentFile) file);
            }
        }

        for (String dependency : parsedFile.getDependencies()) {
            importOrderResult.addDependency(dependency);
        }

        logger.log(Level.INFO, "Found " + importOrderResult.size() + " content file(s) in " + directory + ".");

        return importOrderResult;
    }

    private static int addDirectory(ArrayList<DeploymentFile> list, DeploymentDirectory directory) {
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "Adding whole directory " + directory + ".");
        }

        DeploymentObject[] files = directory.listFiles();

        Arrays.sort(files);

        int fileCount = 0;

        for (DeploymentObject file : files) {
            if (file instanceof DeploymentDirectory) {
                fileCount += addDirectory(list, (DeploymentDirectory) file);
            } else if (file instanceof DeploymentFile) {
                fileCount += addFile(list, (DeploymentFile) file);
            }
        }

        if (fileCount == 0) {
            logger.log(Level.FINE, "The directory " + directory + " did not contain any importable files.");
        } else {
            logger.log(Level.FINE, "Added directory '" + directory + "' with " + fileCount + " file(s).");
        }

        return fileCount;
    }

    private static int addFile(ArrayList<DeploymentFile> list, DeploymentFile file) {
        int result = 0;

        if (isContent(file) && !list.contains(file)) {
            if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST, "Adding file " + file + ".");
            }

            list.add(file);
            result++;
        }

        return result;
    }

    private static boolean isContent(DeploymentFile file) {
        return file.getName().endsWith(XML_EXTENSION) || file.getName().endsWith(TEXT_EXTENSION);
    }

    @Override
    public String toString() {
        return "files specified in " + IMPORT_ORDER_FILE_NAME + " in " + directory;
    }
}
