package com.polopoly.pcmd.parser;

import com.polopoly.pcmd.util.Component;

public class ComponentParser implements Parser<Component> {

    public String getHelp() {
        return "<componentgroup>:<component>";
    }

    public Component parse(String string) throws ParseException {
        int i = string.indexOf(':');

        if (i == -1) {
            throw new ParseException(this, string, "Expected a colon between component group and component name");
        }

        return new Component(string.substring(0, i), string.substring(i+1));
    }

}
