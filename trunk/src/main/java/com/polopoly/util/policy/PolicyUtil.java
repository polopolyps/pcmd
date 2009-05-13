package com.polopoly.util.policy;

import com.polopoly.cm.policy.Policy;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.content.ContentUtil;
import com.polopoly.util.contentid.ContentIdUtil;
import com.polopoly.util.contentlist.ContentListUtil;
import com.polopoly.util.exception.PolicyGetException;
import com.polopoly.util.exception.PolicyModificationException;

public interface PolicyUtil extends Iterable<Policy>, RuntimeExceptionPolicy {
    PolopolyContext getContext();
    ContentUtil getContent();
    String getName();
    void setSingleValued(String field, String value);
    String getSingleValued(String field, String defaultValue);
    String getSingleValued(String field);
    <T> T getSingleReference(String field, Class<T> policyClass) throws PolicyGetException;
    ContentIdUtil getSingleReference(String field) throws PolicyGetException;
    ContentListUtil getContentListAware(String field);
    <T> T getChildPolicy(String field, Class<T> klass);
    <T> void modify(PolicyModification<T> policyModification, Class<T> klass, boolean createNewVersion) throws PolicyModificationException;
    <T> void modify(PolicyModification<T> policyModification, Class<T> klass) throws PolicyModificationException;
    Policy asPolicy();
}