package com.polopoly.ps.pcmd.tool;

import static com.polopoly.ps.pcmd.parser.ContentFieldListParser.PREFIX_FIELD_SEPARATOR;
import static com.polopoly.util.policy.Util.util;

import java.io.IOException;
import java.util.Arrays;

import com.polopoly.cm.ContentFileInfo;
import com.polopoly.cm.ContentId;
import com.polopoly.cm.ContentReference;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.LockInfo;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.client.UserData;
import com.polopoly.cm.client.WorkflowAware;
import com.polopoly.pcmd.tool.Tool;
import com.polopoly.ps.pcmd.argument.InspectParameters;
import com.polopoly.ps.pcmd.field.content.AbstractContentIdField;
import com.polopoly.ps.pcmd.field.content.ContentRefField;
import com.polopoly.ps.pcmd.parser.ContentFieldListParser;
import com.polopoly.ps.pcmd.text.ComponentValueParser;
import com.polopoly.user.server.Group;
import com.polopoly.user.server.GroupId;
import com.polopoly.user.server.UserId;
import com.polopoly.user.server.UserServer;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.ContentIdToContentIterator;
import com.polopoly.util.content.ContentUtil;
import com.polopoly.util.contentid.MajorStrings;
import com.polopoly.util.contentlist.ContentListUtil;

public class InspectTool implements Tool<InspectParameters> {
	private static final char FIELD_VALUE_SEPARATOR = ':';

	public InspectParameters createParameters() {
		return new InspectParameters();
	}

	public void execute(PolopolyContext context, InspectParameters parameters) {
		ContentIdToContentIterator it =
			new ContentIdToContentIterator(context, parameters.getContentIds(), parameters.isStopOnException());

		while (it.hasNext()) {
			ContentRead content = it.next();

			try {
				ContentUtil contentUtil = util(content, context.getPolicyCMServer());

				printId(content);

				printMajor(content);

				printInputTemplate(contentUtil);

				printSecurityParent(context, content);

				printName(content);

				printLockInfo(content);

				printWorkflow(context, content);

				printLoginName(context, content);

				printComponents(parameters,  content);

				printContentReferences(context, content, contentUtil);

				printFiles(content);
			} catch (CMException e) {
				if (parameters.isStopOnException()) {
					throw new CMRuntimeException(e);
				} else {
					context.getLogger().error(content.getContentId().getContentIdString() + ": " + e);
				}
			} catch (IOException e) {
				if (parameters.isStopOnException()) {
					throw new CMRuntimeException(e);
				} else {
					context.getLogger().error(content.getContentId().getContentIdString() + ": " + e);
				}
			}
		}

		it.printInfo(System.err);
	}

	private void printName(ContentRead content) {
		try {
			String name = content.getName();
			if (name != null) {
				System.out.println(ContentFieldListParser.NAME + PREFIX_FIELD_SEPARATOR + name);
			}
		} catch (CMException e) {
			// Empty.
		}
	}

	private ExternalContentId printId(ContentRead content) throws CMException {
		ExternalContentId externalId = content.getExternalId();

		if (externalId != null) {
			System.out.println(ContentFieldListParser.ID + PREFIX_FIELD_SEPARATOR + externalId.getExternalId());
			System.out.println(ContentFieldListParser.NUMERICAL_ID + PREFIX_FIELD_SEPARATOR
								+ content.getContentId().getContentIdString());
		} else {
			System.out.println(ContentFieldListParser.ID + PREFIX_FIELD_SEPARATOR
								+ content.getContentId().getContentIdString());
		}
		return externalId;
	}

	private void printMajor(ContentRead content) throws CMException {

		int major = content.getContentId().getMajor();
		String majorString = MajorStrings.get(major);
		if (majorString != null) {
			System.out.println(ContentFieldListParser.MAJOR + PREFIX_FIELD_SEPARATOR + majorString);
		}

	}

	private void printInputTemplate(ContentUtil contentUtil) {
		System.out.println(ContentFieldListParser.INPUT_TEMPLATE + PREFIX_FIELD_SEPARATOR
							+ contentUtil.getInputTemplate().getContentIdString());
	}

	private void printSecurityParent(PolopolyContext context, ContentRead content) {
		ContentId securityParentId = content.getSecurityParentId();

		if (securityParentId != null) {
			System.out.println(ContentFieldListParser.SECURITY_PARENT + PREFIX_FIELD_SEPARATOR
								+ AbstractContentIdField.get(securityParentId, context));
		}
	}

