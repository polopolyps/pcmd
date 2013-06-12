package com.polopoly.pcmd.tool;

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
import com.polopoly.ps.pcmd.tool.AclTool;
import com.polopoly.testbase.ImportTestContent;
import com.polopoly.user.server.UserServer;
import com.polopoly.util.client.PolopolyContext;

public class AclToolIT extends AbstractIntegrationTestBase {
    private PolopolyContext context;
    private StringBuffer out;

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

    @ImportTestContent(files = { "com.polopoly.pcmd.tool.AclToolIT.image.content" }, waitUntilContentsAreIndexed = { "com.polopoly.pcmd.tool.AclToolIT.image" })
    @Test
    public void aclForImageTest() throws FatalToolException, ArgumentException {
        List<String> args = new ArrayList<String>();
        args.add("com.polopoly.pcmd.tool.AclToolIT.image");

        DefaultArguments arguments = new DefaultArguments("AclTool", new HashMap<String, List<String>>(), args);
        arguments.setContext(context);

        Main.execute(new AclTool(), context, arguments);
        assertTrue(out.toString().contains("parent(1):p.siteengine.Sites.d"));
        assertTrue(out.toString().contains("No ACL ID"));
    }

    @ImportTestContent(files = { "com.polopoly.pcmd.tool.AclToolIT.article.content" }, waitUntilContentsAreIndexed = { "com.polopoly.pcmd.tool.AclToolIT.article" })
    @Test
    public void aclForArticleTest() throws FatalToolException, ArgumentException {
        List<String> args = new ArrayList<String>();
        args.add("com.polopoly.pcmd.tool.AclToolIT.article");

        DefaultArguments arguments = new DefaultArguments("AclTool", new HashMap<String, List<String>>(), args);
        arguments.setContext(context);

        Main.execute(new AclTool(), context, arguments);
        assertTrue(out.toString().contains("parent(1):p.siteengine.Sites.d"));
        assertTrue(out.toString().contains("No ACL ID"));
    }

    @Test
    public void aclForSiteTest() throws FatalToolException, ArgumentException {
        List<String> args = new ArrayList<String>();
        args.add("GreenfieldTimes.d");

        DefaultArguments arguments = new DefaultArguments("AclTool", new HashMap<String, List<String>>(), args);
        arguments.setContext(context);

        Main.execute(new AclTool(), context, arguments);
        assertTrue(out.toString().contains("parent(1):p.siteengine.Sites.d"));
        assertTrue(out.toString().contains("aclId:"));
        assertTrue(out.toString().contains("1WRITE"));
    }

}
