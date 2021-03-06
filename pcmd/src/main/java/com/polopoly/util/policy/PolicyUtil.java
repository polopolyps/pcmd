package com.polopoly.util.policy;

import com.polopoly.cm.policy.Policy;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.content.ContentUtil;
import com.polopoly.util.contentid.ContentIdUtil;
import com.polopoly.util.contentlist.ContentListUtil;
import com.polopoly.util.exception.EmptyListException;
import com.polopoly.util.exception.InvalidTopPolicyClassException;
import com.polopoly.util.exception.NoSuchChildPolicyException;
import com.polopoly.util.exception.PolicyDeleteException;
import com.polopoly.util.exception.PolicyGetException;
import com.polopoly.util.exception.PolicyModificationException;
import com.polopoly.util.exception.ReferenceNotSetException;

public interface PolicyUtil extends Iterable<Policy>, RuntimeExceptionPolicy {
	PolopolyContext getContext();

	ContentUtil getContent();

	String getName();

	ContentIdUtil getContentId();

	void setSingleValued(String field, String value);

	String getSingleValued(String field, String defaultValue) throws NoSuchChildPolicyException;

	String getSingleValued(String field) throws NoSuchChildPolicyException;

	boolean getChecked(String field) throws NoSuchChildPolicyException;

	void setChecked(String field, boolean checked) throws NoSuchChildPolicyException;

	<T> T getSingleReference(String field, Class<T> policyClass) throws ReferenceNotSetException,
			PolicyGetException, NoSuchChildPolicyException;

	ContentIdUtil getSingleReference(String field) throws ReferenceNotSetException, PolicyGetException,
			NoSuchChildPolicyException;

	void setSingleReference(String field, Policy policy);

	<T> T getSingleReference(Class<T> policyClass) throws PolicyGetException;

	ContentIdUtil getSingleReference() throws PolicyGetException;

	void setSingleReference(Policy reference);

	ContentListUtil getContentListAware(String field);

	<T> T getChildPolicy(String field, Class<T> klass);

	<T> T modify(PolicyModification<T> policyModification, Class<T> klass, boolean createNewVersion)
			throws PolicyModificationException;

	<T> T modify(PolicyModification<T> policyModification, Class<T> klass) throws PolicyModificationException;

	<T> void modifyUtil(PolicyModification<PolicyUtil> policyModification) throws PolicyModificationException;

	Policy asPolicy();

	InputTemplateUtil getInputTemplate();

	void delete() throws PolicyDeleteException;

	Policy getTopPolicy();

	<T> T getTopPolicy(Class<T> policyClass) throws InvalidTopPolicyClassException;

	ContentIdUtil getContentReference(String name);

	<T> T getSingleReferenceInList(String field, Class<T> policyClass) throws PolicyGetException,
			EmptyListException;

	void setSingleReferenceInList(String field, Policy policy);
}