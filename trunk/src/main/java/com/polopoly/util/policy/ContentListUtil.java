package com.polopoly.util.policy;

import java.util.Iterator;
import java.util.List;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ContentReference;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.collections.ContentList;
import com.polopoly.cm.collections.ContentListRead;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.CheckedContentIdToPolicyIterator;
import com.polopoly.util.collection.ContentIdToContentIterator;
import com.polopoly.util.collection.ContentIdToPolicyIterator;
import com.polopoly.util.collection.ContentListIterator;
import com.polopoly.util.collection.ContentListListAdapter;
import com.polopoly.util.exception.CMModificationException;

public class ContentListUtil implements Iterable<Policy> {
    public class ContentListContentIds implements Iterable<ContentId> {
        public Iterator<ContentId> iterator() {
            return new ContentListIterator(contentList) {
                @Override
                public String toString() {
                    return ContentListUtil.this.toString();
                }
            };
        }

        public void add(int index, ContentId contentId) throws CMModificationException {
            try {
                contentList.add(index, new ContentReference(contentId, null));
            }
            catch (CMException e) {
                throw new CMModificationException(
                    "While adding " + contentId + " to " + this + ".");
            }
        }

        public void add(ContentId contentId) throws CMModificationException {
            add(size(), contentId);
        }

        public void remove(ContentId contentId) {
            for (int i = contentList.size()-1; i >= 0; i--) {
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

        public ContentId get(int i) {
            try {
                return contentList.getEntry(i).getReferredContentId();
            } catch (CMException e) {
                throw new CMRuntimeException(
                    "While getting entry " + i + " in " + ContentListUtil.this.toString() +
                        ": " + e.getMessage(), e);
            }
        }
    }

    private ContentList contentList;
    private PolicyCMServer server;
    private PolopolyContext context;
    private Object toString;

    /**
     * Use {@link Util#util(ContentListRead, PolicyCMServer)} to get an instance.
     */
    ContentListUtil(ContentListRead contentList, Object toString, PolopolyContext context) {
        this(contentList, toString, context.getPolicyCMServer());
    }

    /**
     * Use {@link Util#util(ContentListRead, PolicyCMServer)} to get an instance.
     */
    ContentListUtil(ContentListRead contentList, Object toString, PolicyCMServer server) {
        this.contentList = (ContentList) contentList;
        this.server = server;
        this.toString = toString;
    }

    public Iterator<Policy> iterator() {
        return new ContentIdToPolicyIterator(server, getContentIds().iterator());
    }

    public <T extends Policy> Iterable<T> policies(final Class<T> policyClass) {
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return new CheckedContentIdToPolicyIterator<T>(
                        server, getContentIds().iterator(), policyClass);
            }
        };
    }

    public ContentListContentIds getContentIds() {
        return new ContentListContentIds();
    }

    public Iterable<ContentRead> getContents() {
        return new Iterable<ContentRead>() {
            public Iterator<ContentRead> iterator() {
                return new ContentIdToContentIterator(
                        server, getContentIds().iterator());
            }
        };
    }

    public void add(int index, Policy policy) throws CMModificationException {
        getContentIds().add(index, policy.getContentId().getContentId());
    }

    public void add(Policy policy) throws CMModificationException {
        add(size(), policy);
    }

    public void remove(Policy policy) throws CMModificationException {
        getContentIds().remove(policy.getContentId());
    }

    public void remove(int index) throws CMModificationException {
        contentList.remove(index);
    }

    public int size() {
        return contentList.size();
    }

    public Policy get(int i) {
        return get(i, Policy.class);
    }

    public <T extends Policy> T get(int i, Class<T> klass) {
        try {
            return getContext().getPolicy(getContentIds().get(i), klass);
        } catch (CMException e) {
            throw new CMRuntimeException(
                "While getting entry " + i + " in " + ContentListUtil.this.toString() +
                    ": " + e.getMessage(), e);
        }
    }

    private PolopolyContext getContext() {
        if (context == null) {
            context = Util.util(server);
        }

        return context;
    }

    public boolean contains(Policy policy) {
        VersionedContentId policyId = policy.getContentId();

        for (ContentId childId : getContentIds()) {
            if (childId.equalsIgnoreVersion(policyId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        if (toString == null) {
            return "content list";
        }
        else {
            return toString.toString();
        }
    }
}
