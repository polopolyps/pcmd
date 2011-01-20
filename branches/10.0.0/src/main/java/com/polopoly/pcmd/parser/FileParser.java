package com.polopoly.pcmd.parser;

import java.io.File;
import java.io.IOException;

import com.polopoly.pcmd.parser.ParseException;
import com.polopoly.pcmd.parser.Parser;

public class FileParser implements Parser<File> {

    public String getHelp() {
        return "a file name (relative or absolute)";
    }

    public File parse(String fileName) throws ParseException {
        File file = new File(fileName);

        validate(file);

        try {
            file = file.getCanonicalFile();
        } catch (IOException e) {
            throw new ParseException(this, fileName, "Could not turn "
                    + file.getAbsolutePath() + " into a canonical path.");
        }

        return file;
    }

    protected void validate(File file) throws ParseException {
    }

}
