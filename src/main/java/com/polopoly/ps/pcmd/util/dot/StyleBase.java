package com.polopoly.ps.pcmd.util.dot;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class StyleBase {
    private Map<String, String> custom = new HashMap<String, String>();
    
    protected void internalCustom(String key, String value) {
        custom.put(key, value);
    }
    
    protected void renderCustomInBuffer(StyleBuffer buffer) {
        for(Entry<String, String> entry : custom.entrySet()) {
            buffer.addStyle(entry.getKey(), entry.getValue());
        }
    }
    
    public abstract String render();
}
