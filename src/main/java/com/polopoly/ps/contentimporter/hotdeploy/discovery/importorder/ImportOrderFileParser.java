package com.polopoly.ps.contentimporter.hotdeploy.discovery.importorder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.ps.contentimporter.hotdeploy.file.DeploymentDirectory;
import com.polopoly.ps.contentimporter.hotdeploy.file.DeploymentFile;
import com.polopoly.ps.contentimporter.hotdeploy.file.DeploymentObject;
import com.polopoly.ps.contentimporter.hotdeploy.file.FileDeploymentDirectory;
import com.polopoly.ps.contentimporter.hotdeploy.file.FileDeploymentFile;

public class ImportOrderFileParser {
    private static final Logger logger = Logger.getLogger(ImportOrderFileParser.class.getName());

    private static final Set<String> NON_EXISTING_FILES = new HashSet<String>();
    static final String DEPENDENCY_PREFIX = "depends:";

    private DeploymentFile importOrderFileAsDeploymentFile;
    private ImportOrderFile importOrderFile;

    private DeploymentDirectory directory;

    public ImportOrderFileParser(final DeploymentDirectory directory,
                                 final DeploymentFile importOrderFileAsDeploymentFile) {
        this.importOrderFileAsDeploymentFile = importOrderFileAsDeploymentFile;
        this.directory = directory;

        importOrderFile = new ImportOrderFile(directory);
    }

    public ImportOrderFileParser(final File importOrderJavaIoFile) {
        this.importOrderFileAsDeploymentFile = new FileDeploymentFile(importOrderJavaIoFile);
        this.directory = new FileDeploymentDirectory(importOrderJavaIoFile.getParentFile());

        importOrderFile = new ImportOrderFile(directory);
    }

    public ImportOrderFile parse() throws IOException {
        InputStream is = importOrderFileAsDeploymentFile.getInputStream();

        logger.log(Level.FINE, "Reading import order from " + importOrderFileAsDeploymentFile + ".");
        System.out.println("Reading import order from " + importOrderFileAsDeploymentFile + ".");

        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

        try {
            while (true) {
                String line = br.readLine();

                if (line == null) {
                    break; // End of file.
                }

                parseLine(line);
            }
        } finally {
            br.close();
        }

        return importOrderFile;
    }

    private boolean isDependencyDeclaration(final String line) {
        return line.startsWith(DEPENDENCY_PREFIX);
    }

    private void parseDependency(final String line) {
        importOrderFile.addDependency(line.substring(DEPENDENCY_PREFIX.length()).trim());
    }

    private boolean isLineThatShouldBeSkipped(final String line) {
        return line.length() == 0 || line.charAt(0) == '#' || line.trim().equals("");
    }

    private boolean isNotAFile(final String line) {
        return line.indexOf(':') != -1 || line.indexOf('?') != -1 || line.indexOf('*') != -1;
    }

    private void parseFileLine(final String line) {
        try {
            DeploymentObject file = directory.getFile(line);
            importOrderFile.addDeploymentObject(file);
        } catch (FileNotFoundException e) {
            if (NON_EXISTING_FILES.add(line)) {
                logger.log(Level.WARNING, "The directory or file specified as \"" + line + "\" in "
                                          + importOrderFileAsDeploymentFile + " does not exist: " + e.getMessage());
                System.out.println("The directory or file specified as \"" + line + "\" in "
                                   + importOrderFileAsDeploymentFile + " does not exist: " + e.getMessage());
            }
        }
    }

    private void warnNotAValidLine(final String line) {
        logger.log(Level.WARNING, "The line \"" + line + "\" in " + importOrderFileAsDeploymentFile
                                  + " does not seem to be a file name. Note that wildcards are not permitted.");
        System.out.println("The line \"" + line + "\" in " + importOrderFileAsDeploymentFile
                           + " does not seem to be a file name. Note that wildcards are not permitted.");
    }

    private void parseLine(final String line) {
        if (isLineThatShouldBeSkipped(line)) {
        } else if (isDependencyDeclaration(line)) {
            parseDependency(line);
        } else if (isNotAFile(line)) {
            warnNotAValidLine(line);
        } else {
            parseFileLine(line);
        }
    }
}
