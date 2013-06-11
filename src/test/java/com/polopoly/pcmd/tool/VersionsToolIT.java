package com.polopoly.pcmd.tool;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.geronimo.mail.util.StringBufferOutputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.polopoly.cm.ContentId;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.ps.pcmd.Main;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.DefaultArguments;
import com.polopoly.ps.pcmd.tool.VersionsTool;
import com.polopoly.testbase.ImportTestContent;
import com.polopoly.util.client.PolopolyContext;

public class VersionsToolIT extends AbstractIntegrationTestBase {
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
    
    @After
    public void cleanUp() throws CMException {
    	ContentId contentId = new ExternalContentId("com.polopoly.pcmd.tool.VersionsToolIT.article1"); 
    	cmServer.removeContent(contentId); 
    }

    @ImportTestContent(files = { "com.polopoly.pcmd.tool.VersionsToolIT.article1.content" }, 
    		           waitUntilContentsAreIndexed = { "com.polopoly.pcmd.tool.VersionsToolIT.article1" })
    @Test
    public void checkWithSymbolicVersions() throws FatalToolException, ArgumentException {
        List<String> args = new ArrayList<String>();
        args.add("com.polopoly.pcmd.tool.VersionsToolIT.article1");

        DefaultArguments arguments = new DefaultArguments("VersionsTool", new HashMap<String, List<String>>(), args);
        arguments.setContext(context);

        VersionedContentId versionContentId;
        String articleContentId = "";
        try {
            versionContentId =
                cmServer.findContentIdByExternalId(new ExternalContentId(
                    "com.polopoly.pcmd.tool.VersionsToolIT.article1"));
            articleContentId = versionContentId.getContentId().getContentId().getContentId().getContentIdString();
        } catch (CMException e) {
            e.printStackTrace();
        }

        Main.execute(new VersionsTool(), context, arguments);
        String result = out.toString();

        assertTrue(result.contains(articleContentId));
        assertTrue(result.contains("LATEST"));
        assertTrue(result.contains("DEFAULT_STAGE"));
    }

    @ImportTestContent(files = { "com.polopoly.pcmd.tool.VersionsToolIT.article1.content",
                                 "com.polopoly.pcmd.tool.VersionsToolIT.article2.content" }, 
                       waitUntilContentsAreIndexed = { "com.polopoly.pcmd.tool.VersionsToolIT.article1" })
    @Test
    public void checkWithoutSymbolicVersions() throws FatalToolException, ArgumentException {
        List<String> args = new ArrayList<String>();
        args.add("com.polopoly.pcmd.tool.VersionsToolIT.article1");

        HashMap<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("symbolic", Arrays.asList(new String[] { "false" }));

        DefaultArguments arguments = new DefaultArguments("VersionsTool", options, args);
        arguments.setContext(context);

        VersionedContentId versionContentId;
        String articleContentId = "";
        try {
            versionContentId =
                cmServer.findContentIdByExternalId(new ExternalContentId(
                    "com.polopoly.pcmd.tool.VersionsToolIT.article1"));
            articleContentId = versionContentId.getContentId().getContentId().getContentId().getContentIdString();
        } catch (CMException e) {
            e.printStackTrace();
        }

        Main.execute(new VersionsTool(), context, arguments);
        
        String result = out.toString();
        
        assertTrue(result.contains(articleContentId));
        assertFalse(result.contains("LATEST"));
        assertFalse(result.contains("DEFAULT_STAGE"));
    }
}
