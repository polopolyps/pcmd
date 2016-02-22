package com.polopoly.ps.pcmd.discovery;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.ps.pcmd.file.DeploymentFile;

public class FallbackDiscoverer implements FileDiscoverer {
    private static final Logger logger = Logger.getLogger(FallbackDiscoverer.class.getName());

    private FileDiscoverer primaryDiscoverer;
    private FileDiscoverer secondaryDiscoverer;

    public FallbackDiscoverer(FileDiscoverer primaryDiscoverer, FileDiscoverer secondaryDiscoverer) {
        this.primaryDiscoverer = primaryDiscoverer;
        this.secondaryDiscoverer = secondaryDiscoverer;
    }

    public List<DeploymentFile> getFilesToImport() throws NotApplicableException {
        List<DeploymentFile> result = null;
        try {
            result = primaryDiscoverer.getFilesToImport();
        } catch (NotApplicableException e) {
            logger.log(Level.INFO, "Cannot apply discovery strategy " + primaryDiscoverer + ": " + e.getMessage());
        }

        if (result == null) {
            result = secondaryDiscoverer.getFilesToImport();
        }

        return result;
    }

    @Override
    public String toString() {
        return primaryDiscoverer + " or " + secondaryDiscoverer;
    }
}
