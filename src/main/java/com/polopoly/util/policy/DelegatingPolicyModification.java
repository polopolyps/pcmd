package com.polopoly.util.policy;

import static com.polopoly.util.Require.require;

import com.polopoly.cm.client.CMException;

public class DelegatingPolicyModification<T> implements PolicyModification<T> {
	private PolicyModification<T> modification;
	
	public DelegatingPolicyModification(PolicyModification<T> modification) {
		super();
		this.modification = require(modification);
	}

	public void modify(T newVersion) throws CMException {
		modification.modify(newVersion);
	}
}
