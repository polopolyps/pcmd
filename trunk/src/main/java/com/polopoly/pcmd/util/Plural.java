package com.polopoly.pcmd.util;

import java.util.Collection;

public class Plural {
    public static String plural(int number) {
        if (number == 1) {
            return "";
        }
        else {
            return "s";
        }
    }

    public static String plural(Collection<?> collection) {
        return plural(collection.size());
    }

    public static String count(int count,
            String noun) {
        return count + " " + noun + plural(count);
    }

    public static String count(Collection<?> collection,
            String noun) {
        return count(collection.size(), noun);
    }
}
