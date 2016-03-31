package com.polopolyps.pcmd.test;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.app.Resource;
import com.polopoly.cm.app.policy.FilePolicy;
import com.polopoly.cm.client.CMException;
import com.polopoly.siteengine.standard.content.ContentBasePolicy;



/**
 * A Policy representing a file resource.
 */
public class FileResourcePolicy extends ContentBasePolicy
    implements Resource
{
    private static final Logger LOG = Logger.getLogger(FileResourcePolicy.class.getName());
    private static final ContentId ICON_EXTERNAL_ID = new ExternalContentId("p.Icons");

    private static final String RESOURCE_TYPE = "file";

    private ContentId iconContentId;


    /**
     * Returns the file policy
     *
     * @return the file policy of this file resource
     * @throws CMException if something goes wrong
     */
    public FilePolicy getFilePolicy()
        throws CMException
    {
        return (FilePolicy) getChildPolicy("file");
    }

    public Map<String, String> getResourceData()
        throws CMException
    {
        try {
            FilePolicy filePolicy = getFilePolicy();
            String fileName = filePolicy.getFileName();

            if (fileName != null) {
                Map<String, String> map = new HashMap<String, String>();

                String filePath = filePolicy.getFullFilePath();

                map.put(Resource.FIELD_CONTENT_FILE_PATH, filePath);
                map.put(Resource.FIELD_RESOURCE_TYPE, RESOURCE_TYPE);
                map.put(Resource.FIELD_IMG_ALT, getName());

                return map;
            }
        } catch (Exception e) {
            logger.logp(Level.WARNING, CLASS, "getResourceData",
                    "Failed to create resource data for " + getContentId(), e);
        }

        return null;
    }
}
