package com.polopoly.pcmd.tool;

import java.util.List;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.pcmd.argument.ArgumentException;
import com.polopoly.pcmd.argument.Arguments;
import com.polopoly.pcmd.argument.NotProvidedException;
import com.polopoly.pcmd.argument.ParameterHelp;
import com.polopoly.pcmd.field.content.Field;
import com.polopoly.pcmd.parser.ContentFieldListParser;
import com.polopoly.pcmd.parser.ContentIdParser;
import com.polopoly.pcmd.parser.IntegerParser;
import com.polopoly.util.client.PolopolyContext;

public class TreeParameters implements FieldListParameters {
    private static final int DEFAULT_DEPTH = 4;
    private static final String DEFAULT_ROOT = "p.RootDepartment";

    private static final String DEPTH = "depth";
    private static final String FIELDS = "fields";
    private static final String DELIMITER = "delimiter";

    private List<Field> fieldList;
    private String delimiter = " ";
    private ContentId root = new ExternalContentId(DEFAULT_ROOT);
    private int depth = DEFAULT_DEPTH;

    public void getHelp(ParameterHelp help) {
        help.setArguments(new ContentIdParser(), "(optional) The root of the hierarchy to print.");
        help.addOption(DEPTH, new IntegerParser(),
            "The depth to print to. Defaults to " + DEFAULT_DEPTH + ".");
        help.addOption(FIELDS, new ContentFieldListParser(), "The fields to print for each object.");
        help.addOption(DELIMITER, null, "The delimiter to print between fields.");
    }

    public void parseParameters(Arguments args, PolopolyContext context)
            throws ArgumentException {
        try {
            setRoot(args.getArgument(0, new ContentIdParser(context)));
        } catch (NotProvidedException e) {
        }

        try {
            setDepth(args.getOption(DEPTH, new IntegerParser()));
        } catch (NotProvidedException e) {
        }

        fieldList = new ContentFieldListParser().parse(args.getOptionString(FIELDS, "id,name"));
        delimiter = args.getOptionString(DELIMITER, " ");
    }

    public void setRoot(ContentId root) {
        this.root = root;
    }

    public ContentId getRoot() {
        return root;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
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
