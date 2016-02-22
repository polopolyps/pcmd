package com.polopoly.pcmd.tool;

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
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.ps.pcmd.Main;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.DefaultArguments;
import com.polopoly.ps.pcmd.tool.CheckPermissionTool;
import com.polopoly.testbase.ImportTestContent;
import com.polopoly.user.server.Caller;
import com.polopoly.user.server.UserServer;
import com.polopoly.util.client.PolopolyContext;

@ImportTestContent(files = { "CheckPermissionToolIT.xml", "CheckPermissionToolIT_permission.xml" })
public class CheckPermissionToolIT extends AbstractIntegrationTestBase {
    private PolopolyContext context;
    private StringBuffer out;

    @Inject
    private PolicyCMServer cmServer;

    @Inject
    private UserServer userServer;
    
    private Caller testCaller; 
    
    @Before
    public void setup() {
        context = new PolopolyContext(userServer, cmServer);
        out = new StringBuffer();
        System.setOut(new PrintStream(new StringBufferOutputStream(out)));
        
        testCaller = login(userServer, "checkpermissiontooluser1", "checkpermissiontooluser1");
        cmServer.setCurrentCaller(testCaller);
    }
    
    @After
    public void resetLoginDetail() { 
    	logout(userServer, testCaller);
    	
    	// to enable import of next test case's xml/content file
    	cmServer.setCurrentCaller(login(userServer, DEFAULT_USER,  DEFAULT_PASSWORD));
    }

    @Test
    public void checkUserWithValidPermission() throws FatalToolException, ArgumentException {
        List<String> args = new ArrayList<String>();
        args.add("GreenfieldTimes.d");

        HashMap<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("permission", Arrays.asList("1READ"));
        
        DefaultArguments arguments = new DefaultArguments("check-permission", options, args);
        arguments.setContext(context);
        arguments.setOptionString("loginuser", "checkpermissiontooluser1");

        Main.execute(new CheckPermissionTool(), context, arguments);
        
        assertTrue(out.toString().contains("has permission 1READ"));
    }

    @Test
    public void checkUserWithInvalidPermission() throws FatalToolException, ArgumentException {
    	
        List<String> args = new ArrayList<String>();
        args.add("GreenfieldTimes.d");

        HashMap<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("permission", Arrays.asList("3WRITE"));
        
        DefaultArguments arguments = new DefaultArguments("check-permission", options, args);
        arguments.setContext(context);
        arguments.setOptionString("loginuser", "checkpermissiontooluser1");

        Main.execute(new CheckPermissionTool(), context, arguments);
        
        assertTrue(out.toString().contains("does not have permission 3WRITE."));
    }
}
