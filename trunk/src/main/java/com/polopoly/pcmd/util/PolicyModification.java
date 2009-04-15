package com.polopoly.pcmd.util;

import com.polopoly.cm.client.CMException;

public interface PolicyModification<T> {
	void modify(T newVersion) throws CMException;
}
