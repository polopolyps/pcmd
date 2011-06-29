package com.polopoly.ps.pcmd.field.policy;

import com.polopoly.cm.policy.Policy;
import com.polopoly.util.client.PolopolyContext;

public class PaddingField implements PolicyField {
    private PolicyField field;
    private int size;

    public PaddingField(PolicyField field, int size) {
        this.field = field;
        this.size = size;
    }

    public String get(Policy policy, PolopolyContext context) {
        StringBuffer result =
            new StringBuffer(field.get(policy, context));

        if (result.length() > size) {
            result.setLength(size - 2);
            result.append("..");
        }

        while (result.length() < size) {
            result.append(' ');
        }

        return result.toString();
    }

}
