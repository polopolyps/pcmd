package com.polopoly.ps.pcmd.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

public class JarDeploymentDirectory extends AbstractDeploymentObject implements DeploymentDirectory {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final Charset LATIN_1 = Charset.forName("ISO-8859-1");

    private static final Logger LOGGER = Logger.getLogger(JarDeploymentDirectory.class.getName());

    protected JarFile jarFile;
    private ZipEntry entry;

    public JarDeploymentDirectory(JarFile file, ZipEntry entry) {
        this.jarFile = file;
        this.entry = entry;
    }

    public boolean exists() {
        return entry != null;
    }

    public DeploymentObject getFile(String fileName) throws FileNotFoundException {
        // zip files always use forward slashes. we can get confused under
        // windows.
        if (File.separatorChar == '\\') {
            fileName = fileName.replace(File.separatorChar, '/');
        }

        if (fileName.endsWith("/")) {
            fileName = fileName.substring(0, fileName.length() - 1);
        }

        if (entry != null) {
            if (!entry.isDirectory()) {
                throw new RuntimeException("Attempt to get subdirectory " + fileName + " from file " + this + ".");
            }
            fileName = getNameWithinJar() + fileName;
        }

        ZipEntry newEntry = jarFile.getEntry(fileName + '/');

        if (newEntry != null) {
            return new JarDeploymentDirectory(jarFile, newEntry);
        }

        newEntry = jarFile.getEntry(fileName);

        if (newEntry == null) {
            if (!isPureAscii(fileName)) {
                throw new FileNotFoundException(
                    fileName
                        + " could not be fetched from JAR "
                        + jarFile.getName()
                        + ". Note that this filename is not US ASCII. Jar files should have file names UTF-8-encoded but "
                        + "common ZIP tools don't always respect this. Try generating the JAR file using the Java JAR command.");
            } else {
                throw new FileNotFoundException(fileName + " could not be fetched from JAR " + jarFile.getName());
            }
        }

        return new JarDeploymentFile(jarFile, newEntry);
    }

    private ZipEntry getEntryUsingEncoding(String fileName, Charset charset) throws FileNotFoundException {
        String platformEncodedFileName = new String(fileName.getBytes(charset), UTF_8);

        ZipEntry newEntry = jarFile.getEntry(platformEncodedFileName);

        System.out.println(platformEncodedFileName + " " + newEntry + " " + charset.displayName());

        if (newEntry != null) {
            LOGGER.log(Level.WARNING,
                       "Note that the file name " + fileName + " in " + jarFile.getName()
                           + " contains non-US ASCII characters may therefore not be readable on all platforms.");
        } else {
            throw new FileNotFoundException(
                fileName
                    + " could not be fetched from JAR "
                    + jarFile.getName()
                    + ". Note that this file name is not US ASCII and is therefore platform dependent. I also tried fetching the file using "
                    + charset.displayName() + " (\"" + platformEncodedFileName + "\")" + " which also didn't work.");
        }

        return newEntry;
    }

    public static boolean isPureAscii(String stringToTest) {
        for (int i = 0; i < stringToTest.length(); i++) {
            if (!isPureAscii(stringToTest.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private static boolean isPureAscii(char charAt) {
        return charAt < 128;
    }

    public DeploymentObject[] listFiles() {
        List<DeploymentObject> result = new ArrayList<DeploymentObject>();

        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            JarEntry resultEntry = entries.nextElement();

            if (isInThisDir(resultEntry)) {
                if (resultEntry.isDirectory()) {
                    result.add(new JarDeploymentDirectory(jarFile, resultEntry));
                } else {
                    result.add(new JarDeploymentFile(jarFile, resultEntry));
                }
            }
        }

        return result.toArray(new DeploymentObject[result.size()]);
    }

    protected boolean isInThisDir(JarEntry resultEntry) {
        return resultEntry.getName().startsWith(getNameWithinJar())
               && !resultEntry.getName().equals(getNameWithinJar());
    }

    public String getName() {
        String nameWithinJar = getNameWithinJar();

        if (nameWithinJar.endsWith("/")) {
            nameWithinJar = nameWithinJar.substring(0, nameWithinJar.length() - 1);
        }

        return jarFile.getName() + "!" + nameWithinJar;
    }

    protected String getNameWithinJar() {
        return entry.getName();
    }

    public String getJarFileName() {
        return jarFile.getName();
    }

    public String getRelativeName(DeploymentObject deploymentObject) {
        if (deploymentObject instanceof JarDeploymentFile
            && ((JarDeploymentFile) deploymentObject).getJarFile().equals(getJarFile())) {
            String fileName = ((JarDeploymentFile) deploymentObject).getNameWithinJar();
            String directoryName = getNameWithinJar();

            if (fileName.startsWith(directoryName)) {
                return fileName.substring(directoryName.length());
            }
        }

        return deploymentObject.getName();
    }

    protected JarFile getJarFile() {
        return jarFile;
    }

    public boolean imports(DeploymentObject object) {
        if (object instanceof JarDeploymentDirectory) {
            return ((JarDeploymentDirectory) object).getJarFile().equals(jarFile)
                   && ((JarDeploymentDirectory) object).getNameWithinJar().startsWith(getNameWithinJar());
        }

        if (object instanceof JarDeploymentFile) {
            return ((JarDeploymentFile) object).getJarFile().equals(jarFile)
                   && ((JarDeploymentFile) object).getNameWithinJar().startsWith(getNameWithinJar());
        }

        return false;
    }
}
