package com.polopoly.ps.pcmd.util.dot;

public class EdgeStyle extends StyleBase {
    public enum Style {
        DASHED("dashed"),
        DOTTED("dotted"),
        SOLID("solid");
        private final String style;
        Style(String style) {
            this.style = style;
        }
        @Override
        public String toString() {
            return style;
        }
    }    
    
    private Style style = null;
    private String color = null;
    private String label = null;
    
    public EdgeStyle style(Style style) {
        this.style = style;
        return this;
    }
    
    public EdgeStyle fillColor(String color) {
        this.color = color;
        return this;
    }
    
    public EdgeStyle label(String label) {
        this.label = label;
        return this;
    }
    
    public String render() {
        StyleBuffer buffer = new StyleBuffer();
        buffer.addStyle("style", style);
        buffer.addStyle("fillColor", color);
        if (label != null) {
            buffer.addStyle("label", label.replaceAll("\\\\", "\\\\\\\\").replaceAll("\n", "\\\\n").replaceAll("\"", "\\\\\""));
        }
        renderCustomInBuffer(buffer);
        return buffer.render();
    }
}
