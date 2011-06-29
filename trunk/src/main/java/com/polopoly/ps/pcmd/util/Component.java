package com.polopoly.ps.pcmd.util;

public class Component {
    private String group;
    private String component;

    public Component(String group, String component) {
        this.group = group;
        this.component = component;
    }

    public String getGroup() {
        return group;
    }

    public String getComponent() {
        return component;
    }
}
