package com.polopoly.pcmd.tool;

import static org.junit.Assert.assertTrue;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.polopoly.ps.pcmd.tool.WorkflowActionsTool;
import com.polopoly.testbase.ImportTestContent;
import com.polopoly.user.server.UserServer;
import com.polopoly.util.client.PolopolyContext;

@ImportTestContent(files = { "WorkflowActionsToolIT_siteDepts.xml", "WorkflowActionsToolIT_simpleWorkflow.xml" }, waitUntilContentsAreIndexed = {
                                                                                                                                                 "com.polopoly.pcmd.tool.WorkflowActionsToolIT.article",
                                                                                                                                                 "com.polopoly.pcmd.tool.WorkflowActionsToolIT.article2" })
public class WorkflowActionsToolIT extends AbstractIntegrationTestBase {
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
    public void performActionTest() throws FatalToolException, ArgumentException {
        List<String> args = new ArrayList<String>();
        VersionedContentId versionContentId;
        String articleContentId = "";
        try {
            versionContentId =
                cmServer.findContentIdByExternalId(new ExternalContentId(
                    "com.polopoly.pcmd.tool.WorkflowActionsToolIT.article"));
            articleContentId = versionContentId.getContentId().getContentIdString();
        } catch (CMException e) {
            e.printStackTrace();
        }
        args.add(articleContentId);

        HashMap<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("perform", Arrays.asList(new String[] { "approve" }));

        DefaultArguments arguments = new DefaultArguments("WorkflowActionsTool", options, args);
        arguments.setContext(context);
        arguments.setOptionString("loginuser", "sysadmin");
        arguments.setOptionString("loginpassword", "sysadmin");

        Main.execute(new WorkflowActionsTool(), context, arguments, true);
        assertTrue(out.toString().contains(":commit, remove"));
        deleteArticleAfterTest("com.polopoly.pcmd.tool.WorkflowActionsToolIT.article");
    }

    @Test
    public void noWorkflowTest() throws FatalToolException, ArgumentException {
        List<String> args = new ArrayList<String>();
        VersionedContentId versionContentId;
        String articleContentId = "";
        try {
            versionContentId =
                cmServer.findContentIdByExternalId(new ExternalContentId(
                    "com.polopoly.pcmd.tool.WorkflowActionsToolIT.article2"));
            articleContentId = versionContentId.getContentId().getContentIdString();
        } catch (CMException e) {
            e.printStackTrace();
        }
        args.add(articleContentId);

        DefaultArguments arguments =
            new DefaultArguments("WorkflowActionsTool", new HashMap<String, List<String>>(), args);
        arguments.setContext(context);
        arguments.setOptionString("loginuser", "sysadmin");
        arguments.setOptionString("loginpassword", "sysadmin");

        Main.execute(new WorkflowActionsTool(), context, arguments, true);
        assertTrue(out.toString().trim().equals("com.polopoly.pcmd.tool.WorkflowActionsToolIT.article2:"));
        deleteArticleAfterTest("com.polopoly.pcmd.tool.WorkflowActionsToolIT.article2");
    }

    private void deleteArticleAfterTest(String articleExternalId) {
        try {
            VersionedContentId id = cmServer.findContentIdByExternalId(new ExternalContentId(articleExternalId));
            cmServer.removeContent(id.getContentId().getContentId());

            System.out.println(id.getContentIdString());
        } catch (CMException e) {
            System.err.println(e.getMessage());

        }
    }

}
