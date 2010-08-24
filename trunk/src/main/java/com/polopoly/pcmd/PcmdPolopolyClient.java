package com.polopoly.pcmd;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.application.IllegalApplicationStateException;
import com.polopoly.cm.client.ContentCacheSettings;
import com.polopoly.cm.client.EjbCmClient;
import com.polopoly.util.client.PolopolyClient;

public class PcmdPolopolyClient extends PolopolyClient {
    private static final Logger LOGGER = Logger
            .getLogger(PcmdPolopolyClient.class.getName());

    private Integer contentCacheSize;

    private Integer policyCacheSize;

    @Override
    protected void setUpCmClient(EjbCmClient cmClient) {
        super.setUpCmClient(cmClient);

        ContentCacheSettings contentCacheSettings = new ContentCacheSettings();

        if (policyCacheSize != null) {
            contentCacheSettings.setPolicyMemoryCacheSize(policyCacheSize
                    .intValue());
        }

        if (contentCacheSize != null) {
            contentCacheSettings.setContentMemoryCacheSize(contentCacheSize
                    .intValue());
        }

        try {
            cmClient.setContentCacheSettings(contentCacheSettings);
        } catch (IllegalApplicationStateException e) {
            LOGGER.log(Level.WARNING, "Cannot configure cache settings: "
                    + e.getMessage());
        }
    }

    public void setPolicyCacheSize(int policyCacheSize) {
        this.policyCacheSize = new Integer(policyCacheSize);
    }

    public void setContentCacheSize(int contentCacheSize) {
        this.contentCacheSize = new Integer(contentCacheSize);
    }
}