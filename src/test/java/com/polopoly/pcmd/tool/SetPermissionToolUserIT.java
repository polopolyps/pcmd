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
import com.polopoly.testbase.ImportTestContent;
import com.polopoly.user.server.Caller;
import com.polopoly.user.server.UserId;
import com.polopoly.user.server.UserServer;
import com.polopoly.util.client.PolopolyContext;

@ImportTestContent(files = "com.polopoly.pcmd.tool.SetPermissionToolIT.xml")
public class SetPermissionToolUserIT extends AbstractIntegrationTestBase {
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

        createUserPermission();
    }

    public void createUserPermission() throws ArgumentException {
        List<String> args = new ArrayList<String>();
        args.add("GreenfieldTimes.d");

        Map<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("user", Arrays.asList("testtooluser"));
        options.put("permission", Arrays.asList("3READ"));

        DefaultArguments arguments = new DefaultArguments("SetPermissionTool", options, args);
        arguments.setContext(context);
        Main.execute(new SetPermissionTool(), context, arguments);
    }

    @Test
    public void checkUserPermissionTest() throws CMException {
        // default caller is sysadmin (98) - but we are not checking for
        // sysadmin's rights, instead checking for user 6001's (testtooluser)
        cmServer.setCurrentCaller(new Caller(new UserId("6001")));

        assertTrue(cmServer.checkPermission(new ExternalContentId("GreenfieldTimes.d"), "3READ", false));
        assertFalse(cmServer.checkPermission(new ExternalContentId("GreenfieldTimes.d"), "3WRITE", false));
    }

}
