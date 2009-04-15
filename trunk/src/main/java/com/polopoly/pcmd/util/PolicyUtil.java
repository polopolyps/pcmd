package com.polopoly.pcmd.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.app.policy.SingleValued;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;

public class PolicyUtil {
	private Policy policy;

	public PolicyUtil(Policy policy) {
		this.policy = policy;
	}

	public void setSingleValued(String field, String value) {
		try {
			getChildPolicy(field, SingleValued.class).setValue(value);
		} catch (CMException e) {
			throw new CMRuntimeException(
				"Could not set field " + field + " in " + this + ": " + e.getMessage());
		}
	}

	public String getSingleValued(String field) {
		try {
			return getChildPolicy(field, SingleValued.class).getValue();
		} catch (CMException e) {
			throw new CMRuntimeException(
				"Could not get field " + field + " in " + this + ": " + e.getMessage(), e);
		}
	}

	public <T> T getChildPolicy(String field, Class<T> klass) {
		try {
			return CheckedCast.cast(policy.getChildPolicy(field), klass);
		} catch (Exception e) {
			String inputTemplate;
			try {
				inputTemplate = policy.getInputTemplate().getExternalId().getExternalId();
			} catch (CMException e2) {
				inputTemplate = e2.toString();
			}

			throw new CMRuntimeException("Could not get field " + field + " in " + this + " (a " + inputTemplate + ") : " + e.getMessage());
		}
	}

	public void log(Level level, String message) {
		getLogger().log(level, message);
	}

	private Logger getLogger() {
		return Logger.getLogger(policy.getClass().getName());
	}

	public <T> T getPolicy(ContentId contentId, Class<T> klass) throws PolicyGetException {
		try {
			return getPolicy(policy.getCMServer(), contentId, klass);
		} catch (CMException e) {
			throw new PolicyGetException("While fetching policy " + contentId.getContentIdString() + ": " + e.getMessage(), e);
		}
	}

	public static <T> T getPolicy(PolicyCMServer server, ContentId contentId, Class<T> klass) throws PolicyGetException {
		try {
			return CheckedCast.cast(server.getPolicy(contentId), klass);
		} catch (CMException e) {
			throw new PolicyGetException("While fetching policy " + contentId.getContentIdString() + ": " + e.getMessage(), e);
		} catch (CheckedClassCastException e) {
			throw new PolicyGetException("While fetching policy " + contentId.getContentIdString() + ": " + e.getMessage(), e);
		}
	}

	public <T> void modify(PolicyModification<T> policyModification, Class<T> klass) throws PolicyModificationException {
		modify(policy, policyModification, klass);
	}

	public static <T> void modify(Policy policy, PolicyModification<T> policyModification, Class<T> klass) throws PolicyModificationException {
		if (policy.getContent().getLockInfo() != null) {
			throw new PolicyModificationException(
				"Content " + policy.getContentId().getContentId().getContentIdString() + " was locked.");
		}

		try {
			policy = policy.getCMServer().createContentVersion(policy.getContentId());
		} catch (CMException e) {
			throw new PolicyModificationException(
				"While creating new version of " + policy.getContentId().getContentId().getContentIdString() +
				": " + e.getMessage(), e);
		}

		try {
			policyModification.modify(CheckedCast.cast(policy, klass));
		} catch (CMException e) {
			try {
				policy.getCMServer().abortContent(policy);
			} catch (CMException e1) {
			}

			throw new PolicyModificationException("While modifying " +
				policy.getContentId().getContentId().getContentIdString() + ": " + e.getMessage(), e);
		} catch (RuntimeException e) {
			try {
				policy.getCMServer().abortContent(policy);
			} catch (CMException e1) {
			}

			throw e;
		} catch (CheckedClassCastException e) {
			throw new PolicyModificationException("New version of " +
					policy.getContentId().getContentId().getContentIdString() + ": " + e.getMessage(), e);
		}

		try {
			policy.getContent().commit();
		} catch (CMException e) {
			try {
				policy.getCMServer().abortContent(policy);
			} catch (CMException e1) {
			}

			throw new PolicyModificationException("While modifying " +
				policy.getContentId().getContentId().getContentIdString() + ": " + e.getMessage(), e);
		}
	}

	@Override
    public String toString() {
		String name;
		try {
			name = policy.getContent().getName();
		} catch (CMException e) {
			name = e.toString();
		}

		return name +
			" (" + policy.getContentId().getContentId().getContentIdString() + ")";
	}
}
