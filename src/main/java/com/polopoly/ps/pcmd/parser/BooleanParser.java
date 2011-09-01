package com.polopoly.ps.pcmd.parser;

public class BooleanParser implements Parser<Boolean> {
    public Boolean parse(String string) throws ParseException {
        if ("true".equals(string) || "on".equals(string)) {
            return true;
        }
        else if ("false".equals(string) || "off".equals(string)) {
            return false;
        }
        else {
            throw new ParseException(this, string, "Expected \"true\" or \"false\".");
        }
    }

    public String getHelp() {
        return "true/false";
    }
}
