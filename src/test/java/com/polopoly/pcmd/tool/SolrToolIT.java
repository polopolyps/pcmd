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
import com.polopoly.ps.pcmd.tool.SolrTool;
import com.polopoly.search.solr.PostFilteredSolrSearchClient;
import com.polopoly.search.solr.SolrSearchClient;
import com.polopoly.testbase.ImportTestContent;
import com.polopoly.util.client.PolopolyContext;

@ImportTestContent(waitUntilContentsAreIndexed={"com.polopoly.pcmd.tool.SolrToolIT.article"})
public class SolrToolIT extends AbstractIntegrationTestBase {

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
    	if(internalIndex)
    		indexClientMap.put("internal", solrClientInternal);
    	if(publicIndex)
    		indexClientMap.put("public", solrClientPublic);
        
    	context = new PolopolyContext(cmServer, indexClientMap);
        out = new StringBuffer();
        System.setOut(new PrintStream(new StringBufferOutputStream(out)));
    }

    @Test
    public void solrSearchTest() throws FatalToolException, ArgumentException {
    	setup(false, true);
        List<String> args = new ArrayList<String>();
        args.add(SolrToolIT.class.getName());

        DefaultArguments arguments = new DefaultArguments("SolrTool", new HashMap<String, List<String>>(), args);
        arguments.setContext(context);

        Main.execute(new SolrTool(), context, arguments);
        assertTrue(out.toString().contains(SolrToolIT.class.getName() + ".article"));
    }
    
    @Test
    public void solrSearchDifferentIndexTest() throws FatalToolException, ArgumentException {
    	setup(true, false);
        List<String> args = new ArrayList<String>();
        args.add(SolrToolIT.class.getName());

        HashMap<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("index", Arrays.asList(new String[]{"internal"}));
        DefaultArguments arguments = new DefaultArguments("SolrTool", options , args);
        arguments.setContext(context);

        Main.execute(new SolrTool(), context, arguments);
        assertTrue(out.toString().contains(SolrToolIT.class.getName() + ".article"));
    }
}

