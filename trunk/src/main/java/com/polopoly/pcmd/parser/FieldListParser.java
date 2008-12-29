package com.polopoly.pcmd.parser;

import java.util.ArrayList;
import java.util.List;

import com.polopoly.pcmd.field.CommittedField;
import com.polopoly.pcmd.field.ComponentField;
import com.polopoly.pcmd.field.ContentIdField;
import com.polopoly.pcmd.field.ContentListField;
import com.polopoly.pcmd.field.ContentListSizeField;
import com.polopoly.pcmd.field.ContentRefField;
import com.polopoly.pcmd.field.Field;
import com.polopoly.pcmd.field.InputTemplateField;
import com.polopoly.pcmd.field.LockerField;
import com.polopoly.pcmd.field.NameField;
import com.polopoly.pcmd.field.NumericalContentIdField;
import com.polopoly.pcmd.field.PaddingField;
import com.polopoly.pcmd.field.VersionField;

public class FieldListParser implements Parser<List<Field>> {

    public static final char PREFIX_FIELD_SEPARATOR = ':';
    private static final String VERSION = "version";
    private static final String COMMITTED = "committed";
    private static final String NAME = "name";
    public static final String COMPONENT = "component";
    public static final String CONTENT_REF = "ref";
    private static final String CONTENT_LIST = "contentlist";
    private static final String CONTENT_LIST_SIZE = "contentlistsize";
    public static final String ID = "id";
    private static final String INPUT_TEMPLATE = "inputtemplate";
    public static final String NUMERICAL_ID = "numericalid";
    private static final String LOCKER = "locker";

    public String getHelp() {
        return ID + " / " + NUMERICAL_ID + " / " + NAME + " / " + COMPONENT + ":" + new ComponentParser().getHelp() + " / " + CONTENT_REF + ":" +
            new ContentRefParser().getHelp() + " / " + CONTENT_LIST + "[:<content list>] / " +
            CONTENT_LIST_SIZE + " / " + LOCKER + " / " + INPUT_TEMPLATE + " / " + COMMITTED +
            " [:<content list>] (append :<width> to any field to pad it)";
    }

    public List<Field> parse(String value) throws ParseException {
        // this isn't ideal as no field names can contain commas. we should allow quoting or something.
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
                int width = Integer.parseInt(field.substring(i+1));

                return new PaddingField(parseField(field.substring(0, i)), width);
            }
            catch (NumberFormatException nfe) {
                // it didn't.
            }
        }

        if (field.equals(NAME)) {
            return new NameField();
        }
        else if (field.equals(ID)) {
            return new ContentIdField();
        }
        else if (field.equals(NUMERICAL_ID)) {
            return new NumericalContentIdField();
        }
        else if (field.equals(LOCKER)) {
            return new LockerField();
        }
        else if (field.equals(COMMITTED)) {
            return new CommittedField();
        }
        else if (field.equals(VERSION)) {
            return new VersionField();
        }
        else if (field.equals(INPUT_TEMPLATE)) {
            return new InputTemplateField();
        }
        else if (field.startsWith(NAME + ':')) {
            return new ComponentField(new ComponentParser().parse(field.substring(NAME.length() + 1)));
        }
        else if (field.startsWith(CONTENT_REF + PREFIX_FIELD_SEPARATOR)) {
            return new ContentRefField(new ContentRefParser().parse(field.substring(CONTENT_REF.length() + 1)));
        }
        else if (field.startsWith(CONTENT_LIST_SIZE)) {
            String contentListName = null;

            if (field.startsWith(CONTENT_LIST_SIZE + ":")) {
                contentListName = field.substring(CONTENT_LIST_SIZE.length() + 1);
            }

            return new ContentListSizeField(contentListName);
        }
        else if (field.startsWith(CONTENT_LIST)) {
            String contentListName = null;

            if (field.startsWith(CONTENT_LIST + ":")) {
                contentListName = field.substring(CONTENT_LIST.length() + 1);
            }

            return new ContentListField(contentListName);
        }
        else {
            throw new ParseException(this, field, "Unknown field");
        }
    }
}
