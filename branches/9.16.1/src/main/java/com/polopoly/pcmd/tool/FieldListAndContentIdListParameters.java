package com.polopoly.pcmd.tool;

import java.util.List;

import com.polopoly.pcmd.argument.ArgumentException;
import com.polopoly.pcmd.argument.Arguments;
import com.polopoly.pcmd.argument.ContentIdListParameters;
import com.polopoly.pcmd.argument.ParameterHelp;
import com.polopoly.pcmd.field.content.Field;
import com.polopoly.pcmd.parser.ContentFieldListParser;
import com.polopoly.util.client.PolopolyContext;

public class FieldListAndContentIdListParameters extends ContentIdListParameters implements FieldListParameters {
    private static final String DEFAULT_FIELDS = "id,name";
    private static final String FIELDS = "fields";
    private static final String DELIMITER = "delimiter";
    private List<Field> fieldList;
    private String delimiter = " ";

    @Override
    public void parseParameters(Arguments args, PolopolyContext context)
            throws ArgumentException {
        super.parseParameters(args, context);

        fieldList = new ContentFieldListParser().parse(args.getOptionString(FIELDS, getDefaultFields()));
        delimiter = args.getOptionString(DELIMITER, " ");
    }

    protected String getDefaultFields() {
        return DEFAULT_FIELDS;
    }

    @Override
    public void getHelp(ParameterHelp help) {
        super.getHelp(help);

        help.addOption(FIELDS, new ContentFieldListParser(), "The fields to print for each object.");
        help.addOption(DELIMITER, null, "The delimiter to print between fields.");
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
