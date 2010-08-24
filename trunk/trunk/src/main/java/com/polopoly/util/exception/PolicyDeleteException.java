package com.polopoly.util.exception;

import com.polopoly.cm.client.CMException;

public class PolicyDeleteException extends CMException {

    public PolicyDeleteException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public PolicyDeleteException(String arg0) {
        super(arg0);
    }

    public PolicyDeleteException(Throwable arg0) {
        super(arg0);
    }

}
