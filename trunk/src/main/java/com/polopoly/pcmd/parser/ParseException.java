package com.polopoly.pcmd.parser;

import com.polopoly.pcmd.argument.ArgumentException;

public class ParseException extends ArgumentException {
    private String field;

    public ParseException(Parser<?> parser, String value, String message) {
        super("Error parsing \"" + value + "\". Should be " + parser.getHelp() + ": " + message);
    }

    public void setField(String field) {
        this.field = field;
    }

    @Override
    public String getMessage() {
        return (field != null ? field + ": " : "") + super.getMessage();
    }
}
