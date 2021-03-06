package com.polopoly.pcmd.tool;

import static org.junit.Assert.assertTrue;

import java.io.PrintStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.ejb.FinderException;

import org.apache.geronimo.mail.util.StringBufferOutputStream;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.ps.pcmd.Main;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.DefaultArguments;
import com.polopoly.ps.pcmd.tool.AddGroupMemberTool;
import com.polopoly.testnext.base.ImportTestContent;
import com.polopoly.user.server.Caller;
import com.polopoly.user.server.Group;
import com.polopoly.user.server.GroupId;
import com.polopoly.user.server.InvalidSessionKeyException;
import com.polopoly.user.server.PermissionDeniedException;
import com.polopoly.user.server.PrincipalId;
import com.polopoly.user.server.User;
import com.polopoly.user.server.UserServer;
import com.polopoly.util.client.PolopolyContext;

public class AddGroupMemberToolIT extends AbstractIntegrationTestBase {
    private static final String TEST_GROUP2 = "AddGroupMemberToolIT_Group2";
	private static final String TESTADDGROUPMEMBER = "testaddgroupmember";
	private static final String TEST_GROUP1 = "AddGroupMemberToolIT_Group1";
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

    @ImportTestContent(files = { "AddGroupMemberToolIT_group1.xml", "AddGroupMemberToolIT_user.xml" })
    @Test
    public void addGroupMemberTest() throws FatalToolException, ArgumentException, RemoteException, FinderException {
        List<String> args = new ArrayList<String>();
        args.add(TESTADDGROUPMEMBER);

        HashMap<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("group", Arrays.asList(new String[] { TEST_GROUP1 }));

        DefaultArguments arguments = new DefaultArguments("AddGroupMemberTool", options, args);
        arguments.setContext(context);
        arguments.setOptionString("loginpassword", "sysadmin");

        Main.execute(new AddGroupMemberTool(), context, arguments);
        assertTrue("Tool output did not contain " + TESTADDGROUPMEMBER, out.toString().contains(TESTADDGROUPMEMBER));
        
        Group group = getGroup(TEST_GROUP1);
        User testUser = userServer.getUserByLoginName(TESTADDGROUPMEMBER);
        assertTrue("User " + TESTADDGROUPMEMBER + " is not a member of group " + TEST_GROUP1, group.isDirectMember(testUser.getUserId()));
        removeGroupMemberAfterTest(TESTADDGROUPMEMBER, TEST_GROUP1, false);
    }

    @ImportTestContent(files = { "AddGroupMemberToolIT_group1.xml", "AddGroupMemberToolIT_group2.xml" })
    @Test
    public void addGroupTest() throws FatalToolException, ArgumentException, RemoteException, FinderException {
        List<String> args = new ArrayList<String>();
        args.add(TEST_GROUP1);

        HashMap<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("group", Arrays.asList(new String[] { TEST_GROUP2 }));

        DefaultArguments arguments = new DefaultArguments("AddGroupMemberTool", options, args);
        arguments.setContext(context);
        arguments.setOptionString("loginpassword", "sysadmin");

        Main.execute(new AddGroupMemberTool(), context, arguments);
        assertTrue("Tool output did not contain: " + TEST_GROUP1, out.toString().contains(TEST_GROUP1));
     
        Group group1 = getGroup(TEST_GROUP1);
        Group group2 = getGroup(TEST_GROUP2);
        
        assertTrue("Group " + TEST_GROUP1 + " is not a member of " + TEST_GROUP2, group2.isDirectMember(group1.getGroupId()));
        removeGroupMemberAfterTest(TEST_GROUP1, TEST_GROUP2, true);
    }

    private void removeGroupMemberAfterTest(String groupMember, String groupName, boolean isGroup) {
        Caller currentCaller = cmServer.getCurrentCaller();
        PrincipalId principalId = null;
        Group group = null;
        try {
            GroupId[] mainGroups = userServer.findGroupsByName(groupName);
            if (mainGroups.length > 0) {
                group = userServer.findGroup(mainGroups[0]);
            }

            if (isGroup) {
                GroupId[] groups = userServer.findGroupsByName(groupMember);
                if (groups.length > 0) {
                    Group memberGroup = userServer.findGroup(groups[0]);
                    principalId = memberGroup.getGroupId();
                }
            } else {
                principalId = userServer.getUserByLoginName(groupMember).getUserId();
            }
            group.removeMember(principalId, currentCaller);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (FinderException e) {
            e.printStackTrace();
        } catch (PermissionDeniedException e) {
            e.printStackTrace();
        } catch (InvalidSessionKeyException e) {
            e.printStackTrace();
        }

    }

    private Group getGroup(String groupName) throws RemoteException, FinderException {
		GroupId[] groupId = userServer.findGroupsByName(groupName);
        assertTrue("Failed to find test group for validation: " + groupName, groupId.length == 1);
        Group group = userServer.findGroup(groupId[0]);
		return group;
	}
}
