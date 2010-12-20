package com.polopoly.pcmd.parser;

import java.io.File;

public class ExistingDirectoryParser extends ExistingFileParser {

    @Override
    public String getHelp() {
        return "a file name (relative or absolute) denoting a directory";
    }

    @Override
    protected void validate(File file) throws ParseException {
        if (!file.exists()) {
            throw new ParseException(this, file.getName(),
                    "The directory does not exist.");
        }

        if (!file.isDirectory()) {
            throw new ParseException(this, file.getName(),
                    "A directory must be specified, not an ordinary file.");
        }
    }
}
