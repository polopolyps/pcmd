package com.polopoly.util.policy;

import com.polopoly.cm.client.CMException;

public class EmptyPolicyModification<T> implements PolicyModification<T> {

	@Override
	public void modify(T newVersion) throws CMException {

		// don't do anything.

	}

}
