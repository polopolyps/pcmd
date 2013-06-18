package com.polopoly.ps.pcmd.discovery.importorder;

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

import com.polopoly.ps.pcmd.file.DeploymentDirectory;
import com.polopoly.ps.pcmd.file.DeploymentFile;
import com.polopoly.ps.pcmd.file.DeploymentObject;
import com.polopoly.ps.pcmd.file.FileDeploymentDirectory;
import com.polopoly.ps.pcmd.file.FileDeploymentFile;

public class ImportOrderFileParser {
    private static final Logger logger = Logger.getLogger(ImportOrderFileParser.class.getName());

    private static final Set<String> NON_EXISTING_FILES = new HashSet<String>();
    static final String DEPENDENCY_PREFIX = "depends:";

    private DeploymentFile importOrderFileAsDeploymentFile;
    private ImportOrderFile importOrderFile;

    private DeploymentDirectory directory;

    public ImportOrderFileParser(DeploymentDirectory directory, DeploymentFile importOrderFileAsDeploymentFile) {
        this.importOrderFileAsDeploymentFile = importOrderFileAsDeploymentFile;
        this.directory = directory;
        importOrderFile = new ImportOrderFile(directory);
    }

    public ImportOrderFileParser(File importOrderJavaIoFile) {
        this.importOrderFileAsDeploymentFile = new FileDeploymentFile(importOrderJavaIoFile);
        this.directory = new FileDeploymentDirectory(importOrderJavaIoFile.getParentFile());
        importOrderFile = new ImportOrderFile(directory);
    }

    public ImportOrderFile parse() throws IOException {
        InputStream is = importOrderFileAsDeploymentFile.getInputStream();

        logger.log(Level.FINE, "Reading import order from " + importOrderFileAsDeploymentFile + ".");

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

    private boolean isDependencyDeclaration(String line) {
        return line.startsWith(DEPENDENCY_PREFIX);
    }

    private void parseDependency(String line) {
        importOrderFile.addDependency(line.substring(DEPENDENCY_PREFIX.length()).trim());
    }

    private boolean isLineThatShouldBeSkipped(String line) {
        return line.length() == 0 || line.charAt(0) == '#' || line.trim().equals("");
    }

    private boolean isNotAFile(String line) {
        return line.indexOf(':') != -1 || line.indexOf('?') != -1 || line.indexOf('*') != -1;
    }

    private void parseFileLine(String line) {
        try {
            DeploymentObject file = directory.getFile(line);

            importOrderFile.addDeploymentObject(file);
        } catch (FileNotFoundException e) {
            if (NON_EXISTING_FILES.add(line)) {
                logger.log(Level.WARNING, "The directory or file specified as \"" + line + "\" in "
                                          + importOrderFileAsDeploymentFile + " does not exist: " + e.getMessage());
            }
        }
    }

    private void warnNotAValidLine(String line) {
        logger
            .log(Level.WARNING,
                 "The line \""
                     + line
                     + "\" in "
                     + importOrderFileAsDeploymentFile
                     + " does not seem to be a file name. Note that wildcards are not permitted (but whole directories are).");
    }

    private void parseLine(String line) {
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
