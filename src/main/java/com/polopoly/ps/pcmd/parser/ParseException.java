package com.polopoly.ps.pcmd.parser;

import com.polopoly.ps.pcmd.argument.ArgumentException;

public class ParseException extends ArgumentException {
    private String field;

    public ParseException(Parser<?> parser, String value, String message) {
        super("Error parsing \"" + value + "\". Should be " + parser.getHelp() + ": " + message);
    }

    public ParseException(Parser<?> parser, String value, Exception e) {
        super("Fatal error while parsing \"" + value + " (Should be " + parser.getHelp() + "): " + e, e);
    }

    public void setField(String field) {
        this.field = field;
    }

    @Override
    public String getMessage() {
        return (field != null ? field + ": " : "") + super.getMessage();
    }
}
