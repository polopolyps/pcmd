package com.polopoly.ps.pcmd.tool;

import java.util.List;

import com.polopoly.ps.pcmd.argument.Parameters;
import com.polopoly.ps.pcmd.field.content.Field;

/**
 * The parameters for a tool for which you can specified the fields to print.
 */
public interface FieldListParameters extends Parameters {
    List<Field> getFieldList();
    String getDelimiter();
}
