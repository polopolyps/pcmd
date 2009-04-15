package com.polopoly.pcmd.argument;

import java.util.Iterator;

import com.polopoly.cm.ContentId;
import com.polopoly.pcmd.parser.Parser;

public interface Arguments {
    Iterator<ContentId> getStdInContentIds() throws ArgumentException;

    Iterator<ContentId> getArgumentContentIds() throws ArgumentException;

    <T> T getOption(String name, Parser<T> parser) throws ArgumentException;

    boolean getFlag(String option, boolean defaultValue) throws ArgumentException;

    String getOptionString(String string) throws ArgumentException;

    String getOptionString(String fields, String defaultValue);

    int getArgumentCount();

    String getArgument(int i);
}
