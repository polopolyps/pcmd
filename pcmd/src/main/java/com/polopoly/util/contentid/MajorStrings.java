package com.polopoly.util.contentid;

// Not sure, is there a product option for this?
public class MajorStrings {
    public static String get(int major) {
        switch (major) {
        case 1:
            return "article";
        case 2:
            return "department";
        case 7:
            return "layoutelement";
        default:
            return null;
        }
    }
}
