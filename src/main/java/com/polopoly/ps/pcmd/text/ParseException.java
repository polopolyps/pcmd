package com.polopoly.ps.pcmd.text;

public class ParseException extends Exception {

    public ParseException(String message, String line, int atLine) {
        super("At line " + atLine + ", \"" + startOf(line) + "\": " + message);
    }

    private static String startOf(String line) {
        if (line.length() > 40) {
            return line.substring(0, 40) + "...";
        } else {
            return line;
        }
    }

}
