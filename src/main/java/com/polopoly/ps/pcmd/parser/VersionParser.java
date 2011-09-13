package com.polopoly.ps.pcmd.parser;

public class VersionParser implements Parser<Long>{

    @Override
    public Long parse(String string) throws ParseException {
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException e) {
            throw new ParseException(this, string, "not a version");
        }
    }

    @Override
    public String getHelp() {
        return "<version>";
    }

}
