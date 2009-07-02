package com.polopoly.pcmd.field.policy;

import com.polopoly.cm.policy.Policy;
import com.polopoly.util.client.PolopolyContext;

public class ClassField implements PolicyField {

    public String get(Policy policy, PolopolyContext context) {
        return policy.getClass().getName();
    }

}
