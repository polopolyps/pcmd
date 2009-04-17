package com.polopoly.pcmd.field.content;

import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.pcmd.tool.PolopolyContext;

public class NameField implements Field {

    public String get(ContentRead content, PolopolyContext context) {
        try {
            return content.getName();
        } catch (CMException e) {
            System.err.println(e.toString());

            return "";
        }
    }

}
