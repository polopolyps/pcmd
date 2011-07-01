package com.polopoly.ps.pcmd.util;

import com.polopoly.pcmd.tool.Tool;
import com.polopoly.util.CheckedCast;
import com.polopoly.util.CheckedClassCastException;

public class ToolRetriever {
    public static class NoSuchToolException extends Exception {
        public NoSuchToolException(String message) {
            super(message);
        }
    }

    private static final String TOOLS_PACKAGE = "com.polopoly.ps.pcmd.tool";
    private static final String LEGACY_TOOLS_PACKAGE = "com.polopoly.pcmd.tool";

    public   static Tool<?> getTool(String toolShortName) throws NoSuchToolException {
        String toolClassName = toCamelCase(toolShortName) + "Tool";

        Tool<?> tool;

        try {
            try {
                tool = CheckedCast.cast(Class.forName(TOOLS_PACKAGE + "." + toolClassName).newInstance(), Tool.class);
            } catch (NoClassDefFoundError e) {
                try {
                    tool = CheckedCast.cast(Class.forName(LEGACY_TOOLS_PACKAGE + "." + toolClassName).newInstance(), Tool.class);
                } catch (NoClassDefFoundError legacyException) {
                    throw new NoSuchToolException("The tool " + toolClassName + " could not be instantiated: " + e);
                }
            }
        } catch (CheckedClassCastException e) {
            throw new NoSuchToolException("The tool " + toolClassName + " did not implement the Tool interface.");
        } catch (InstantiationException e) {
            throw new NoSuchToolException("The tool " + toolClassName + " could not be instantiated: " + e);
        } catch (IllegalAccessException e) {
            throw new NoSuchToolException("The tool " + toolClassName + " could not be instantiated: " + e);
        } catch (ClassNotFoundException e) {
            throw new NoSuchToolException("Tool \"" + toolShortName + "\" not found. Looked for implement in class " + toolClassName + ".");
        }

        return tool;
    }

    private static String toCamelCase(String tool) {
        if (tool.length() == 0) {
            return "";
        }

        StringBuffer result = new StringBuffer(tool.length());

        boolean nextUppercase = true;

        for (int i = 0; i < tool.length(); i++) {
            char ch = tool.charAt(i);

            if (ch == '-') {
                nextUppercase = true;
                continue;
            }

            if (nextUppercase) {
                result.append(Character.toUpperCase(ch));
                nextUppercase = false;
            } else {
                result.append(ch);
            }
        }

        return result.toString();
    }

    private static String fromCamelCase(String name) {
        if (name.length() == 0) {
            return "";
        }

        StringBuffer result = new StringBuffer(name.length());

        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);

            if (Character.isUpperCase(ch)) {
                if (result.length() > 0) {
                    result.append('-');
                }
                result.append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }

        return result.toString();
    }

    public static String getToolName(Class<?> toolClass) {
        String name = toolClass.getSimpleName();

        if (name.endsWith("Tool")) {
            name = name.substring(0, name.length() - 4);
        }

        return fromCamelCase(name);
    }
}
