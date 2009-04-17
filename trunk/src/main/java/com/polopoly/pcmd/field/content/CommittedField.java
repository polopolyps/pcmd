package com.polopoly.pcmd.field.content;

import java.util.Date;

import com.polopoly.cm.client.ContentRead;
import com.polopoly.pcmd.tool.PolopolyContext;

public class CommittedField implements Field {

    public String get(ContentRead content, PolopolyContext context) {
        long committed = content.getVersionInfo().getCommitted();

        if (committed > 0) {
            return new Date(committed).toString();
        }
        else {
            return "";
        }
    }

}
