package com.polopoly.ps.pcmd.file;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A file with an unique name of that file to use as key when comparing file
 * properties. Platform issues makes it impossible to use the file name.
 */
public interface DeploymentFile extends DeploymentObject {
    InputStream getInputStream() throws FileNotFoundException;

    /**
     * Returns the parent URL (relative to which relative file names / URLs can
     * be resolved).
     */
    URL getBaseUrl() throws MalformedURLException;

    long getQuickChecksum();

    long getSlowChecksum();
}