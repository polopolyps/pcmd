package com.polopoly.pcmd.tool;

import java.util.List;

import com.polopoly.pcmd.argument.ArgumentException;
import com.polopoly.pcmd.argument.Arguments;
import com.polopoly.pcmd.argument.ContentIdListParameters;
import com.polopoly.pcmd.argument.ParameterHelp;
import com.polopoly.pcmd.field.Field;
import com.polopoly.pcmd.parser.FieldListParser;

public class ListParameters extends ContentIdListParameters {
    private static final String FIELDS = "fields";
    private static final String DELIMITER = "delimiter";
    private List<Field> fieldList;
    private String delimiter = " ";

    @Override
    public void parseParameters(Arguments args, PolopolyContext context)
            throws ArgumentException {
        super.parseParameters(args, context);

        fieldList = new FieldListParser().parse(args.getOptionString(FIELDS, "numericalid,name"));
        delimiter = args.getOptionString(DELIMITER, " ");
    }

    @Override
    public void getHelp(ParameterHelp help) {
        super.getHelp(help);

        help.addOption(FIELDS, new FieldListParser(), "The fields to print for each object.");
    }

    public void setFieldList(List<Field> fieldList) {
        this.fieldList = fieldList;
    }

    public List<Field> getFieldList() {
        return fieldList;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getDelimiter() {
        return delimiter;
    }
}
