package com.polopoly.pcmd.tool.graphcontent;

import java.util.List;

import com.polopoly.pcmd.argument.ArgumentException;
import com.polopoly.pcmd.argument.Arguments;
import com.polopoly.pcmd.argument.ContentIdListParameters;
import com.polopoly.pcmd.argument.NotProvidedException;
import com.polopoly.pcmd.argument.ParameterHelp;
import com.polopoly.pcmd.parser.BooleanParser;
import com.polopoly.pcmd.parser.IntegerParser;
import com.polopoly.pcmd.parser.ListParser;
import com.polopoly.util.client.PolopolyContext;

public class GraphContentParameters extends ContentIdListParameters {
    private boolean excludePolopoly;
    private boolean excludeGreenFieldTimes;
    private int depth;
    private boolean force;
    private List<Integer> filterMajors;
    private boolean renderRMD;
    private FORMAT format = FORMAT.Text; 
    
    public enum FORMAT {
        Text, Dot
    };
    
    public boolean isExcludeGreenFieldTimes() {
        return excludeGreenFieldTimes;
    }
    
    public boolean isExcludePolopoly() {
        return excludePolopoly;
    }
    
    public boolean isRenderRMD() {
        return renderRMD;
    }
    
    public void setExcludeGreenFieldTimes(boolean excludeGreenFieldTimes) {
        this.excludeGreenFieldTimes = excludeGreenFieldTimes;
    }
    
    public void setExcludePolopoly(boolean excludePolopoly) {
        this.excludePolopoly = excludePolopoly;
    }
    
    public void setRenderRMD(boolean renderRMD) {
        this.renderRMD = renderRMD;
    }
    
    public List<Integer> getFilterMajors() {
        return filterMajors;
    }
    
    public void setFilterMajors(List<Integer> majors) {
        this.filterMajors = majors;
    }    

    public int getDepth() {
        return depth;
    }
    
    public void setDepth(int depth) {
        if (depth < 1) {
            depth = 1;
        } else {
            this.depth = depth;    
        }
    }

    public boolean isForce() {
        return force;
    }
    
    public void setForce(boolean force) {
        this.force = force;
    }
    
    public FORMAT getFormat() {
        return format;
    }
    
    public void setFormat(FORMAT format) {
        this.format = format;
    }
    
    public void getHelp(ParameterHelp help) {
        super.getHelp(help);
        
        help.addOption("majors", ListParser.wrap(new IntegerParser()), "Filter graph to only show content with these majors.");
        help.addOption("no-polopoly", new BooleanParser(), "Filter content with external ids starting with 'p.'");
        help.addOption("no-gt", new BooleanParser(), "Filter content with external ids starting with 'example.'");
        help.addOption("depth", new IntegerParser(), "Determines how deep to traverse the graph from the seeds. Defaults to 5.");
        help.addOption("rmd", new BooleanParser(), "Graph reference metadata as individual nodes.");
        help.addOption("force", new BooleanParser(), "Forces the tool to accept parameters it considers unwise.");
        help.addOption("dot", new BooleanParser(), "Produce output in Graphviz \"dot\" format.");
    }

    public void parseParameters(Arguments args, PolopolyContext context) throws ArgumentException {
        super.parseParameters(args, context);
        
        setExcludePolopoly(args.getFlag("no-polopoly", false));
        setExcludeGreenFieldTimes(args.getFlag("no-gt", false));
        setForce(args.getFlag("force", false));
        setRenderRMD(args.getFlag("rmd", false));
        if (args.getFlag("dot", false)) {
            setFormat(FORMAT.Dot);
        } else {
            setFormat(FORMAT.Text);
        }
        
        try {
            setFilterMajors(args.getOption("majors", ListParser.wrap(new IntegerParser())));
        } catch (NotProvidedException e) {
        }
        
        try {
            setDepth(args.getOption("depth", new IntegerParser()));
        } catch (NotProvidedException e) {
            setDepth(3);
        }
    }
}
