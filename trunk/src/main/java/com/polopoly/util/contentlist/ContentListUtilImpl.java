package com.polopoly.util.contentlist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ContentReference;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.collections.ContentList;
import com.polopoly.cm.collections.ContentListRead;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.cm.policy.ReferenceMetaDataPolicy;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.CheckedContentIdToPolicyIterator;
import com.polopoly.util.collection.ContentIdToContentUtilIterator;
import com.polopoly.util.collection.ContentIdToPolicyIterator;
import com.polopoly.util.collection.ContentListIterator;
import com.polopoly.util.collection.ContentListListAdapter;
import com.polopoly.util.collection.TransformingIterator;
import com.polopoly.util.content.ContentUtil;
import com.polopoly.util.contentid.ContentIdUtil;
import com.polopoly.util.exception.PolicyGetException;
import com.polopoly.util.policy.PolicyUtil;
import com.polopoly.util.policy.Util;

public class ContentListUtilImpl extends RuntimeExceptionContentListWrapper
        implements ContentListUtil {
    public class ContentListContentIds implements Iterable<ContentId> {
        public Iterator<ContentId> iterator() {
            return new ContentListIterator(contentList) {
                @Override
                public String toString() {
                    return ContentListUtilImpl.this.toString();
                }
            };
        }

        public void add(int index, ContentId contentId) {
            add(index, contentId, null);
        }

        public void add(int index, ContentId contentId,
                ContentId referenceMetaDataId) {
            try {
                contentList.add(index, new ContentReference(contentId,
                        referenceMetaDataId));
            } catch (CMException e) {
                throw new CMRuntimeException("While adding "
                        + contentId.getContentIdString() + " to " + this + ": "
                        + e.getMessage(), e);
            }
        }

        public void add(ContentId contentId) {
            add(size(), contentId);
        }

        public void remove(ContentId contentId) {
            for (int i = contentList.size() - 1; i >= 0; i--) {
                if (get(i).equalsIgnoreVersion(contentId)) {
                    remove(i);
                }
            }
        }

        private void remove(int index) {
            contentList.remove(index);
        }

        public int size() {
            return contentList.size();
        }

        public List<ContentId> toList() {
            return new ContentListListAdapter(contentList, toString);
        }

        public ContentIdUtil get(int i) {
            try {
                ContentId result = contentList.getEntry(i)
                        .getReferredContentId();

                if (result != null) {
                    return Util.util(result, getContext());
                } else {
                    return null;
                }
            } catch (CMException e) {
                throw new CMRuntimeException("While getting entry " + i
                        + " in " + ContentListUtilImpl.this.toString() + ": "
                        + e.getMessage(), e);
            }
        }

        @Override
        public String toString() {
            return ContentListUtilImpl.this.toString();
        }
    }

    private ContentList contentList;

    private PolicyCMServer server;

    private PolopolyContext lazyContext;

    private Object toString;

    /**
     * Use {@link Util#util(ContentListRead, PolicyCMServer)} to get an
     * instance.
     */
    public ContentListUtilImpl(ContentListRead contentList, Object toString,
            PolopolyContext context) {
        this(contentList, toString, context.getPolicyCMServer());

        if (context == null) {
            throw new IllegalArgumentException("Context was null.");
        }

        this.lazyContext = context;
    }

    /**
     * Use {@link Util#util(ContentListRead, PolicyCMServer)} to get an
     * instance.
     */
    public ContentListUtilImpl(ContentListRead contentList, Object toString,
            PolicyCMServer server) {
        super(contentList);

        this.contentList = (ContentList) contentList;
        this.server = server;
        this.toString = toString;
    }

    public Iterator<Policy> iterator() {
        return new ContentIdToPolicyIterator(server, contentIds().iterator());
    }

    public <T> Iterable<T> policies(final Class<T> policyClass) {
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return new CheckedContentIdToPolicyIterator<T>(server,
                        contentIds().iterator(), policyClass);
            }
        };
    }

    public Iterable<PolicyUtil> policyUtils() {
        return new Iterable<PolicyUtil>() {
            public Iterator<PolicyUtil> iterator() {
                return new TransformingIterator<Policy, PolicyUtil>(
                        new CheckedContentIdToPolicyIterator<Policy>(server,
                                contentIds().iterator(), Policy.class)) {
                    @Override
                    protected PolicyUtil transform(Policy next) {
                        return Util.util(next);
                    }
                };
            }
        };
    }

    public ContentListContentIds contentIds() {
        return new ContentListContentIds();
    }

    public Iterable<ContentUtil> contents() {
        return new Iterable<ContentUtil>() {
            public Iterator<ContentUtil> iterator() {
                return new ContentIdToContentUtilIterator(server, contentIds()
                        .iterator());
            }
        };
    }

    public void add(int index, Policy policy) {
        contentIds().add(index, policy.getContentId().getContentId());
    }

    public void add(Policy policy) {
        add(size(), policy);
    }

    public void add(int index, Policy policy,
            ReferenceMetaDataPolicy referenceMetaData) {
        contentIds().add(
                index,
                policy.getContentId().getContentId(),
                (referenceMetaData != null ? referenceMetaData.getContentId()
                        .getContentId() : null));
    }

    public void add(Policy policy, ReferenceMetaDataPolicy referenceMetaData) {
        add(size(), policy, referenceMetaData);
    }

    public void remove(Policy policy) {
        contentIds().remove(policy.getContentId());
    }

    public ContentIdUtil get(int i) {
        return contentIds().get(i);
    }

    public <T> T get(int i, Class<T> klass) throws PolicyGetException {
        return getContext().getPolicy(contentIds().get(i), klass);
    }

    private PolopolyContext getContext() {
        if (lazyContext == null) {
            lazyContext = Util.util(server);
        }

        return lazyContext;
    }

    public boolean contains(Policy policy) {
        return indexOf(policy) >= 0;
    }

    public PolicyCMServer getPolicyCMServer() {
        return server;
    }

    @Override
    public String toString() {
        String group;
        try {
            group = contentList.getContentListStorageGroup();
        } catch (CMException e) {
            group = e.getMessage();
        }

        if (toString == null) {
            return "content list \"" + group + "\"";
        } else {
            return "content list \"" + group + "\" in " + toString.toString();
        }
    }

    public Iterable<ContentReferenceUtil> references() {
        return new Iterable<ContentReferenceUtil>() {
            public Iterator<ContentReferenceUtil> iterator() {
                return new ContentReferenceIterator(ContentListUtilImpl.this,
                        getContext());
            }
        };
    }

    @Override
    public ContentReferenceUtil getEntry(int index) {
        ContentReference unwrapped = super.getEntry(index);

        return new ContentReferenceUtil(unwrapped, this, getContext());
    }

    public <T> List<T> policyList(Class<T> policyClass) {
        List<T> result = new ArrayList<T>();

        for (T policy : policies(policyClass)) {
            result.add(policy);
        }

        return result;
    }

    public void clear() {
        while (size() > 0) {
            remove(0);
        }
    }

    public int indexOf(Policy policy) {
        VersionedContentId policyId = policy.getContentId();

        for (int i = size() - 1; i >= 0; i--) {
            if (getEntry(i).getReferredContentId()
                    .equalsIgnoreVersion(policyId)) {
                return i;
            }
        }

        return -1;
    }
}
