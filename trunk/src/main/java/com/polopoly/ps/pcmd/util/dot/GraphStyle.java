package com.polopoly.ps.pcmd.util.dot;

public class GraphStyle extends StyleBase {
    public enum RankDirection {
        LEFT_TO_RIGHT("LR");
        
        private final String code;
        
        RankDirection(String code) {
            this.code = code;
        }
        
        @Override
        public String toString() {
            return code;
        }
    }
    
    private RankDirection rankDirection = null;
    
    public GraphStyle rankDirection(RankDirection direction) {
        this.rankDirection = direction;
        return this;
    }
    
    public String render() {
        StyleBuffer buffer = new StyleBuffer();
        buffer.addStyle("rankdir", rankDirection);
        renderCustomInBuffer(buffer);
        return buffer.render();
    }
}
