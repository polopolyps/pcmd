package com.polopoly.ps.pcmd.tool;

import static com.polopoly.ps.pcmd.parser.ContentFieldListParser.PREFIX_FIELD_SEPARATOR;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.polopoly.pcmd.tool.Tool;
import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.ps.pcmd.parser.ContentFieldListParser;
import com.polopoly.user.server.Caller;
import com.polopoly.user.server.Group;
import com.polopoly.user.server.GroupId;
import com.polopoly.user.server.InvalidSessionKeyException;
import com.polopoly.user.server.PermissionDeniedException;
import com.polopoly.user.server.User;
import com.polopoly.user.server.UserId;
import com.polopoly.user.server.UserServer;
import com.polopoly.user.server.jsp.UserFactory;
import com.polopoly.util.client.PolopolyContext;

public class UserTool implements Tool<UserParameters> {

	private static final Logger LOG = Logger.getLogger(UserTool.class.getName());

	public UserParameters createParameters() {
		return new UserParameters();
	}

	public void execute(PolopolyContext context, UserParameters parameters) throws FatalToolException {
		if (parameters.getUsers().isEmpty()) {
			printAllUsers(context);
		} else {
			for (User user : parameters.getUsers()) {
				printUser(user, context);
			}
		}
	}

	private void printAllUsers(PolopolyContext context) throws FatalToolException {
		UserServer userServer = context.getUserServer();

		try {

			UserId[] ids = userServer.findUserIdsByAttributeValue("user", "loginName", "%", 
					context.getPolicyCMServer().getCurrentCaller());

			for (UserId userId : ids) {
				printUser(userId, context, userServer);
			}
		} catch (Exception e) {
			throw new FatalToolException("Something unexpected when getting all users", e);
		}
	}

	private void printUser(UserId userId, PolopolyContext context, UserServer userServer) throws FatalToolException {
		try {
			User user = context.getUserServer().getUserByUserId(userId);

			printUser(user, context);
			printGroups(context, userId);
		} catch (RemoteException e) {
			throw new FatalToolException(e);
		} catch (CreateException e) {
			throw new FatalToolException(e);
		}
	}

	private void printGroups(PolopolyContext context, UserId userId) throws FatalToolException {
		context.getLogger().debug("Printing groups");
		UserServer userServer = context.getUserServer();
		GroupId[] findGroupsByMember;
		try {
			findGroupsByMember = userServer.findGroupsByMember(userId);
			for (GroupId groupId : findGroupsByMember) {
				try {
					Group findGroup = userServer.findGroup(groupId);
					System.out.println("group:" + findGroup.getGroupId().getGroupIdInt() + ":" + findGroup.getName());
				} catch (FinderException e) {
					throw new FatalToolException(e);
				}
			}
		} catch (RemoteException e) {
			throw new FatalToolException(e);
		}

	}

	private void printUser(User user, PolopolyContext context) {
		try {
			System.out.println(
					ContentFieldListParser.ID + PREFIX_FIELD_SEPARATOR + user.getUserId().getPrincipalIdString());
			System.out.println("loginname: " + user.getLoginName());

			if (user.isLdapUser()) {
				System.out.println("(is LDAP user)");
			}

			Caller caller = context.getPolicyCMServer().getCurrentCaller();

			String[] groupNames = user.getPersistentGroupNames(caller);

			for (String groupName : groupNames) {
				String[] attributeNames = user.getPersistentAttributeNames(groupName, caller);

				for (String attributeName : attributeNames) {
					try {
						String value = user.getPersistent(groupName, attributeName, caller);

						System.out.println(ContentFieldListParser.COMPONENT + PREFIX_FIELD_SEPARATOR + groupName + ':'
								+ attributeName + '=' + value);
					} catch (PermissionDeniedException e) {
						System.err.println(e.toString());
					} catch (InvalidSessionKeyException e) {
						System.err.println(e.toString());
					}
				}
			}

			printGroups(context, user.getUserId());
		} catch (RemoteException e) {
			System.err.println(e.toString());
		}
	}

	public String getHelp() {
		return "Prints information on the users in the system.";
	}

}