	private void printLockInfo(ContentRead content) {
		LockInfo lockInfo = content.getLockInfo();

		if (lockInfo != null && lockInfo.getLocker() != null) {
			System.out.println(ContentFieldListParser.LOCKER + PREFIX_FIELD_SEPARATOR
								+ lockInfo.getLocker().getLoginName());
		}
	}

	private void printWorkflow(PolopolyContext context, ContentRead content) throws CMException {
		if (content instanceof WorkflowAware) {
			VersionedContentId workflowId = ((WorkflowAware) content).getWorkflowId();

			if (workflowId != null) {
				System.out.println(ContentFieldListParser.WORKFLOW + PREFIX_FIELD_SEPARATOR
									+ AbstractContentIdField.get(workflowId, context));
			}
		}
	}

	private void printLoginName(PolopolyContext context, ContentRead content) {
		if (content instanceof UserData) {
			try {
				UserData userData = (UserData) content;
				UserId userId = userData.getUserId();
				UserServer userServer = context.getUserServer();

				System.out.println("loginname" + PREFIX_FIELD_SEPARATOR + userData.getLoginName());

				for (GroupId groupId : userServer.getAllGroups()) {
					try {
						Group group = userServer.findGroup(groupId);

						if (group.isDirectMember(userId)) {
							System.out.println("group" + PREFIX_FIELD_SEPARATOR + group.getName() + " ("
												+ groupId.getGroupIdInt() + ")");
						}
					} catch (Exception e) {
						System.err.println("group " + groupId + ":" + e.toString());
					}
				}
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
	}

	private String[] printComponents(InspectParameters parameters, ContentRead content) throws CMException {
		String[] groups = content.getComponentGroupNames();

		Arrays.sort(groups);

		for (String group : groups) {
			String[] names = content.getComponentNames(group);

			Arrays.sort(names);

			for (String name : names) {
				String value = content.getComponent(group, name);
				
				if(parameters.isEscaped()) {
					 value = new ComponentValueParser().escape(value);
				}

				// Name already printed at top of content.
				if (group.equals("polopoly.Content") && name.equals("name")) {
					continue;
				}

				System.out.println(ContentFieldListParser.COMPONENT + PREFIX_FIELD_SEPARATOR + group + ':' + name
									+ FIELD_VALUE_SEPARATOR + value);
			}
		}
		return groups;
	}

	private void printFiles(ContentRead content) throws CMException, IOException {
		ContentFileInfo[] files = content.listFiles("/", true);

		for (ContentFileInfo file : files) {
			if (!file.isDirectory()) {
				System.out.println("file" + ':' + file.getPath() + " (" + file.getSize() + "b)");
			}
		}
	}

	private void printContentReferences(PolopolyContext context, ContentRead content, ContentUtil contentUtil)
		throws CMException {
		String[] groups = content.getContentReferenceGroupNames();

		Arrays.sort(groups);

		for (String group : groups) {
			String[] names = content.getContentReferenceNames(group);
			if (isContentList(names)) {
				ContentListUtil contentList = contentUtil.getContentList(group);

				printContentList(contentList, context);
			} else {
				Arrays.sort(names);

				for (String name : names) {
					// Inputtemplate already printed at top.
					if (group.equals("polopoly.Content") && name.equals("inputTemplateId")) {
						continue;
					}
					System.out.println(ContentFieldListParser.CONTENT_REF + PREFIX_FIELD_SEPARATOR + group + ':' + name
										+ FIELD_VALUE_SEPARATOR
										+ new ContentRefField(group, name).get(content, context));

				}
			}
		}
	}

	private void printContentList(ContentListUtil contentList, PolopolyContext context) {
		int size = contentList.size();

		for (int i = 0; i < size; i++) {
			ContentReference entry = contentList.getEntry(i);
			ContentId rmd = entry.getReferenceMetaDataId();
			ContentId referredId = entry.getReferredContentId();

			if (rmd != null) {
				System.out.println("list" + PREFIX_FIELD_SEPARATOR + contentList.getContentListStorageGroup()
									+ FIELD_VALUE_SEPARATOR + AbstractContentIdField.get(rmd, context)
									+ FIELD_VALUE_SEPARATOR + AbstractContentIdField.get(referredId, context));
			} else {
				System.out.println("list" + PREFIX_FIELD_SEPARATOR + contentList.getContentListStorageGroup()
									+ FIELD_VALUE_SEPARATOR + AbstractContentIdField.get(referredId, context));
			}
		}
	}

	private boolean isContentList(String[] names) {
		for (String name : names) {
			try {
				Integer.parseInt(name);
			} catch (NumberFormatException e) {
				return false;
			}
		}

		return true;
	}

	public String getHelp() {
		return "Prints the components, content references and meta data of the specified objects.";
	}
}
