package com.polopoly.ps.pcmd.tool;

import java.util.Iterator;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.pcmd.tool.Tool;
import com.polopoly.ps.pcmd.argument.ContentIdListParameters;
import com.polopoly.ps.pcmd.field.content.AbstractContentIdField;
import com.polopoly.ps.pcmd.field.content.AbstractPrincipalIdField;
import com.polopoly.user.server.Acl;
import com.polopoly.user.server.AclEntry;
import com.polopoly.user.server.AclId;
import com.polopoly.user.server.PrincipalId;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.ContentIdToContentIterator;

public class AclTool implements Tool<ContentIdListParameters> {
    public ContentIdListParameters createParameters() {
        return new ContentIdListParameters();
    }

    @SuppressWarnings("unchecked")
    public void execute(PolopolyContext context,
            ContentIdListParameters parameters) {
        ContentIdToContentIterator it =
            new ContentIdToContentIterator(context, parameters.getContentIds(), parameters.isStopOnException());

        while (it.hasNext()) {
            ContentRead content = it.next();

            System.out.println(AbstractContentIdField.get(content.getContentId(), context));

            try {
                ContentId parent = content.getSecurityParentId();
                int generation = 1;

                while (parent != null) {
                    System.out.println("parent(" + generation++ + "):" + AbstractContentIdField.get(parent.getContentId(), context));

                    parent = context.getPolicyCMServer().getContent(parent).getSecurityParentId();
                }

                AclId aclId = content.getAclId();

                if (aclId != null) {
                    Acl acl = context.getUserServer().findAcl(aclId);

                    System.out.print("aclId:" + aclId.getAclIdInt() + " ");

                    Iterator<PrincipalId> owners = acl.owners();

                    while (owners.hasNext()) {
                        System.out.print(owners.next().getPrincipalIdString());
                    }

                    System.out.println();

                    Iterator<AclEntry> entries = acl.entries();

                    while (entries.hasNext()) {
                        AclEntry entry = entries.next();

                        System.out.print(AbstractPrincipalIdField.get(entry.getPrincipalId(), context) + ": ");

                        Iterator<String> permissions = entry.permissions();

                        boolean first = true;

                        while (permissions.hasNext()) {
                            if (first) {
                                first = false;
                            }
                            else {
                                System.out.print(",");
                            }

                            System.out.print(permissions.next());
                        }

                        System.out.println();
                    }
                }
                else {
                    System.out.println("No ACL ID.");
                }
            } catch (Exception e) {
                if (parameters.isStopOnException()) {
                    throw new CMRuntimeException(e);
                }
                else {
                    System.err.println(content.getContentId().getContentIdString() + ": " + e);
                }
            }
        }
    }

    public String getHelp() {
        return "Prints information on the ACL defined on the specified content objects.";
    }
}
