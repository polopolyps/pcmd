package com.polopoly.ps.layout.element.util;


public class NoCurrentArticleException extends Exception {
    public NoCurrentArticleException(String message) {
        super(message);
    }

    public NoCurrentArticleException(Throwable cause) {
        super(cause);
    }
}
