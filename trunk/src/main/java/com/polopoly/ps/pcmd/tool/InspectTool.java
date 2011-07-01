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
import com.polopoly.ps.pcmd.argument.ContentIdListParameters;
import com.polopoly.ps.pcmd.field.content.AbstractContentIdField;
import com.polopoly.ps.pcmd.field.content.ContentRefField;
import com.polopoly.ps.pcmd.parser.ContentFieldListParser;
import com.polopoly.user.server.Group;
import com.polopoly.user.server.GroupId;
import com.polopoly.user.server.UserId;
import com.polopoly.user.server.UserServer;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.ContentIdToContentIterator;
import com.polopoly.util.content.ContentUtil;
import com.polopoly.util.contentlist.ContentListUtil;

public class InspectTool implements Tool<ContentIdListParameters> {
    private static final char FIELD_VALUE_SEPARATOR = ':';

    public ContentIdListParameters createParameters() {
        return new ContentIdListParameters();
    }

    public void execute(PolopolyContext context,
            ContentIdListParameters parameters) {
        ContentIdToContentIterator it = new ContentIdToContentIterator(context,
                parameters.getContentIds(), parameters.isStopOnException());

        while (it.hasNext()) {
            ContentRead content = it.next();

            try {
                ContentUtil contentUtil = util(content, context
                        .getPolicyCMServer());

                printId(content);

                printInputTemplate(contentUtil);

                printSecurityParent(context, content);

                printLockInfo(content);

                printWorkflow(context, content);

                printLoginName(context, content);

                printComponents(content);

                printContentReferences(context, content, contentUtil);

                printFiles(content);
            } catch (CMException e) {
                if (parameters.isStopOnException()) {
                    throw new CMRuntimeException(e);
                } else {
                    System.err.println(content.getContentId()
                            .getContentIdString()
                            + ": " + e);
                }
            } catch (IOException e) {
                if (parameters.isStopOnException()) {
                    throw new CMRuntimeException(e);
                } else {
                    System.err.println(content.getContentId()
                            .getContentIdString()
                            + ": " + e);
                }
            }
        }

        it.printInfo(System.err);
    }

    private ExternalContentId printId(ContentRead content) throws CMException {
        ExternalContentId externalId = content.getExternalId();

        if (externalId != null) {
            System.out.println(ContentFieldListParser.ID
                    + PREFIX_FIELD_SEPARATOR + externalId.getExternalId());
            System.out.println(ContentFieldListParser.NUMERICAL_ID
                    + PREFIX_FIELD_SEPARATOR
                    + content.getContentId().getContentIdString());
        } else {
            System.out.println(ContentFieldListParser.ID
                    + PREFIX_FIELD_SEPARATOR
                    + content.getContentId().getContentIdString());
        }
        return externalId;
    }

    private void printInputTemplate(ContentUtil contentUtil) {
        System.out.println(ContentFieldListParser.INPUT_TEMPLATE
                + PREFIX_FIELD_SEPARATOR
                + contentUtil.getInputTemplate().getExternalIdString());
    }

    private void printSecurityParent(PolopolyContext context,
            ContentRead content) {
        ContentId securityParentId = content.getSecurityParentId();

        if (securityParentId != null) {
            System.out.println(ContentFieldListParser.SECURITY_PARENT
                    + PREFIX_FIELD_SEPARATOR
                    + AbstractContentIdField.get(securityParentId, context));
        }
    }

    private void printLockInfo(ContentRead content) {
        LockInfo lockInfo = content.getLockInfo();

        if (lockInfo != null && lockInfo.getLocker() != null) {
            System.out.println(ContentFieldListParser.LOCKER
                    + PREFIX_FIELD_SEPARATOR
                    + lockInfo.getLocker().getLoginName());
        }
    }

    private void printWorkflow(PolopolyContext context, ContentRead content)
            throws CMException {
        if (content instanceof WorkflowAware) {
            VersionedContentId workflowId = ((WorkflowAware) content)
                    .getWorkflowId();

            if (workflowId != null) {
                System.out.println(ContentFieldListParser.WORKFLOW
                        + PREFIX_FIELD_SEPARATOR
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

                System.out.println("loginname" + PREFIX_FIELD_SEPARATOR
                        + userData.getLoginName());

                for (GroupId groupId : userServer.getAllGroups()) {
                    try {
                        Group group = userServer.findGroup(groupId);

                        if (group.isDirectMember(userId)) {
                            System.out.println("group" + PREFIX_FIELD_SEPARATOR
                                    + group.getName() + " ("
                                    + groupId.getGroupIdInt() + ")");
                        }
                    } catch (Exception e) {
                        System.err.println("group " + groupId + ":"
                                + e.toString());
                    }
                }
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
    }

    private String[] printComponents(ContentRead content) throws CMException {
        String[] groups = content.getComponentGroupNames();

        Arrays.sort(groups);

        for (String group : groups) {
            String[] names = content.getComponentNames(group);

            Arrays.sort(names);

            for (String name : names) {
                String value = content.getComponent(group, name);

                System.out.println(ContentFieldListParser.COMPONENT
                        + PREFIX_FIELD_SEPARATOR + group + ':' + name
                        + FIELD_VALUE_SEPARATOR + value);
            }
        }
        return groups;
    }

    private void printFiles(ContentRead content) throws CMException,
            IOException {
        ContentFileInfo[] files = content.listFiles("/", true);

        for (ContentFileInfo file : files) {
            if (!file.isDirectory()) {
                System.out.println("file" + ':' + file.getPath() + " ("
                        + file.getSize() + "b)");
            }
        }
    }

    private void printContentReferences(PolopolyContext context,
            ContentRead content, ContentUtil contentUtil) throws CMException {
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
                    System.out.println(ContentFieldListParser.CONTENT_REF
                            + PREFIX_FIELD_SEPARATOR
                            + group
                            + ':'
                            + name
                            + FIELD_VALUE_SEPARATOR
                            + new ContentRefField(group, name).get(content,
                                    context));
                }
            }
        }
    }

    private void printContentList(ContentListUtil contentList,
            PolopolyContext context) {
        int size = contentList.size();

        for (int i = 0; i < size; i++) {
            ContentReference entry = contentList.getEntry(i);
            ContentId rmd = entry.getReferenceMetaDataId();
            ContentId referredId = entry.getReferredContentId();

            if (rmd != null) {
                System.out.println("list" + PREFIX_FIELD_SEPARATOR
                        + contentList.getContentListStorageGroup()
                        + FIELD_VALUE_SEPARATOR
                        + AbstractContentIdField.get(rmd, context)
                        + FIELD_VALUE_SEPARATOR
                        + AbstractContentIdField.get(referredId, context));
            } else {
                System.out.println("list" + PREFIX_FIELD_SEPARATOR
                        + contentList.getContentListStorageGroup()
                        + FIELD_VALUE_SEPARATOR
                        + AbstractContentIdField.get(referredId, context));
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
