package com.polopoly.ps.pcmd.parser;

import java.io.File;
import java.io.IOException;

public class CreatingDirectoryParser implements Parser<File> {

    public String getHelp() {
        return "A new directory path.";
    }

    public File parse(String fileName) throws ParseException {
        File dir = new File(fileName);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            dir = dir.getCanonicalFile();
        } catch (IOException e) {
            System.err.println("Could not turn " + dir.getAbsolutePath() + " into a canonical path.");
        }

        return dir;
    }

}
