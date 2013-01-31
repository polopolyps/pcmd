package com.polopoly.pcmd.tool;

import static org.junit.Assert.*;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.geronimo.mail.util.StringBufferOutputStream;
import org.junit.Before;
import org.junit.Test;

import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.ps.pcmd.Main;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.DefaultArguments;
import com.polopoly.ps.pcmd.tool.InspectTool;
import com.polopoly.util.client.PolopolyContext;

public class InspectToolIntegrationTest extends AbstractIntegrationTestBase {

    private PolopolyContext context;
    private StringBuffer out;

    @Before
    public void setup() {
        context = new PolopolyContext(testApplication.getApplication());
        out = new StringBuffer();
        System.setOut(new PrintStream(new StringBufferOutputStream(out)));

    }

    @Test
    public void inspectTest() throws FatalToolException, ArgumentException {

        List<String> args = new ArrayList<String>();
        args.add(this.getClass().getName() + ".article");

        DefaultArguments arguments = new DefaultArguments("InspectTool", new HashMap<String, List<String>>(), args);
        arguments.setContext(context);

        Main.execute(new InspectTool(), context, arguments);
        assertTrue(out.toString().contains("name:" + this.getClass().getName()));
    }

}
