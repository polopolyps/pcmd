package com.polopoly.ps.pcmd;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.application.IllegalApplicationStateException;
import com.polopoly.cm.client.ContentCacheSettings;
import com.polopoly.cm.client.DiskCacheSettings;
import com.polopoly.cm.client.EjbCmClient;
import com.polopoly.util.client.PolopolyClient;

public class PcmdPolopolyClient extends PolopolyClient {
    private static final Logger LOGGER = Logger.getLogger(PcmdPolopolyClient.class.getName());

    private Integer contentCacheSize;

    private Integer policyCacheSize;

    private File persistenceCacheDir = null;

    @Override
    protected void setUpCmClient(EjbCmClient cmClient) {
        super.setUpCmClient(cmClient);

        setupContentCache(cmClient);

        // Pcmd has no use for a persistence cache unless it's warming one for
        // the fronts.
        if (persistenceCacheDir != null) {
            setupPersistenceCache(cmClient);
        }
    }

    private void setupPersistenceCache(EjbCmClient cmClient) {
        DiskCacheSettings diskCacheSettings = new DiskCacheSettings();
        final String BASE = persistenceCacheDir.getAbsolutePath();
        diskCacheSettings.setCacheBaseDirectory(persistenceCacheDir);
        diskCacheSettings.setContentCacheDirectory(new File(BASE + "/contentcache"));
        diskCacheSettings.setDerivedFilesCacheDirectory(new File(BASE + "/derivedfiles"));
        diskCacheSettings.setFilesCacheDirectory(new File(BASE + "/filescache"));

        try {
            cmClient.setDiskCacheSettings(diskCacheSettings);
        } catch (Exception e) {
            System.err.println("Could not set disk cache settings: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    private void setupContentCache(EjbCmClient cmClient) {
        ContentCacheSettings contentCacheSettings = new ContentCacheSettings();

        if (policyCacheSize != null) {
            contentCacheSettings.setPolicyMemoryCacheSize(policyCacheSize.intValue());
        }

        if (contentCacheSize != null) {
            contentCacheSettings.setContentMemoryCacheSize(contentCacheSize.intValue());
        }

        try {
            cmClient.setContentCacheSettings(contentCacheSettings);
        } catch (IllegalApplicationStateException e) {
            LOGGER.log(Level.WARNING, "Cannot configure cache settings: " + e.getMessage());
        }
    }

    public void setPolicyCacheSize(int policyCacheSize) {
        this.policyCacheSize = new Integer(policyCacheSize);
    }

    public void setContentCacheSize(int contentCacheSize) {
        this.contentCacheSize = new Integer(contentCacheSize);
    }

    public void setPersistenceCacheDir(File persistenceCacheDir) {
        this.persistenceCacheDir = persistenceCacheDir;

    }
}