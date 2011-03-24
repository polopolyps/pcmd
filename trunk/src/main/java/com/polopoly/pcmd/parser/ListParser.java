package com.polopoly.pcmd.parser;

import java.util.ArrayList;
import java.util.List;

public class ListParser<T> implements Parser<List<T>> {
    private Parser<T> delegate;

    private ListParser(Parser<T> delegate) {
        this.delegate = delegate;
    }
    
    public static <T> ListParser<T> wrap(Parser<T> delegate) {
        return new ListParser<T>(delegate);
    }
    
    public String getHelp() {
        return "[" + delegate.getHelp() + " (, ....)]";
    }

    public List<T> parse(String value) throws ParseException {
        String[] parts = value.split(",");

        List<T> result = new ArrayList<T>();

        for (String part : parts) {
            result.add(delegate.parse(part));
        }

        return result;        
    }
}
