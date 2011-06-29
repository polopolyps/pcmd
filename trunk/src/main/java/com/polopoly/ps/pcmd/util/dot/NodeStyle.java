package com.polopoly.ps.pcmd.util.dot;

public class NodeStyle extends StyleBase {
    public enum Style {
        FILLED("filled");
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
    private Double penWidth = null;
    
    public NodeStyle style(Style style) {
        this.style = style;
        return this;
    }
    
    public NodeStyle fillColor(String color) {
        this.color = color;
        return this;
    }
    
    public NodeStyle label(String label) {
        this.label = label;
        return this;
    }

    public NodeStyle penWidth(double width) {
        this.penWidth = width;
        return this;
    }

    
    public String render() {
        StyleBuffer buffer = new StyleBuffer();
        buffer.addStyle("style", style);
        buffer.addStyle("fillcolor", color);
        buffer.addStyle("penwidth", penWidth);
        if (label != null) {
            buffer.addStyle("label", label.replaceAll("\\\\", "\\\\\\\\").replaceAll("\n", "\\\\n").replaceAll("\"", "\\\\\""));
        }
        renderCustomInBuffer(buffer);
        return buffer.render();
    }

}
