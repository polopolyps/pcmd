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
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.ps.pcmd.Main;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.DefaultArguments;
import com.polopoly.ps.pcmd.tool.CheckPermissionTool;
import com.polopoly.testbase.ImportTestContent;
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

    @Before
    public void setup() {
        context = new PolopolyContext(userServer, cmServer);
        out = new StringBuffer();
        System.setOut(new PrintStream(new StringBufferOutputStream(out)));
    }

    @Test
    public void checkUserWithValidPermission() throws FatalToolException, ArgumentException {
        List<String> args = new ArrayList<String>();
        args.add("GreenfieldTimes.d");

        HashMap<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("permission", Arrays.asList(new String[] { "1READ" }));

        DefaultArguments arguments = new DefaultArguments("check-permission", options, args);
        arguments.setContext(context);
        arguments.setOptionString("loginuser", "checkpermissiontooluser1");

        Main.execute(new CheckPermissionTool(), context, arguments, true);
        assertTrue(out.toString().contains("checkpermissiontooluser1 has permission 1READ"));
    }

    public void checkUserWithInvalidPermission() throws FatalToolException, ArgumentException {
        List<String> args = new ArrayList<String>();
        args.add("GreenfieldTimes.d");

        HashMap<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("permission", Arrays.asList(new String[] { "1WRITE" }));

        DefaultArguments arguments = new DefaultArguments("CheckPermissionTool", options, args);
        arguments.setContext(context);
        arguments.setOptionString("loginuser", "checkpermissiontooluser2");

        Main.execute(new CheckPermissionTool(), context, arguments, true);
        assertTrue(out.toString().contains("checkpermissiontooluser2 does not have permission 1WRITE"));
    }
}
