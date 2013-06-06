package com.polopoly.pcmd.tool;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.geronimo.mail.util.StringBufferOutputStream;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.ps.pcmd.Main;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.DefaultArguments;
import com.polopoly.ps.pcmd.tool.DeleteTool;
import com.polopoly.ps.pcmd.tool.InspectTool;
import com.polopoly.testbase.ImportTestContent;
import com.polopoly.util.client.PolopolyContext;

@ImportTestContent
public class DeleteToolIT extends AbstractIntegrationTestBase {
    private PolopolyContext context;
    private StringBuffer out;

    @Inject
    private PolicyCMServer cmServer;

    @Before
    public void setup() {
        context = new PolopolyContext(cmServer);
        out = new StringBuffer();
        System.setOut(new PrintStream(new StringBufferOutputStream(out)));
    }

    @Test
    public void deleteArticleWithoutReferenceTest() throws FatalToolException, ArgumentException {

        List<String> args = new ArrayList<String>();
        args.add(DeleteToolIT.class.getName() + ".article");

        DefaultArguments arguments1 = new DefaultArguments("InspectTool", new HashMap<String, List<String>>(), args);
        arguments1.setContext(context);

        Main.execute(new InspectTool(), context, arguments1);
        assertTrue(out.toString().contains("name:" + DeleteToolIT.class.getName()));

        DefaultArguments arguments = new DefaultArguments("DeleteTool", new HashMap<String, List<String>>(), args);
        arguments.setContext(context);

        Main.execute(new DeleteTool(), context, arguments);
        assertFalse(out.toString().contains("While removing"));
    }
}
