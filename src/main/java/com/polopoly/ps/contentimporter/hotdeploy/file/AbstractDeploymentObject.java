package com.polopoly.ps.contentimporter.hotdeploy.file;

/**
 * A file with an unique name of that file to use as key when comparing file
 * properties. Platform issues makes it impossible to use the file name.
 */
public abstract class AbstractDeploymentObject
    implements DeploymentObject, Comparable<DeploymentObject>
{
    @Override
    public boolean equals(final Object o)
    {
        return o instanceof DeploymentObject && ((DeploymentObject) o).getName().equals(getName());
    }

    @Override
    public int hashCode()
    {
        return getName().hashCode();
    }

    @Override
    public String toString()
    {
        return getName();
    }

    public int compareTo(final DeploymentObject o)
    {
        return getName().compareTo(o.getName());
    }
}