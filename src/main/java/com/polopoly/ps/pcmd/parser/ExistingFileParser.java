package com.polopoly.ps.pcmd.parser;

import java.io.File;
import java.io.IOException;

public class ExistingFileParser implements Parser<File> {

    public String getHelp() {
        return "a file name (relative or absolute)";
    }

    public File parse(String fileName) throws ParseException {
        File file = new File(fileName);

        validate(file);

        if (!file.exists()) {
            throw new ParseException(this, fileName, "The file " + file.getAbsolutePath() + " did not exist.");
        }

        try {
            file = file.getCanonicalFile();
        } catch (IOException e) {
            System.err.println("Could not turn " + file.getAbsolutePath() + " into a canonical path.");
        }

        return file;
    }

    protected void validate(File file) throws ParseException {
    }

}
