package com.polopoly.pcmd.tool;

import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.Content;
import com.polopoly.pcmd.FatalToolException;
import com.polopoly.pcmd.field.content.AbstractContentIdField;
import com.polopoly.user.server.Acl;
import com.polopoly.user.server.AclEntry;
import com.polopoly.user.server.AclId;
import com.polopoly.user.server.Caller;
import com.polopoly.user.server.PrincipalId;
import com.polopoly.util.client.ClientFromArgumentsConfigurator;
import com.polopoly.util.client.NonLoggedInCaller;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.ContentIdToContentIterator;

public class SetPermissionTool implements Tool<SetPermissionParameters> {

    public SetPermissionParameters createParameters() {
        return new SetPermissionParameters();
    }

    public void execute(PolopolyContext context,
            SetPermissionParameters parameters) {
        Caller currentCaller = context.getPolicyCMServer().getCurrentCaller();

        if (currentCaller instanceof NonLoggedInCaller) {
            throw new FatalToolException(
                "You must log a user to be able to set an ACL. Specify the " +
                ClientFromArgumentsConfigurator.PASSWORD + " parameter to log in.");
        }

        ContentIdToContentIterator it =
            new ContentIdToContentIterator(context, parameters.getContentIds(), parameters.isStopOnException());

        while (it.hasNext()) {
            Content content = (Content) it.next();

            String contentIdString = AbstractContentIdField.get(content.getContentId(), context);

            System.out.println(contentIdString);

            try {

                AclId aclId = content.getAclId();

                if (aclId == null) {
                    content.lock();
                    aclId = content.createAcl();

                    System.out.println(contentIdString + ": no ACL existed. Created one.");
                }

                Acl acl = context.getUserServer().findAcl(aclId);

                System.out.print("aclId:" + aclId.getAclIdInt() + " ");

                PrincipalId principalId = parameters.getPrincipalId(context);

                AclEntry entry = acl.getEntry(principalId);

                if (entry == null) {
                    entry = new AclEntry(principalId);
                    System.out.println("principal " + principalId.getPrincipalIdString() + " in " +
                        contentIdString + ": no ACL entry existed. Created one.");
                }

                for (String permission : parameters.getPermissions()) {
                    entry.addPermission(permission);
                }

                acl.addEntry(entry, currentCaller);
            } catch (Exception e) {
                System.err.println(e.toString());
                e.printStackTrace();
            } finally {
                try {
                    content.unlock();
                } catch (CMException e) {
                    System.err.println("While unlocking " + contentIdString + ": " + e);
                }
            }
        }
    }

    public String getHelp() {
        return "Adds a permission to an ACL of an object for a certain principal.";
    }
}

