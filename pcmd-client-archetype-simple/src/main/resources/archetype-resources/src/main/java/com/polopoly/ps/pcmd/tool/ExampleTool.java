#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package com.polopoly.ps.pcmd.tool;

import com.polopoly.pcmd.tool.Tool;
import com.polopoly.ps.pcmd.parameters.ExampleParameters;
import com.polopoly.util.client.PolopolyContext;

public class ExampleTool implements Tool<ExampleParameters> {

    @Override
    public void execute(PolopolyContext context, ExampleParameters parameters) {
    	System.out.println("Example tool was executed, message: \"" + parameters.getMessage() + "\"");
    }

    @Override
    public String getHelp() {
        return "Pcmd Example tool.";
    }

    @Override
    public ExampleParameters createParameters() {
        return new ExampleParameters();
    }


}
