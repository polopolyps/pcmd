package com.polopoly.ps.contentimporter.hotdeploy.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;

public class FileDeploymentFile extends AbstractDeploymentObject implements DeploymentFile, TemplateDefinitionAware {
    private static final int BUF_SIZE = 1024 * 64;
    private static final Logger logger = Logger.getLogger(FileDeploymentFile.class.getName());

    protected File file;
    private boolean isTemplateDefinitionFile;

    public FileDeploymentFile(final File file) {
        this.file = file;
    }

    public InputStream getInputStream() throws FileNotFoundException {
        return new FileInputStream(file);
    }

    public String getName() {
        return file.getAbsolutePath();
    }

    public URL getBaseUrl() throws MalformedURLException {
        File parentFile = getParentFile();
        return parentFile.toURI().toURL();
    }

    File getParentFile() {
        File result = file.getParentFile();

        if (result == null) {
            result = file.getAbsoluteFile().getParentFile();
        }

        return result;
    }

    public boolean isTemplateDefinitionFile() {
        return isTemplateDefinitionFile;
    }

    public void setIsTemplateDefinitionFile(final boolean isTemplateDefinitionFile) {
        this.isTemplateDefinitionFile = isTemplateDefinitionFile;
    }

    public String getDirectory() {
        return getParentFile().getAbsolutePath();
    }

    public long getQuickChecksum() {
        return file.lastModified();
    }

    public long getSlowChecksum() {
        CRC32 checksum = new CRC32();

        byte[] buffer = new byte[BUF_SIZE];

        try {
            InputStream inputStream = getInputStream();

            int readBytes;

            do {
                readBytes = inputStream.read(buffer);

                if (readBytes > 0) {
                    checksum.update(buffer, 0, readBytes);
                }
            }

            while (readBytes == BUF_SIZE);

            inputStream.close();
            return checksum.getValue();
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            System.err.println(e.getMessage());
            return 0;
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            System.err.println(e.getMessage());
            return 0;
        }
    }

    public File getFile() {
        return file;
    }

    public boolean imports(final DeploymentObject object) {
        return object.equals(this);
    }
}
