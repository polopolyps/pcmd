package com.polopoly.pcmd.tool;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.geronimo.mail.util.StringBufferOutputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.ps.pcmd.Main;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.DefaultArguments;
import com.polopoly.ps.pcmd.tool.SetPermissionTool;
import com.polopoly.testnext.base.ImportTestContent;
import com.polopoly.user.server.Caller;
import com.polopoly.user.server.UserId;
import com.polopoly.user.server.UserServer;
import com.polopoly.util.client.PolopolyContext;

@ImportTestContent(files = "com.polopoly.pcmd.tool.SetPermissionToolIT.xml")
public class SetPermissionToolGroupIT extends AbstractIntegrationTestBase {
    private PolopolyContext context;
    private StringBuffer out;

    @Inject
    private PolicyCMServer cmServer;

    @Inject
    private UserServer userServer;

    @Before
    public void setup() throws ArgumentException {
        context = new PolopolyContext(userServer, cmServer);
        out = new StringBuffer();
        System.setOut(new PrintStream(new StringBufferOutputStream(out)));

        createGroupPermission();
    }
    
    @After
    public void resetLoginDetail() { // to enable import of next test case's xml/content file
    	cmServer.setCurrentCaller(login(userServer, DEFAULT_USER,  DEFAULT_PASSWORD));
    }

    public void createGroupPermission() throws ArgumentException {
        List<String> args = new ArrayList<String>();
        args.add("GreenfieldTimes.d");

        Map<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("group", Arrays.asList("Test Tool Group"));
        options.put("permission", Arrays.asList("2WRITE"));

        DefaultArguments arguments = new DefaultArguments("SetPermissionTool", options, args);
        arguments.setContext(context);

        Main.execute(new SetPermissionTool(), context, arguments);
    }

    @Test
    public void checkGroupPermissionTest() throws CMException {
        // default caller is sysadmin (98) - but we are not checking for
        // sysadmin's rights, instead checking for user 6001's in the Test Tool
        // Group
        cmServer.setCurrentCaller(new Caller(new UserId("6001")));

        assertTrue(cmServer.checkPermission(new ExternalContentId("GreenfieldTimes.d"), "2WRITE", false));
        assertFalse(cmServer.checkPermission(new ExternalContentId("GreenfieldTimes.d"), "2READ", false));
    }
}
