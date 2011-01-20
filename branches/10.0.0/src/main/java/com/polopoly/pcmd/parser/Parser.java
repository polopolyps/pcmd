package com.polopoly.pcmd.parser;

public interface Parser<T> {
    T parse(String string) throws ParseException;

    /**
     * Returns a very short format description (e.g. "true/false", or "<major>.<minor>")
     */
    String getHelp();
}
