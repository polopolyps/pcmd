package com.polopoly.ps.pcmd.tool;

import com.polopoly.pcmd.tool.Tool;
import com.polopoly.ps.pcmd.field.content.AbstractPrincipalIdField;
import com.polopoly.user.server.Caller;
import com.polopoly.user.server.PrincipalId;
import com.polopoly.util.client.PolopolyContext;

public class AddGroupMemberTool implements Tool<AddGroupMemberParameters> {

    public AddGroupMemberParameters createParameters() {
        return new AddGroupMemberParameters();
    }

    public void execute(PolopolyContext context,
            AddGroupMemberParameters parameters) {
        Caller currentCaller = context.getPolicyCMServer().getCurrentCaller();

        for (PrincipalId member : parameters.getMembers()) {
            try {
                parameters.getGroup().addMember(member, currentCaller);

                System.out.println(AbstractPrincipalIdField.get(member, context));
            } catch (Exception e) {
                System.err.println("While adding "+ AbstractPrincipalIdField.get(member, context) + ": " + e);
            }
        }
    }

    public String getHelp() {
        return "Adds the specified members (users or groups) to a group";
    }

}
