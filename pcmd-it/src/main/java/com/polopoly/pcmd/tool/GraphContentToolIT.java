package com.polopoly.pcmd.tool;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.geronimo.mail.util.StringBufferOutputStream;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.ps.pcmd.Main;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.DefaultArguments;
import com.polopoly.ps.pcmd.tool.GraphContentTool;
import com.polopoly.testnext.base.ImportTestContent;
import com.polopoly.user.server.UserServer;
import com.polopoly.util.client.PolopolyContext;

@ImportTestContent(files = { "com.polopoly.pcmd.tool.GraphContentToolIT.xml" })
public class GraphContentToolIT extends AbstractIntegrationTestBase {
    private PolopolyContext context;
    private StringBuffer out;
    private StringBuffer err;

    @Inject
    private PolicyCMServer cmServer;
    
    @Inject
    private UserServer userServer;

    @Before
    public void setup() {
        context = new PolopolyContext(userServer, cmServer);

        out = new StringBuffer();
        System.setOut(new PrintStream(new StringBufferOutputStream(out)));
    }

    public void setupSystemErrPrint() {
        err = new StringBuffer();
        System.setErr(new PrintStream(new StringBufferOutputStream(err)));
    }

    /**
     * "control group" for graph content tests
     * 
     * @throws ArgumentException
     */
    @Test
    public void graphControlTest() throws ArgumentException {
        setupSystemErrPrint();
        List<String> args = new ArrayList<String>();
        args.add(GraphContentToolIT.class.getName() + ".image");

        DefaultArguments arguments =
            new DefaultArguments("GraphContentTool", new HashMap<String, List<String>>(), args);
        arguments.setContext(context);

        Main.execute(new GraphContentTool(), context, arguments);

        assertTrue(out.toString().contains("example.Image"));
        assertTrue(out.toString().contains("p.Column"));
        assertTrue(out.toString().contains("p.TreeCategory"));
        assertTrue(out.toString().contains("subject-15093"));
        assertTrue(out.toString().contains("p.TextInput"));
        assertFalse(out.toString().contains("\"example.Image\" -> \"p.SelectableSubField\" [style = \"dotted\""));
        assertFalse(out.toString()
            .contains("\"com.polopoly.pcmd.tool.GraphContentToolIT.image\" -> \"subject-15093\" []"));
        assertFalse(err.toString()
            .contains("You have selected a depth greater than 3 and have not specified any filter majors"));
    }

    @Test
    public void majorsTest() throws ArgumentException {
        List<String> args = new ArrayList<String>();
        args.add(GraphContentToolIT.class.getName() + ".image");

        Map<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("majors", Arrays.asList("2")); // major 2 = Department

        DefaultArguments arguments = new DefaultArguments("GraphContentTool", options, args);
        arguments.setContext(context);

        Main.execute(new GraphContentTool(), context, arguments);

        assertTrue(out.toString().contains("subject-15093"));
        assertFalse(out.toString().contains("example.Image"));
    }

    @Test
    public void noPolopolyTest() throws ArgumentException {
        List<String> args = new ArrayList<String>();
        args.add(GraphContentToolIT.class.getName() + ".image");

        Map<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("no-polopoly", Arrays.asList("true"));

        DefaultArguments arguments = new DefaultArguments("GraphContentTool", options, args);
        arguments.setContext(context);

        Main.execute(new GraphContentTool(), context, arguments);

        assertFalse(out.toString().contains("p.Column"));
    }

    @Test
    public void noGtTest() throws ArgumentException {
        List<String> args = new ArrayList<String>();
        args.add(GraphContentToolIT.class.getName() + ".image");

        Map<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("no-gt", Arrays.asList("true"));

        DefaultArguments arguments = new DefaultArguments("GraphContentTool", options, args);
        arguments.setContext(context);

        Main.execute(new GraphContentTool(), context, arguments);

        assertFalse(out.toString().contains("example.Image"));
    }

    @Test
    public void depthTest() throws ArgumentException {
        List<String> args = new ArrayList<String>();
        args.add(GraphContentToolIT.class.getName() + ".image");

        Map<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("depth", Arrays.asList("2"));

        DefaultArguments arguments = new DefaultArguments("GraphContentTool", options, args);
        arguments.setContext(context);

        Main.execute(new GraphContentTool(), context, arguments);

        assertFalse(out.toString().contains("p.TreeCategory"));
        assertTrue(out.toString().contains("example.Image"));
    }

    @Test
    public void rmdTest() throws ArgumentException {
        List<String> args = new ArrayList<String>();
        args.add(GraphContentToolIT.class.getName() + ".image");

        Map<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("rmd", Arrays.asList("true"));

        DefaultArguments arguments = new DefaultArguments("GraphContentTool", options, args);
        arguments.setContext(context);

        Main.execute(new GraphContentTool(), context, arguments);

        assertFalse(out.toString().contains("p.TextInput"));
    }

    /**
     * test "depth" > 3 without the "force" parameter
     * 
     * @throws ArgumentException
     */
    @Test
    public void initialForceTest() throws ArgumentException {
        setupSystemErrPrint();

        List<String> args = new ArrayList<String>();
        args.add(GraphContentToolIT.class.getName() + ".image");

        Map<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("depth", Arrays.asList("5"));

        DefaultArguments arguments = new DefaultArguments("GraphContentTool", options, args);
        arguments.setContext(context);

        Main.execute(new GraphContentTool(), context, arguments);

        assertTrue(err.toString()
            .contains("You have selected a depth greater than 3 and have not specified any filter majors"));
    }

    /**
     * test "depth" with "force" parameter
     * 
     * @throws ArgumentException
     */
    @Test
    public void theRealForceTest() throws ArgumentException {
        setupSystemErrPrint();

        List<String> args = new ArrayList<String>();
        args.add(GraphContentToolIT.class.getName() + ".image");

        Map<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("depth", Arrays.asList("5"));
        options.put("force", Arrays.asList("true"));

        DefaultArguments arguments = new DefaultArguments("GraphContentTool", options, args);
        arguments.setContext(context);

        Main.execute(new GraphContentTool(), context, arguments);

        assertFalse(err.toString()
            .contains("You have selected a depth greater than 3 and have not specified any filter majors"));
        assertTrue(out.toString().contains("p.TextInput"));
    }

    @Test
    public void dotTest() throws ArgumentException {
        List<String> args = new ArrayList<String>();
        args.add(GraphContentToolIT.class.getName() + ".image");

        Map<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("dot", Arrays.asList("true"));

        DefaultArguments arguments = new DefaultArguments("GraphContentTool", options, args);
        arguments.setContext(context);

        Main.execute(new GraphContentTool(), context, arguments);

        assertTrue(out.toString().contains("\"pcmd.Image\" -> \"p.SelectableSubField\" [style = \"dotted\""));
        assertTrue(out.toString()
            .contains("\"com.polopoly.pcmd.tool.GraphContentToolIT.image\" -> \"subject-15093\" []"));
    }
}