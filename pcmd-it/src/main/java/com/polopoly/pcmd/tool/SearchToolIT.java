package com.polopoly.pcmd.tool;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
import com.polopoly.cm.ContentId;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.ps.pcmd.Main;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.DefaultArguments;
import com.polopoly.ps.pcmd.tool.SearchTool;
import com.polopoly.testnext.base.ImportTestContent;
import com.polopoly.user.server.UserServer;
import com.polopoly.util.client.PolopolyContext;
import com.polopolyps.pcmd.test.ImagePolicy;
import com.polopolyps.pcmd.test.StandardArticlePolicy;


@ImportTestContent(files = { "templates/image.xml", "com.polopoly.pcmd.tool.SearchToolIT.content" })
public class SearchToolIT extends AbstractIntegrationTestBase {

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

    private void setupSystemErr() {
        err = new StringBuffer();
        System.setErr(new PrintStream(new StringBufferOutputStream(err)));
    }

    @Test
    public void inputTemplateTest() throws ArgumentException, CMException {
        setup();

        List<String> args = new ArrayList<String>();

        Map<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("inputtemplate", Arrays.asList("pcmd.StandardArticle"));

        DefaultArguments arguments = new DefaultArguments("SearchTool", options, args);
        arguments.setContext(context);

        Main.execute(new SearchTool(), context, arguments);

        ImagePolicy imagePolicy =
            (ImagePolicy) cmServer.getPolicy(new ExternalContentId(SearchToolIT.class.getName() + ".image"));
        assertFalse(out.toString().contains(imagePolicy.getContentId().getContentId().getContentIdString()));

        assertTrue(out.toString().contains(SearchToolIT.class.getName() + ".article"));
    }

    @Test
    public void majorTest() throws ArgumentException {
        List<String> args = new ArrayList<String>();

        Map<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("major", Arrays.asList("1"));

        DefaultArguments arguments = new DefaultArguments("SearchTool", options, args);
        arguments.setContext(context);

        Main.execute(new SearchTool(), context, arguments);
        assertTrue(out.toString().contains(SearchToolIT.class.getName() + ".article"));
    }

    @Test
    public void resolveIdTest() throws ArgumentException, CMException {
        List<String> args = new ArrayList<String>();

        Map<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("inputtemplate", Arrays.asList("pcmd.StandardArticle"));
        options.put("resolveid", Arrays.asList("false"));

        DefaultArguments arguments = new DefaultArguments("SearchTool", options, args);
        arguments.setContext(context);

        Main.execute(new SearchTool(), context, arguments);

        StandardArticlePolicy articlePolicy =
            (StandardArticlePolicy) cmServer
                .getPolicy(new ExternalContentId(SearchToolIT.class.getName() + ".article"));
        assertTrue(out.toString().contains(articlePolicy.getContentId().getContentId().getContentIdString()));
    }

    @Test
    public void componentAndComponentValueTest() throws ArgumentException {
        List<String> args = new ArrayList<String>();

        Map<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("inputtemplate", Arrays.asList("pcmd.StandardArticle"));
        options.put("component", Arrays.asList("author:value"));
        options.put("componentvalue", Arrays.asList("Andy Miller"));

        DefaultArguments arguments = new DefaultArguments("SearchTool", options, args);
        arguments.setContext(context);

        Main.execute(new SearchTool(), context, arguments);

        assertTrue(out.toString().contains(SearchToolIT.class.getName() + ".article"));
    }

    @Test
    public void refersToTest() throws ArgumentException, CMException {
        List<String> args = new ArrayList<String>();

        Map<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("refersto", Arrays.asList(SearchToolIT.class.getName() + ".image"));

        DefaultArguments arguments = new DefaultArguments("SearchTool", options, args);
        arguments.setContext(context);

        Main.execute(new SearchTool(), context, arguments);

        StandardArticlePolicy articlePolicy =
            (StandardArticlePolicy) cmServer
                .getPolicy(new ExternalContentId(SearchToolIT.class.getName() + ".article"));

        ContentId imageMetadataRef = articlePolicy.getContentReference("images", "0");
        
        assertNotNull(imageMetadataRef);
        
       String externalId = articlePolicy.getContent().getExternalId().getExternalId();
        
        
        String string = out.toString();
		assertTrue(string.contains(externalId));
    }

    @Test
    public void batchSizeTest() throws ArgumentException {
        setupSystemErr();
        List<String> args = new ArrayList<String>();

        int batchsize = 5;
        int secondBatchStart = batchsize + 1;
        int secondBatchEnd = batchsize * 2;
        int invalidSecondBatchStart = batchsize + 2;

        Map<String, List<String>> options = new HashMap<String, List<String>>();

        options.put("inputtemplate", Arrays.asList("pcmd.StandardArticle"));
        options.put("batchsize", Arrays.asList(String.valueOf(batchsize)));

        DefaultArguments arguments = new DefaultArguments("SearchTool", options, args);
        arguments.setContext(context);

        Main.execute(new SearchTool(), context, arguments);
        assertTrue(err.toString().contains(secondBatchStart + " to " + secondBatchEnd));
        assertFalse(err.toString().contains(invalidSecondBatchStart + " to " + secondBatchEnd));
    }

    @Test
    public void sinceVersionTest() throws CMException, ArgumentException {
        List<String> args = new ArrayList<String>();

        ImagePolicy imagePolicy =
            (ImagePolicy) cmServer.getPolicy(new ExternalContentId(SearchToolIT.class.getName() + ".image"));

        int version = imagePolicy.getVersionInfo().getVersion();

        Map<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("sinceversion", Arrays.asList(String.valueOf(version)));

        DefaultArguments arguments = new DefaultArguments("SearchTool", options, args);
        arguments.setContext(context);

        Main.execute(new SearchTool(), context, arguments);

        StandardArticlePolicy articlePolicy =
            (StandardArticlePolicy) cmServer
                .getPolicy(new ExternalContentId(SearchToolIT.class.getName() + ".article"));

        ContentId imageMetadataRef = articlePolicy.getContentReference("images", "0");
        Policy policy = cmServer.getPolicy(imageMetadataRef.getContentId().getContentId());
        
        String externalId = policy.getContent().getExternalId().getExternalId();

        String string = out.toString();
        
		assertTrue(string.contains(externalId));
    }

    @Test
    public void untilVersionTest() throws CMException, ArgumentException {
        StandardArticlePolicy articlePolicy =
            (StandardArticlePolicy) cmServer
                .getPolicy(new ExternalContentId(SearchToolIT.class.getName() + ".article"));

        int version = articlePolicy.getVersionInfo().getVersion();

        List<String> args = new ArrayList<String>();

        Map<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("inputtemplate", Arrays.asList("pcmd.StandardArticle"));
        options.put("untilversion", Arrays.asList(String.valueOf(version)));

        DefaultArguments arguments = new DefaultArguments("SearchTool", options, args);
        arguments.setContext(context);

        Main.execute(new SearchTool(), context, arguments);
        assertTrue(out.toString().contains(SearchToolIT.class.getName() + ".article"));
    }
}
