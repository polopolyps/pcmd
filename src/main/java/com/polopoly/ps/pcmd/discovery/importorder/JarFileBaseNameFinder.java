package com.polopoly.ps.pcmd.discovery.importorder;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JarFileBaseNameFinder {
    private static final Logger logger = Logger.getLogger(JarFileBaseNameFinder.class.getName());

    public String getBaseName(String jarAbsolutePath) {
        String jarFileName = stripPath(jarAbsolutePath);

        String jarFileNameWithoutExtension = stripExtension(jarAbsolutePath, jarFileName);

        Pattern pattern = Pattern.compile("[^\\d]*(-\\d+(\\.\\d+)*).*?");

        Matcher matcher = pattern.matcher(jarFileNameWithoutExtension);

        if (matcher.matches()) {
            String versionString = matcher.group(1);
            int versionStart = jarFileNameWithoutExtension.indexOf(versionString);

            if (versionStart != -1) {
                String afterVersion = jarFileNameWithoutExtension.substring(versionStart + versionString.length());

                if (afterVersion.startsWith("-SNAPSHOT")) {
                    afterVersion = afterVersion.substring("-SNAPSHOT".length());
                }

                return jarFileNameWithoutExtension.substring(0, versionStart)
                       + (afterVersion.equals("-tests") ? afterVersion : "");
            } else {
                logger.log(Level.WARNING, "Funny, could not find \"" + versionString + "\" in \""
                                          + jarFileNameWithoutExtension + "\".");
            }
        }

        return jarFileNameWithoutExtension;
    }

    private String stripExtension(String jarAbsolutePath, String jarFileName) {
        int extensionStartsAtIndex = jarFileName.lastIndexOf(".jar");

        String jarFileNameWithoutExtension;

        if (extensionStartsAtIndex != -1) {
            jarFileNameWithoutExtension = jarFileName.substring(0, extensionStartsAtIndex);
        } else {
            logger.log(Level.WARNING, "The JAR file " + jarAbsolutePath + " did not have the extension \".jar\".");

            jarFileNameWithoutExtension = jarFileName;
        }
        return jarFileNameWithoutExtension;
    }

    private String stripPath(String jarAbsolutePath) {
        int i = jarAbsolutePath.lastIndexOf('/');

        if (i == -1) {
            // handle both windows and unix paths.
            i = jarAbsolutePath.lastIndexOf('\\');
        }

        String jarFileName;

        if (i != -1) {
            jarFileName = jarAbsolutePath.substring(i + 1);
        } else {
            jarFileName = jarAbsolutePath;
        }
        return jarFileName;
    }
}
