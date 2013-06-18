package com.polopoly.ps.pcmd.util;

import java.io.File;

public class VersionedJar {
    private static final String SNAPSHOT_SUFFIX = "-SNAPSHOT";
    String version;
    String jar;

    public VersionedJar(File file) throws NotAJarException {
        if (!file.getName().endsWith(".jar")) {
            throw new NotAJarException();
        }

        String jarName = file.getName().substring(0, file.getName().length() - 4);

        int i = findVersionSeparator(jarName);

        if (i == -1) {
            jar = jarName;
            version = "";
        } else {
            version = jarName.substring(i + 1);
            jar = jarName.substring(0, i);
        }
    }

    public String getJarWithoutVersion() {
        return jar;
    }

    public String getVersion() {
        return version;
    }

    private int findVersionSeparator(String jarName) {
        int i = jarName.length() - 1;

        if (jarName.endsWith(SNAPSHOT_SUFFIX)) {
            i -= SNAPSHOT_SUFFIX.length();
        }

        // find the last segment of the name (separated by minuses) that does
        // not
        // contain a number.
        int lastMinus = -1;
        boolean foundNumberSinceLastMinus = false;

        for (; i >= 0; i--) {
            char ch = jarName.charAt(i);

            if (ch == '-') {
                if (!foundNumberSinceLastMinus) {
                    return lastMinus;
                }

                lastMinus = i;
                foundNumberSinceLastMinus = false;
            } else if (ch >= '0' && ch <= '9') {
                foundNumberSinceLastMinus = true;
            }
        }

        return -1;
    }
}