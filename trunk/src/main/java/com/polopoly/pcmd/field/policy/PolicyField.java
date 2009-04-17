package com.polopoly.pcmd.field.policy;

import com.polopoly.cm.policy.Policy;
import com.polopoly.pcmd.tool.PolopolyContext;

public interface PolicyField {
    String get(Policy policy, PolopolyContext context);
}
