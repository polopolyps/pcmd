package com.polopoly.pcmd.tool;

import com.polopoly.cm.client.Content;
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
            System.err.println("You must log a user to be able to set an ACL. Specify the " +
                    ClientFromArgumentsConfigurator.PASSWORD + " parameter to log in.");
            System.exit(1);
        }

        ContentIdToContentIterator it =
            new ContentIdToContentIterator(context, parameters.getContentIds(), parameters.isStopOnException());

        while (it.hasNext()) {
            try {
                Content content = (Content) it.next();

                System.out.println(AbstractContentIdField.get(content.getContentId(), context));

                AclId aclId = content.getAclId();

                if (aclId == null) {
                    aclId = content.createAcl();

                    System.out.println("No ACL existed. Created one.");
                }

                Acl acl = context.getUserServer().findAcl(aclId);

                System.out.print("aclId:" + aclId.getAclIdInt() + " ");

                PrincipalId principalId = parameters.getPrincipalId(context);

                AclEntry entry = acl.getEntry(principalId);

                if (entry == null) {
                    entry = new AclEntry(principalId);
                    System.out.println("No ACL entry for the user " + principalId.getPrincipalIdString() + " existed. Created one.");
                }

                entry.addPermission(parameters.getPermission());
                acl.addEntry(entry, currentCaller);
            } catch (Exception e) {
                System.err.println(e.toString());
                e.printStackTrace();
            }
        }
    }

    public String getHelp() {
        return "Adds a permission to an ACL of an object for a certain principal.";
    }

}
