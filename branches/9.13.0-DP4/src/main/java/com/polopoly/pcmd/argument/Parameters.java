package com.polopoly.pcmd.argument;

import com.polopoly.util.client.PolopolyContext;

/**
 * Each tool is associated with a class implementing this interface. It has get and set methods for the
 * parameters of the tool.
 * @author andreasehrencrona
 */
public interface Parameters {
    void parseParameters(Arguments args, PolopolyContext context) throws ArgumentException;

    void getHelp(ParameterHelp help);
}
