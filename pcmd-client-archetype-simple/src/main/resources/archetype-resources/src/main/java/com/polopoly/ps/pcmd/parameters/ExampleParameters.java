#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package com.polopoly.ps.pcmd.parameters;

import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.Arguments;
import com.polopoly.ps.pcmd.argument.ParameterHelp;
import com.polopoly.ps.pcmd.argument.Parameters;
import com.polopoly.util.client.PolopolyContext;

public class ExampleParameters implements Parameters {
	private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String tool) {
        this.message = tool;
    }

    public void getHelp(ParameterHelp help) {
        help.setArguments(null, "message.");
    }

    public void parseParameters(Arguments args, PolopolyContext context)
            throws ArgumentException {
        if (args.getArgumentCount() > 0) {
            setMessage(args.getArgument(0));
        } else {
        	throw new ArgumentException("Missing argument message");
        }
    }

}
