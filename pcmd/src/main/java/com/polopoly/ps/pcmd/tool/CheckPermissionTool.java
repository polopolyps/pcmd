package com.polopoly.ps.pcmd.tool;

import com.polopoly.cm.client.Content;
import com.polopoly.pcmd.tool.Tool;
import com.polopoly.ps.pcmd.field.content.AbstractContentIdField;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.ContentIdToContentIterator;

public class CheckPermissionTool implements Tool<CheckPermissionParameters> {

    public CheckPermissionParameters createParameters() {
        return new CheckPermissionParameters();
    }

    public void execute(PolopolyContext context,
            CheckPermissionParameters parameters) {
        ContentIdToContentIterator it =
            new ContentIdToContentIterator(context, parameters.getContentIds(), parameters.isStopOnException());

        String loginName = context.getPolicyCMServer().getCurrentCaller().getLoginName();
        String permission = parameters.getPermission();

        while (it.hasNext()) {
            Content content = (Content) it.next();

            System.out.println(AbstractContentIdField.get(content.getContentId(), context));

            boolean checkResult = content.checkPermission(permission, false);

            if (checkResult) {
                System.out.println(loginName + " has permission " + permission + ".");
            }
            else {
                System.out.println(loginName + " does not have permission " + permission + ".");
            }
        }
    }

    public String getHelp() {
        return "Checks whether the logged in user has a permission on a certain object.";
    }
}
