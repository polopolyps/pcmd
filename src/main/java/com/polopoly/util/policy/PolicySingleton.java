package com.polopoly.util.policy;

import static com.polopoly.util.policy.Util.util;

import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.Policy;
import com.polopoly.util.Require;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.exception.CannotFetchSingletonException;
import com.polopoly.util.exception.NoSuchPolicyException;
import com.polopoly.util.exception.PolicyCreateException;
import com.polopoly.util.exception.PolicyGetException;

public class PolicySingleton {

	private int major;
	private String externalId;
	private String inputTemplate;
	private PolopolyContext context;

	public PolicySingleton(PolopolyContext context, int major,
			String externalId, String inputTemplate) {
		this.major = major;
		this.context = Require.require(context);
		this.externalId = Require.require(externalId);
		this.inputTemplate = Require.require(inputTemplate);
	}

	public PolicyUtil get() throws CannotFetchSingletonException {
		try {
			return context.getPolicyUtil(externalId);
		} catch (NoSuchPolicyException e) {
			try {
				return util(context.createPolicy(major, inputTemplate,
						new PolicyModification<Policy>() {
							@Override
							public void modify(Policy newVersion)
									throws CMException {
								newVersion.getContent().setExternalId(
										externalId);
							}
						}));
			} catch (PolicyCreateException ce) {
				throw new CannotFetchSingletonException(ce);
			}
		} catch (PolicyGetException e) {
			throw new CannotFetchSingletonException(e);
		}
	}

	public <T> T get(Class<T> policyClass) throws CannotFetchSingletonException {
		return get(policyClass, new EmptyPolicyModification<T>());
	}

	public <T> T get(Class<T> policyClass,
			final PolicyModification<T> modification)
			throws CannotFetchSingletonException {
		try {
			return context.getPolicy(externalId, policyClass);
		} catch (NoSuchPolicyException e) {
			try {
				return context.createPolicy(major, inputTemplate, null,
						policyClass, new PolicyModification<T>() {
							@Override
							public void modify(T newVersion) throws CMException {
								((Policy) newVersion).getContent()
										.setExternalId(externalId);

								modification.modify(newVersion);
							}
						});
			} catch (PolicyCreateException ce) {
				throw new CannotFetchSingletonException(ce);
			}
		} catch (PolicyGetException e) {
			throw new CannotFetchSingletonException(e);
		}
	}

}
