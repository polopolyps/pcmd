package com.polopoly.ps.pcmd.tool;

import java.util.List;

import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.Arguments;
import com.polopoly.ps.pcmd.argument.ContentIdListParameters;
import com.polopoly.ps.pcmd.argument.NotProvidedException;
import com.polopoly.ps.pcmd.argument.ParameterHelp;
import com.polopoly.ps.pcmd.field.policy.PolicyField;
import com.polopoly.ps.pcmd.parser.IntegerParser;
import com.polopoly.ps.pcmd.parser.PolicyFieldListParser;
import com.polopoly.util.client.PolopolyContext;

public class PolicyTreeParameters extends ContentIdListParameters {
    private static final String FIELDS = "fields";
    private static final String DEPTH = "depth";
    private static final String DELIMITER = "delimiter";
    private static final int DEFAULT_DEPTH = 5;
    private static final String DEFAULT_DELIMITER = " ";

    private List<PolicyField> fieldList;
    private String delimiter = DEFAULT_DELIMITER;
    private int depth = DEFAULT_DEPTH;

    @Override
    public void getHelp(ParameterHelp help) {
        super.getHelp(help);

        help.addOption(DEPTH, new IntegerParser(), "The depth to which the tree should be printed (defaults " + DEFAULT_DEPTH + ")");
        help.addOption(DELIMITER, null, "The delimiter between field values.");
        help.addOption(FIELDS, new PolicyFieldListParser(), "The fields to print for each child policy.");
    }

    @Override
    public void parseParameters(Arguments args, PolopolyContext context) throws ArgumentException {
        super.parseParameters(args, context);

        try {
            setDepth(args.getOption(DEPTH, new IntegerParser()));
        } catch (NotProvidedException e) {
        }

        fieldList = new PolicyFieldListParser().parse(args.getOptionString(FIELDS, "name,inputtemplate,value"));
        delimiter = args.getOptionString(DELIMITER, " ");
    }

    public void setFieldList(List<PolicyField> fieldList) {
        this.fieldList = fieldList;
    }

    public List<PolicyField> getFieldList() {
        return fieldList;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDepth(int depth) {
        this.depth  = depth;
    }

    public int getDepth() {
        return depth;
    }
}
