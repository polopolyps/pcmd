package com.polopoly.pcmd.argument;

import java.util.Iterator;

import com.polopoly.cm.ContentId;
import com.polopoly.pcmd.parser.Parser;

public interface Arguments {
    Iterator<ContentId> getStdInContentIds() throws ArgumentException;

    Iterator<ContentId> getArgumentContentIds(int i, boolean stopOnException) throws ArgumentException;

    <T> T getOption(String name, Parser<T> parser) throws ArgumentException;

    boolean getFlag(String option, boolean defaultValue) throws ArgumentException;

    String getOptionString(String option) throws NotProvidedException;

    String getOptionString(String option, String defaultValue);

    int getArgumentCount();

    String getArgument(int i) throws NotProvidedException;

    <T> T getArgument(int i, Parser<T> parser) throws ArgumentException;
}
