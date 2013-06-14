package com.polopoly.ps.pcmd.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import com.polopoly.ps.pcmd.util.NotAJarException;
import com.polopoly.ps.pcmd.util.VersionedJar;

public class JarDeploymentFile extends AbstractDeploymentObject implements DeploymentFile {

    protected JarFile file;
    protected ZipEntry entry;

    public JarDeploymentFile(JarFile file, ZipEntry entry) {
        this.file = file;
        this.entry = entry;
    }

    public InputStream getInputStream() throws FileNotFoundException {
        if (entry == null) {
            throw new FileNotFoundException("While reading " + this + ": file not found");
        }

        try {
            return file.getInputStream(entry);
        } catch (IOException e) {
            throw new FileNotFoundException("While reading " + this + ": " + e.getMessage());
        }
    }

    public String getName() {
        String entryName = null;

        if (entry != null) {
            entryName = entry.getName();

            if (entryName.endsWith("/")) {
                entryName = entryName.substring(0, entryName.length() - 1);
            }
        }

        // for equality we don't consider the path or version of a JAR file,
        // only its name,
        // since it is likely to be found in multiple places such as the maven
        // repository and
        // web-inf/lib. Also, it shouldn't be reimported just because there is a
        // new version if the content itself has not changed.

        File iofile = new File(file.getName());
        String jarWithoutVersion;

        try {
            jarWithoutVersion = new VersionedJar(iofile).getJarWithoutVersion();
        } catch (NotAJarException e) {
            jarWithoutVersion = iofile.getName();
        }

        return jarWithoutVersion + "!" + (entryName != null ? entryName : "n/a");
    }

    @Override
    public String toString() {
        String name = null;

        if (entry != null) {
            name = entry.getName();

            if (name.endsWith("/")) {
                name = name.substring(0, name.length() - 1);
            }
        }

        return file.getName() + "!" + (name != null ? name : "n/a");
    }

    public URL getBaseUrl() throws MalformedURLException {
        String name = appendSlashInFront(getNameOfDirectoryWithinJar());
        return new URL("jar:file:" + (new File(file.getName())).getAbsolutePath() + "!" + name);
    }

    public String getNameOfDirectoryWithinJar() {
        String name = entry.getName();

        int i = name.lastIndexOf("/");

        if (i != -1) {
            name = name.substring(0, i + 1);
        } else {
            name = "/";
        }
        return name;
    }

    private String appendSlashInFront(String orig) {
        if (!orig.startsWith("/")) {
            orig = "/" + orig;
        }
        return orig;
    }

    public JarFile getJarFile() {
        return file;
    }

    public String getNameWithinJar() {
        if (entry != null) {
            return entry.getName();
        } else {
            return "";
        }
    }

    public long getQuickChecksum() {
        return entry.getTime();
    }

    public long getSlowChecksum() {
        return entry.getCrc();
    }

    public boolean imports(DeploymentObject object) {
        return object.equals(this);
    }
}
