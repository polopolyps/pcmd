package com.polopoly.ps.pcmd.file;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ResourceFile extends AbstractDeploymentObject implements DeploymentFile {
    private String resourceName;

    public ResourceFile(String resourceName) {
        this.resourceName = resourceName;
    }

    public URL getBaseUrl() throws MalformedURLException {
        URL resourceUrl = getClass().getResource(resourceName);

        String path = resourceUrl.getPath();

        int i = path.lastIndexOf('/');

        if (i != -1) {
            return new URL(resourceUrl.getProtocol(), resourceUrl.getHost(), path.substring(0, i) + "/");
        }

        throw new IllegalStateException("Expected the resource " + resourceName + " to be a file, not the root.");
    }

    public InputStream getInputStream() throws FileNotFoundException {
        InputStream result = getClass().getResourceAsStream(resourceName);

        if (result == null) {
            throw new FileNotFoundException("No resource with name \"" + resourceName + "\" existed.");
        }

        return result;
    }

    public long getQuickChecksum() {
        throw new IllegalStateException("Not implemented.");
    }

    public long getSlowChecksum() {
        throw new IllegalStateException("Not implemented.");
    }

    public String getName() {
        return resourceName;
    }

    public boolean imports(DeploymentObject object) {
        return object.equals(this);
    }

}
