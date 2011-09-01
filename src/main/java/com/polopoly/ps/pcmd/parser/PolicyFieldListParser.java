package com.polopoly.ps.pcmd.parser;

import java.util.ArrayList;
import java.util.List;

import com.polopoly.ps.pcmd.field.policy.ClassField;
import com.polopoly.ps.pcmd.field.policy.InputTemplateField;
import com.polopoly.ps.pcmd.field.policy.NameField;
import com.polopoly.ps.pcmd.field.policy.PaddingField;
import com.polopoly.ps.pcmd.field.policy.PolicyField;
import com.polopoly.ps.pcmd.field.policy.ValueField;

public class PolicyFieldListParser implements Parser<List<PolicyField>> {

    public static final char PREFIX_FIELD_SEPARATOR = ':';
    private static final String NAME = "name";
    private static final String VALUE = "value";
    private static final String CLASS = "class";
    private static final String INPUT_TEMPLATE = "inputtemplate";

    public String getHelp() {
       return NAME + " / " + VALUE + " / " + CLASS + " / " + INPUT_TEMPLATE +
           " (append :<width> to any field to pad it)";
    }

    public List<PolicyField> parse(String value) throws ParseException {
        // this isn't ideal as no field names can contain commas. we should allow quoting or something.
        String[] fields = value.split(",");

        List<PolicyField> result = new ArrayList<PolicyField>();

        for (String field : fields) {
            result.add(parseField(field));
        }

        return result;
    }

    private PolicyField parseField(String field) throws ParseException {
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
        else if (field.equals(VALUE)) {
            return new ValueField();
        }
        else if (field.equals(CLASS)) {
            return new ClassField();
        }
        else if (field.equals(INPUT_TEMPLATE)) {
            return new InputTemplateField();
        }
        else {
            throw new ParseException(this, field, "Unknown field");
        }
    }
}
