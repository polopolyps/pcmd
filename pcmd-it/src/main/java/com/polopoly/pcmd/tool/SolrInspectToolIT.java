package com.polopoly.pcmd.tool;

import static org.junit.Assert.assertTrue;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.geronimo.mail.util.StringBufferOutputStream;
import org.junit.Test;

import com.google.inject.Inject;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.ps.pcmd.Main;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.DefaultArguments;
import com.polopoly.ps.pcmd.tool.SolrInspectTool;
import com.polopoly.search.solr.PostFilteredSolrSearchClient;
import com.polopoly.search.solr.SolrSearchClient;
import com.polopoly.testnext.base.ImportTestContent;
import com.polopoly.util.client.PolopolyContext;

@ImportTestContent(files = { "com.polopoly.pcmd.tool.SolrInspectToolIT.article.content" }, waitUntilContentsAreIndexed = { "com.polopoly.pcmd.tool.SolrInspectToolIT.article" })
public class SolrInspectToolIT extends AbstractIntegrationTestBase {

    private PolopolyContext context;
    private StringBuffer out;

    @Inject
    private PolicyCMServer cmServer;

    @Inject
    private SolrSearchClient solrClientPublic;

    @Inject
    private PostFilteredSolrSearchClient solrClientInternal;

    public void setup(boolean internalIndex, boolean publicIndex) {
        Map<String, SolrSearchClient> indexClientMap = new HashMap<String, SolrSearchClient>();
        if (internalIndex)
            indexClientMap.put("internal", solrClientInternal);
        if (publicIndex)
            indexClientMap.put("public", solrClientPublic);

        context = new PolopolyContext(cmServer, indexClientMap);
        out = new StringBuffer();
        System.setOut(new PrintStream(new StringBufferOutputStream(out)));

    }

    @Test
    public void solrInspectInPublicIndexTest() throws FatalToolException, ArgumentException {
        setup(false, true);
        List<String> args = new ArrayList<String>();
        args.add("com.polopoly.pcmd.tool.SolrInspectToolIT.article");

        HashMap<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("searchindex", Arrays.asList(new String[] { "public" }));

        DefaultArguments arguments = new DefaultArguments("SolrInspectTool", new HashMap<String, List<String>>(), args);
        arguments.setContext(context);

        Main.execute(new SolrInspectTool(), context, arguments);
        assertTrue(out.toString().contains("eventId"));
    }

    @Test
    public void solrInspectInInternalIndexTest() throws FatalToolException, ArgumentException {
        setup(true, false);
        List<String> args = new ArrayList<String>();
        args.add("com.polopoly.pcmd.tool.SolrInspectToolIT.article");

        HashMap<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("searchindex", Arrays.asList(new String[] { "internal" }));

        DefaultArguments arguments = new DefaultArguments("SolrInspectTool", options, args);

        arguments.setContext(context);

        Main.execute(new SolrInspectTool(), context, arguments);
        assertTrue(out.toString().contains("eventId"));
    }

    @Test
    public void solrInspectInvalidContentIdInPublicIndexTest() throws FatalToolException, ArgumentException {
        setup(false, true);
        List<String> args = new ArrayList<String>();
        args.add("1.1220");

        HashMap<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("searchindex", Arrays.asList(new String[] { "internal" }));

        DefaultArguments arguments = new DefaultArguments("SolrInspectTool", new HashMap<String, List<String>>(), args);
        arguments.setContext(context);

        Main.execute(new SolrInspectTool(), context, arguments);
        assertTrue(out.toString().contains("No index document for:"));
    }

}
