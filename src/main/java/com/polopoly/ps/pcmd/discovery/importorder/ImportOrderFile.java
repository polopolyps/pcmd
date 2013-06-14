package com.polopoly.ps.pcmd.discovery.importorder;

import static com.polopoly.ps.pcmd.discovery.importorder.ImportOrderFileDiscoverer.IMPORT_ORDER_FILE_NAME;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.polopoly.ps.pcmd.file.DeploymentDirectory;
import com.polopoly.ps.pcmd.file.DeploymentFile;
import com.polopoly.ps.pcmd.file.DeploymentObject;
import com.polopoly.ps.pcmd.file.FileDeploymentDirectory;

public class ImportOrderFile implements Iterable<DeploymentObject> {
    private List<DeploymentObject> filesAndDirectories = new ArrayList<DeploymentObject>();
    private List<String> dependencies = new ArrayList<String>();
    private DeploymentDirectory directory;

    public ImportOrderFile(ImportOrder importOrder) {
        this(importOrder.getDirectory());

        for (DeploymentFile deploymentFile : importOrder) {
            addDeploymentObject(deploymentFile);
        }
    }

    public ImportOrderFile(File directory) {
        this.directory = new FileDeploymentDirectory(directory);
    }

    public ImportOrderFile(DeploymentDirectory directory) {
        this.directory = directory;
    }

    public void addDependency(String dependency) {
        dependencies.add(dependency);
    }

    public void addDeploymentObject(int index, DeploymentObject deploymentObject) {
        filesAndDirectories.add(index, deploymentObject);
    }

    public void addDeploymentObject(DeploymentObject deploymentObject) {
        filesAndDirectories.add(deploymentObject);
    }

    public void removeDeploymentObject(DeploymentObject deploymentObject) {
        filesAndDirectories.remove(deploymentObject);
    }

    public Iterator<DeploymentObject> iterator() {
        return filesAndDirectories.iterator();
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public DeploymentDirectory getDirectory() {
        return directory;
    }

    public boolean imports(DeploymentObject fileOrDirectory) {
        for (DeploymentObject deploymentObject : this) {
            if (deploymentObject.imports(fileOrDirectory)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        try {
            return getFile().getAbsolutePath();
        } catch (IOException e) {
            return directory + "/" + IMPORT_ORDER_FILE_NAME;
        }
    }

    public File getFile() throws IOException {
        DeploymentDirectory directory = getDirectory();

        if (!(directory instanceof FileDeploymentDirectory)) {
            throw new IOException("Cannot write to directory " + directory
                                  + " since it doesn't seem to be a standard directory.");
        }

        File fileDirectory = ((FileDeploymentDirectory) directory).getFile();

        return new File(fileDirectory, IMPORT_ORDER_FILE_NAME);
    }
}
