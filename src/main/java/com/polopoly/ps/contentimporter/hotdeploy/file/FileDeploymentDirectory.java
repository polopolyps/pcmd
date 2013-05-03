package com.polopoly.ps.contentimporter.hotdeploy.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileDeploymentDirectory extends AbstractDeploymentObject implements DeploymentDirectory {
    private static final Logger logger = Logger.getLogger(FileDeploymentDirectory.class.getName());
    private File file;

    public FileDeploymentDirectory(File file) {
        this.file = file;
    }

    public boolean exists() {
        return file.exists() && file.canRead();
    }

    public DeploymentObject getFile(String fileName) throws FileNotFoundException {
        File newFile;

        if (fileName.equals(".")) {
            return this;
        }

        // we always support forward slashes, even on windows
        if (File.separatorChar != '/') {
            fileName = fileName.replace('/', File.separatorChar);
        }

        if (new File(fileName).isAbsolute()) {
            newFile = new File(fileName);
        } else {
            newFile = new File(file, fileName);
        }

        if (!newFile.exists()) {
            throw new FileNotFoundException("File " + newFile.getAbsolutePath() + " does not exist.");
        }

        if (newFile.isDirectory()) {
            return new FileDeploymentDirectory(newFile);
        } else {
            return new FileDeploymentFile(newFile);
        }

    }

    public DeploymentObject[] listFiles() {
        File[] files = file.listFiles();

        // happens if the file is not a directory.
        if (files == null) {
            return new DeploymentObject[0];
        }

        List<DeploymentObject> result = new ArrayList<DeploymentObject>(files.length);

        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().startsWith(".")) {
                continue;
            }

            try {
                result.add(getFile(files[i].getName()));
            } catch (FileNotFoundException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                System.err.println(e.getMessage());
            }
        }

        return result.toArray(new DeploymentObject[result.size()]);
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return file.getAbsolutePath();
    }

    public String getRelativeName(DeploymentObject deploymentObject) {
        if (deploymentObject instanceof FileDeploymentFile || deploymentObject instanceof FileDeploymentDirectory) {
            String fileName = deploymentObject.getName();
            String directoryName = getName();

            if (fileName.startsWith(directoryName + File.separator)) {
                String result = fileName.substring(directoryName.length() + 1);

                // the canonical form is using forward slashes.
                // the import order will have slashes in the wrong direction
                // otherwise.
                if (File.separatorChar != '/') {
                    result = result.replace(File.separatorChar, '/');
                }

                return result;
            }

            if (deploymentObject.equals(this)) {
                return ".";
            }
        }

        return deploymentObject.getName();
    }

    public boolean imports(DeploymentObject object) {
        if (object instanceof FileDeploymentDirectory || object instanceof FileDeploymentFile) {
            return object.equals(this) || object.getName().startsWith(getName() + File.separator);
        }

        return false;
    }
}
