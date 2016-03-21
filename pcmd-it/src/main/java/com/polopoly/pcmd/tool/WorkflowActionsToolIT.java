package com.polopoly.pcmd.tool;

import static org.junit.Assert.assertTrue;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.apache.geronimo.mail.util.StringBufferOutputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.polopoly.cm.ContentId;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.ps.pcmd.Main;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.DefaultArguments;
import com.polopoly.ps.pcmd.tool.WorkflowActionsTool;
import com.polopoly.testnext.base.ImportTestContent;
import com.polopoly.user.server.UserServer;
import com.polopoly.util.client.PolopolyContext;

//@ImportTestContent(files = { "com.polopoly.pcmd.tool.WorkflowActionsToolIT.xml", 
//		                     "com.polopoly.pcmd.tool.WorkflowActionsToolITArticle.xml"}, 
//                   waitUntilContentsAreIndexed = { "com.polopoly.pcmd.tool.WorkflowActionsToolIT.article", 
//	                                               "com.polopoly.pcmd.tool.WorkflowActionsToolIT.article2" })

@ImportTestContent
public class WorkflowActionsToolIT extends AbstractIntegrationTestBase {
	
	private PolopolyContext context;
	private StringBuffer out;

	@Inject 
	private Logger LOG; 
	
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

	@After
	public void cleanUp() throws CMException {
		ContentId workflowDeptId = new ExternalContentId("WorkflowActions.d"); 
		
		if(cmServer.contentExists(workflowDeptId)){
			LOG.info("Removing WorkflowActions.d..."); 
			cmServer.removeContent(workflowDeptId);
		}
	}
	/**
	 * tests on a WorkflowAction.d article - default with workflow
	 */
	@Test
	public void performActionTest() throws FatalToolException, ArgumentException {
		String testArticle = "com.polopoly.pcmd.tool.WorkflowActionsToolIT.article"; 

		List<String> args = new ArrayList<String>();
		ContentId contentId = new ExternalContentId(testArticle); 

		try {
			if(cmServer.contentExists(contentId)) {
				args.add(cmServer.getPolicy(contentId).getContentId().getContentId().getContentIdString());
			}
		} catch (CMException e) {
			e.printStackTrace();
		}

		HashMap<String, List<String>> options = new HashMap<String, List<String>>();
		options.put("perform", Arrays.asList("approve"));

		DefaultArguments arguments = new DefaultArguments("WorkflowActionsTool", options, args);
		arguments.setContext(context);
		arguments.setOptionString("loginuser", "sysadmin");

		Main.execute(new WorkflowActionsTool(), context, arguments);
		
		String result = out.toString(); 

		assertTrue(result.contains(":commit, remove"));
		cleanUpArticle(testArticle);
	}

	/**
	 * tests on a GreenfieldTimes.d article - default no workflow
	 */
	@Test
	public void noWorkflowTest() throws FatalToolException, ArgumentException {

		String testArticle = "com.polopoly.pcmd.tool.WorkflowActionsToolIT.article2"; 

		List<String> args = new ArrayList<String>();
		ContentId contentId = new ExternalContentId(testArticle);

		try {
			if(cmServer.contentExists(contentId)) {
				args.add(cmServer.getPolicy(contentId).getContentId().getContentId().getContentIdString());
			}
		} catch (CMException e) {
			e.printStackTrace();
		}

		DefaultArguments arguments =
				new DefaultArguments("WorkflowActionsTool", new HashMap<String, List<String>>(), args);
		arguments.setContext(context);
		arguments.setOptionString("loginuser", "sysadmin");

		Main.execute(new WorkflowActionsTool(), context, arguments);

		assertTrue(out.toString().trim().equals(testArticle + ":"));
		cleanUpArticle(testArticle);
	}

	private void cleanUpArticle(String articleExternalId) {
		ContentId contentId = new ExternalContentId(articleExternalId);

		try {
			if(cmServer.contentExists(contentId)) {
				cmServer.removeContent(contentId); 
			}
		} catch (CMException ex) {
			ex.printStackTrace(); 
		}
	}

}
