package com.polopoly.util.policy;

import java.util.List;

import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.client.InputTemplate;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;

public class RuntimeExceptionPolicyWrapper extends DelegatingPolicy implements RuntimeExceptionPolicy {

    public RuntimeExceptionPolicyWrapper(Policy delegate) {
        super(delegate);
    }

    @Override
    public String getPolicyName() {
        try {
            return super.getPolicyName();
        } catch (CMException e) {
            throw toRuntimeException(e, "getPolicyName");
        }
    }

    @Override
    public Policy getParentPolicy() {
        try {
            return super.getParentPolicy();
        } catch (CMException e) {
            throw toRuntimeException(e, "getParentPolicy");
        }
    }

    @Override
    public List<String> getChildPolicyNames() {
        try {
            return super.getChildPolicyNames();
        } catch (CMException e) {
            throw toRuntimeException(e, "getChildPolicyNames");
        }
    }

    @Override
    public Policy getChildPolicy(String name) {
        try {
            return super.getChildPolicy(name);
        } catch (CMException e) {
            throw toRuntimeException(e, "getChildPolicy");
        }
    }

    @Override
    public PolicyCMServer getCMServer() {
        try {
            return super.getCMServer();
        } catch (CMException e) {
            throw toRuntimeException(e, "getCMServer");
        }
    }

    @Override
    public InputTemplate getInputTemplate() {
        try {
            return super.getInputTemplate();
        } catch (CMException e) {
            throw toRuntimeException(e, "getInputTemlate");
        }
    }

    @Override
    public String getComponent(String name)  {
        try {
            return super.getComponent(name);
        } catch (CMException e) {
            throw toRuntimeException(e, "getComponent");
        }
    }

    @Override
    public void setComponent(String name, String value) {
        try {
            super.setComponent(name, value);
        } catch (CMException e) {
            throw toRuntimeException(e, "setComponent");
        }
    }

    @Override
    public String[] getComponentNames() {
        try {
            return super.getComponentNames();
        } catch (CMException e) {
            throw toRuntimeException(e, "getComponentNames");
        }
    }

    private RuntimeException toRuntimeException(Exception e, String operation) {
        return new CMRuntimeException("While performing operation " + operation + " on " +
                this + ": " + e.getMessage(), e);
    }
}
