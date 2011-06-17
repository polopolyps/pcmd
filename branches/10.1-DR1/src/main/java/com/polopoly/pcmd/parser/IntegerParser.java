package com.polopoly.pcmd.parser;

public class IntegerParser implements Parser<Integer> {

    public String getHelp() {
        return "<integer>";
    }

    public Integer parse(String string) throws ParseException {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            throw new ParseException(this, string, "not a number");
        }
    }

}
