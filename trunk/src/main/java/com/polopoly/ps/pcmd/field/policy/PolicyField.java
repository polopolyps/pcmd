package com.polopoly.ps.pcmd.field.policy;

import com.polopoly.cm.policy.Policy;
import com.polopoly.util.client.PolopolyContext;

public interface PolicyField {
    String get(Policy policy, PolopolyContext context);
}
