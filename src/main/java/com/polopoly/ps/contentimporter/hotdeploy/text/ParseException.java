package com.polopoly.ps.contentimporter.hotdeploy.text;

@SuppressWarnings("serial")
public class ParseException
    extends Exception
{
    public ParseException(final String message,
                          final String line,
                          final int atLine)
    {
        super("At line " + atLine + ", \"" + startOf(line) + "\": " + message);
    }

    private static String startOf(final String line)
    {
        if (line.length() > 40) {
            return line.substring(0, 40) + "...";
        } else {
            return line;
        }
    }
}
