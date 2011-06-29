package com.polopoly.ps.pcmd.field.policy;

import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.Policy;
import com.polopoly.util.client.PolopolyContext;

public class NameField implements PolicyField {

    public String get(Policy policy, PolopolyContext context) {
        try {
            return policy.getPolicyName();
        } catch (CMException e) {
            System.err.println(e.toString());

            return "";
        }
    }

}
