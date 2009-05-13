package com.polopoly.util.policy;

import java.util.List;

import com.polopoly.cm.client.InputTemplate;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;

public interface RuntimeExceptionPolicy extends Policy {
    String getPolicyName();
    Policy getParentPolicy();
    List<String> getChildPolicyNames();
    Policy getChildPolicy(String name);
    PolicyCMServer getCMServer();
    InputTemplate getInputTemplate();
}
