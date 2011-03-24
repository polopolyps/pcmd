package com.polopoly.pcmd.util.dot;

public class StyleBuffer {
    private StringBuffer buffer = new StringBuffer();
    private boolean first = true;
    
    public void addStyle(String name, Object value) {
        if (name != null && value != null) {
            if (!first) {
                buffer.append(", ");
            }
            
            buffer.append(name).append(" = \"").append(value.toString()).append("\"");
            
            first = false;
        }
    }
    
    public String render() {
        return "[" + buffer.toString() + "]";
    }
}
