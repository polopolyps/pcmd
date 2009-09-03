package com.polopoly.util.contentlist;

import java.util.List;

import com.polopoly.cm.policy.Policy;
import com.polopoly.pcmd.tool.ContentReferenceUtil;
import com.polopoly.util.content.ContentUtil;
import com.polopoly.util.contentid.ContentIdUtil;
import com.polopoly.util.contentlist.ContentListUtilImpl.ContentListContentIds;
import com.polopoly.util.exception.CMModificationException;

public interface ContentListUtil extends RuntimeExceptionContentList, Iterable<Policy> {
    Iterable<ContentUtil> contents();
    <T> Iterable<T> policies(final Class<T> policyClass);
    <T> List<T> policyList(final Class<T> policyClass);
    ContentListContentIds contentIds();
    void add(int index, Policy policy) throws CMModificationException;
    void add(Policy policy) throws CMModificationException;
    void remove(Policy policy) throws CMModificationException;
    ContentIdUtil get(int i);
    <T extends Policy> T get(int i, Class<T> klass);
    boolean contains(Policy policy);
    Iterable<ContentReferenceUtil> references();
}
