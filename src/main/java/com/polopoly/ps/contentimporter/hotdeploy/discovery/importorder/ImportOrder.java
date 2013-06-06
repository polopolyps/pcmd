package com.polopoly.ps.contentimporter.hotdeploy.discovery.importorder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.polopoly.ps.contentimporter.hotdeploy.file.DeploymentDirectory;
import com.polopoly.ps.contentimporter.hotdeploy.file.DeploymentFile;
import com.polopoly.ps.contentimporter.hotdeploy.file.FileDeploymentDirectory;

@SuppressWarnings("serial")
public class ImportOrder
    extends ArrayList<DeploymentFile>
{
    private DeploymentDirectory directory;
    private List<String> dependencies = new ArrayList<String>();

    public ImportOrder(final DeploymentDirectory directory)
    {
        this.directory = directory;
    }

    public void addDependency(final String dependency)
    {
        dependencies.add(dependency);
    }

    public Collection<String> getDependencies()
    {
        return dependencies;
    }

    public DeploymentDirectory getDirectory()
    {
        return directory;
    }

    public void setDirectory(final DeploymentDirectory directory)
    {
        this.directory = directory;
    }

    public void setDirectory(final File directory)
    {
        this.directory = new FileDeploymentDirectory(directory);
    }

    @Override
    public String toString()
    {
        return "import order file in " + directory + ": " + super.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof ImportOrder && ((ImportOrder) o).getDirectory().equals(directory);
    }

    @Override
    public int hashCode()
    {
        return directory.hashCode();
    }
}
