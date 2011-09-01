package com.polopoly.ps.pcmd.parser;

import java.util.ArrayList;
import java.util.List;

import com.polopoly.ps.pcmd.field.content.CommittedField;
import com.polopoly.ps.pcmd.field.content.ComponentField;
import com.polopoly.ps.pcmd.field.content.ContentIdField;
import com.polopoly.ps.pcmd.field.content.ContentListField;
import com.polopoly.ps.pcmd.field.content.ContentListSizeField;
import com.polopoly.ps.pcmd.field.content.ContentRefField;
import com.polopoly.ps.pcmd.field.content.Field;
import com.polopoly.ps.pcmd.field.content.InputTemplateField;
import com.polopoly.ps.pcmd.field.content.LockerField;
import com.polopoly.ps.pcmd.field.content.NameField;
import com.polopoly.ps.pcmd.field.content.NumericalContentIdField;
import com.polopoly.ps.pcmd.field.content.PaddingField;
import com.polopoly.ps.pcmd.field.content.SecurityParentField;
import com.polopoly.ps.pcmd.field.content.VersionCountField;
import com.polopoly.ps.pcmd.field.content.VersionField;
import com.polopoly.ps.pcmd.field.content.WorkflowField;

public class ContentFieldListParser implements Parser<List<Field>> {

    public static final char PREFIX_FIELD_SEPARATOR = ':';

    private static final String VERSION = "version";

    private static final String VERSION_COUNT = "versioncount";

    private static final String COMMITTED = "committed";

    private static final String NAME = "name";

    public static final String COMPONENT = "component";

    public static final String CONTENT_REF = "ref";

    public static final String CONTENT_LIST = "contentlist";

    private static final String CONTENT_LIST_SIZE = "contentlistsize";

    public static final String ID = "id";

    public static final String WORKFLOW = "workflow";

    public static final String INPUT_TEMPLATE = "inputtemplate";

    public static final String NUMERICAL_ID = "numericalid";

    public static final String LOCKER = "locker";

    public static final String SECURITY_PARENT = "securityparent";

    public String getHelp() {
        return ID + " / " + NUMERICAL_ID + " / " + NAME + " / " + COMPONENT
                + ":" + new ComponentParser().getHelp() + " / " + CONTENT_REF
                + ":" + new ContentRefParser().getHelp() + " / " + CONTENT_LIST
                + "[:<content list>] / " + CONTENT_LIST_SIZE
                + "[:<content list>] / " + LOCKER + " / " + INPUT_TEMPLATE
                + " / " + COMMITTED + " / " + VERSION + " / " + VERSION_COUNT
                + " / " + WORKFLOW + " / " + SECURITY_PARENT
                + " (append :<width> to any field to pad it)";
    }

    public List<Field> parse(String value) throws ParseException {
        // this isn't ideal as no field names can contain commas. we should
        // allow quoting or something.
        String[] fields = value.split(",");

        List<Field> result = new ArrayList<Field>();

        for (String field : fields) {
            result.add(parseField(field));
        }

        return result;
    }

    private Field parseField(String field) throws ParseException {
        int i = field.lastIndexOf(':');

        // check if field ends in padding specifier
        if (i != -1) {
            try {
                int width = Integer.parseInt(field.substring(i + 1));

                return new PaddingField(parseField(field.substring(0, i)),
                        width);
            } catch (NumberFormatException nfe) {
                // it didn't.
            }
        }

        if (field.equals(NAME)) {
            return new NameField();
        } else if (field.equals(ID)) {
            return new ContentIdField();
        } else if (field.equals(NUMERICAL_ID)) {
            return new NumericalContentIdField();
        } else if (field.equals(LOCKER)) {
            return new LockerField();
        } else if (field.equals(COMMITTED)) {
            return new CommittedField();
        } else if (field.equals(VERSION)) {
            return new VersionField();
        } else if (field.equals(VERSION_COUNT)) {
            return new VersionCountField();
        } else if (field.equals(INPUT_TEMPLATE)) {
            return new InputTemplateField();
        } else if (field.startsWith(COMPONENT + ':')) {
            return new ComponentField(new ComponentParser().parse(field
                    .substring(COMPONENT.length() + 1)));
        } else if (field.startsWith(CONTENT_REF + PREFIX_FIELD_SEPARATOR)) {
            return new ContentRefField(new ContentRefParser().parse(field
                    .substring(CONTENT_REF.length() + 1)));
        } else if (field.equals(WORKFLOW)) {
            return new WorkflowField();
        } else if (field.startsWith(CONTENT_LIST_SIZE)) {
            String contentListName = null;

            if (field.startsWith(CONTENT_LIST_SIZE + ":")) {
                contentListName = field
                        .substring(CONTENT_LIST_SIZE.length() + 1);
            }

            return new ContentListSizeField(contentListName);
        } else if (field.startsWith(CONTENT_LIST)) {
            String contentListName = null;

            if (field.startsWith(CONTENT_LIST + ":")) {
                contentListName = field.substring(CONTENT_LIST.length() + 1);
            }

            return new ContentListField(contentListName);
        } else if (field.startsWith(SECURITY_PARENT)) {
            return new SecurityParentField();
        } else {
            throw new ParseException(this, field, "Unknown field");
        }
    }
}
