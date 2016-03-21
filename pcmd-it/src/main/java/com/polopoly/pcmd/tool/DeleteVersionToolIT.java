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
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.ps.pcmd.Main;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.DefaultArguments;
import com.polopoly.ps.pcmd.tool.DeleteTool;
import com.polopoly.ps.pcmd.tool.DeleteVersionTool;
import com.polopoly.ps.pcmd.tool.InspectTool;
import com.polopoly.testnext.base.ImportTestContent;
import com.polopoly.user.server.UserServer;
import com.polopoly.util.client.PolopolyContext;

@ImportTestContent
public class DeleteVersionToolIT extends AbstractIntegrationTestBase {
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

    @Test
    public void deleteDefaultVersionTest() throws FatalToolException, ArgumentException {

        List<String> args = new ArrayList<String>();
        args.add(DeleteVersionToolIT.class.getName() + ".article");

        VersionedContentId versionContentId;
        String articleContentId = "";
        try {
            versionContentId =
                cmServer.findContentIdByExternalId(new ExternalContentId(DeleteVersionToolIT.class.getName()
                                                                         + ".article"));
            articleContentId = versionContentId.getContentId().getContentId().getContentId().getContentIdString();
        } catch (CMException e) {
            e.printStackTrace();
        }

        DefaultArguments arguments1 = new DefaultArguments("InspectTool", new HashMap<String, List<String>>(), args);
        arguments1.setContext(context);

        Main.execute(new InspectTool(), context, arguments1);
        assertTrue(out.toString().contains("name:" + DeleteVersionToolIT.class.getName()));

        DefaultArguments arguments =
            new DefaultArguments("DeleteVersionTool", new HashMap<String, List<String>>(), args);
        arguments.setContext(context);

        Main.execute(new DeleteVersionTool(), context, arguments);
        assertTrue(out.toString().contains(articleContentId));
    }

    @ImportTestContent(files = { "com.polopoly.pcmd.tool.DeleteVersionToolIT.article1.content",
                                "com.polopoly.pcmd.tool.DeleteVersionToolIT.article2.content" }, waitUntilContentsAreIndexed = { "com.polopoly.pcmd.tool.DeleteVersionToolIT.article1" })
    @Test
    public void deleteSpecifiedVersionTest() throws FatalToolException, ArgumentException {
        List<String> args1 = new ArrayList<String>();
        args1.add(DeleteVersionToolIT.class.getName() + ".article1");

        DefaultArguments arguments1 = new DefaultArguments("InspectTool", new HashMap<String, List<String>>(), args1);
        arguments1.setContext(context);

        Main.execute(new InspectTool(), context, arguments1);
        assertTrue(out.toString().contains("name:" + DeleteVersionToolIT.class.getName()));

        VersionedContentId versionContentId;
        String articleContentId = "";
        try {
            versionContentId =
                cmServer.findContentIdByExternalId(new ExternalContentId(DeleteVersionToolIT.class.getName()
                                                                         + ".article1"));
            articleContentId = versionContentId.getContentId().getContentIdString();
        } catch (CMException e) {
            e.printStackTrace();
        }

        List<String> args = new ArrayList<String>();
        args.add(articleContentId);

        DefaultArguments arguments =
            new DefaultArguments("DeleteVersionTool", new HashMap<String, List<String>>(), args);
        arguments.setContext(context);

        Main.execute(new DeleteVersionTool(), context, arguments);
        assertTrue(out.toString().contains(articleContentId));
        deleteImportedTestContent();

    }

    public void deleteImportedTestContent() throws ArgumentException {
        List<String> args = new ArrayList<String>();
        args.add(DeleteVersionToolIT.class.getName() + ".article1");

        DefaultArguments arguments = new DefaultArguments("DeleteTool", new HashMap<String, List<String>>(), args);
        arguments.setContext(context);

        Main.execute(new DeleteTool(), context, arguments);
    }

}
