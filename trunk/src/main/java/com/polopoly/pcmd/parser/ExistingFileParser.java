package com.polopoly.pcmd.parser;

import java.io.File;

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

        return file;
    }

    protected void validate(File file) throws ParseException {
    }

}
