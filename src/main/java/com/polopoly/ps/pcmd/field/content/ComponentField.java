package com.polopoly.ps.pcmd.field.content;

import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.ps.pcmd.util.Component;
import com.polopoly.util.client.PolopolyContext;

public class ComponentField implements Field {
    private String group;
    private String component;

    public ComponentField(String group, String component) {
        this.group = group;
        this.component = component;
    }

    public ComponentField(Component componentObject) {
        this.group = componentObject.getGroup();
        this.component = componentObject.getComponent();
    }

    public String get(ContentRead content, PolopolyContext context) {
        try {
            String componentValue = content.getComponent(group, component);

            if (componentValue == null) {
                return "<null>";
            }
            else {
                return componentValue;
            }
        } catch (CMException e) {
            System.err.println(e.toString());

            return "";
        }
    }

}
