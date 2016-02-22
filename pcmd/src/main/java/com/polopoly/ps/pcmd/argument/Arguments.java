package com.polopoly.ps.pcmd.argument;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.polopoly.cm.ContentId;
import com.polopoly.ps.pcmd.parser.ParseException;
import com.polopoly.ps.pcmd.parser.Parser;

public interface Arguments {
    Iterator<ContentId> getStdInContentIds();

    Collection<ContentId> getArgumentContentIds(int i, boolean stopOnException) throws ArgumentException;

    <T> T getOption(String name, Parser<T> parser) throws ArgumentException;

    <T> List<T> getOptions(String name, Parser<T> parser) throws ArgumentException;

    <T> T getOption(String option, Parser<T> parser, String defaultString) throws ParseException;

    boolean getFlag(String option, boolean defaultValue) throws ArgumentException;

    String getOptionString(String option) throws NotProvidedException;

    List<String> getOptionStrings(String option) throws NotProvidedException;

    String getOptionString(String option, String defaultValue);

    int getArgumentCount();

    String getArgument(int i) throws NotProvidedException;

    <T> T getArgument(int i, Parser<T> parser) throws ArgumentException;

    Set<String> getUnusedParameters();
}
