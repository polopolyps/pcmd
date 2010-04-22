package com.polopoly.util.policy;

import com.polopoly.cm.client.CMException;

public interface PolicyModification<T> {
    void modify(T newVersion) throws CMException;
}
