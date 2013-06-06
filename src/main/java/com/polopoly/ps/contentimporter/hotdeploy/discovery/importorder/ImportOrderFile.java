package com.polopoly.ps.contentimporter.hotdeploy.discovery.importorder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.polopoly.ps.contentimporter.hotdeploy.file.DeploymentDirectory;
import com.polopoly.ps.contentimporter.hotdeploy.file.DeploymentFile;
import com.polopoly.ps.contentimporter.hotdeploy.file.DeploymentObject;
import com.polopoly.ps.contentimporter.hotdeploy.file.FileDeploymentDirectory;

public class ImportOrderFile
    implements Iterable<DeploymentObject>
{
    private static final String IMPORT_ORDER_FILE_NAME = "_import_order";

    private List<String> dependencies = new ArrayList<String>();
    private List<DeploymentObject> filesAndDirectories = new ArrayList<DeploymentObject>();

    private DeploymentDirectory directory;
    private String importOrderFileName;

    public ImportOrderFile(final ImportOrder importOrder)
    {
        this(importOrder.getDirectory());

        for (DeploymentFile deploymentFile : importOrder) {
            addDeploymentObject(deploymentFile);
        }
    }

    public ImportOrderFile(final File directory)
    {
        this.directory = new FileDeploymentDirectory(directory);
    }

    public ImportOrderFile(final DeploymentDirectory directory)
    {
        this.directory = directory;
    }

    public ImportOrderFile(final File directory,
                           final String importOrderFileName)
    {
        this(directory);
        this.importOrderFileName = importOrderFileName;
    }

    public void addDependency(final String dependency)
    {
        dependencies.add(dependency);
    }

    public void addDeploymentObject(final int index,
                                    final DeploymentObject deploymentObject)
    {
        filesAndDirectories.add(index, deploymentObject);
    }

    public void addDeploymentObject(final DeploymentObject deploymentObject)
    {
        filesAndDirectories.add(deploymentObject);
    }

    public void removeDeploymentObject(final DeploymentObject deploymentObject)
    {
        filesAndDirectories.remove(deploymentObject);
    }

    public Iterator<DeploymentObject> iterator()
    {
        return filesAndDirectories.iterator();
    }

    public List<String> getDependencies()
    {
        return dependencies;
    }

    public DeploymentDirectory getDirectory()
    {
        return directory;
    }

    public boolean imports(final DeploymentObject fileOrDirectory)
    {
        for (DeploymentObject deploymentObject : this) {
            if (deploymentObject.imports(fileOrDirectory)) {
                return true;
            }
        }

        return false;
    }

    private String getImportOrderFileName()
    {
        return (importOrderFileName != null) ? importOrderFileName : IMPORT_ORDER_FILE_NAME;
    }

    @Override
    public String toString()
    {
        try {
            return getFile().getAbsolutePath();
        } catch (IOException e) {
            return directory + "/" + getImportOrderFileName();
        }
    }

    public File getFile()
        throws IOException
    {
        DeploymentDirectory directory = getDirectory();

        if (!(directory instanceof FileDeploymentDirectory)) {
            throw new IOException("Cannot write to directory " + directory + " since it doesn't seem to be a standard directory.");
        }

        File fileDirectory = ((FileDeploymentDirectory) directory).getFile();
        return new File(fileDirectory, getImportOrderFileName());
    }
}
