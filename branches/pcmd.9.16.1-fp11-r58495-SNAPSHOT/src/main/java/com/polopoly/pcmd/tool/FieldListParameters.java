package com.polopoly.pcmd.tool;

import java.util.List;

import com.polopoly.pcmd.argument.Parameters;
import com.polopoly.pcmd.field.content.Field;

/**
 * The parameters for a tool for which you can specified the fields to print.
 */
public interface FieldListParameters extends Parameters {
    List<Field> getFieldList();
    String getDelimiter();
}
