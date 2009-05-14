package com.polopoly.util.contentlist;

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
import com.polopoly.pcmd.tool.ContentReferenceUtil;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.CheckedContentIdToPolicyIterator;
import com.polopoly.util.collection.ContentIdToContentUtilIterator;
import com.polopoly.util.collection.ContentIdToPolicyIterator;
import com.polopoly.util.collection.ContentListIterator;
import com.polopoly.util.collection.ContentListListAdapter;
import com.polopoly.util.content.ContentUtil;
import com.polopoly.util.contentid.ContentIdUtil;
import com.polopoly.util.exception.CMModificationException;
import com.polopoly.util.policy.Util;

public class ContentListUtilImpl extends RuntimeExceptionContentListWrapper implements ContentListUtil {
    public class ContentListContentIds implements Iterable<ContentId> {
        public Iterator<ContentId> iterator() {
            return new ContentListIterator(contentList) {
                @Override
                public String toString() {
                    return ContentListUtilImpl.this.toString();
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

        public ContentIdUtil get(int i) {
            try {
                ContentId result = contentList.getEntry(i).getReferredContentId();

                if (result != null) {
                    return Util.util(result, context);
                }
                else {
                    return null;
                }
            } catch (CMException e) {
                throw new CMRuntimeException(
                    "While getting entry " + i + " in " + ContentListUtilImpl.this.toString() +
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
    public ContentListUtilImpl(ContentListRead contentList, Object toString, PolopolyContext context) {
        this(contentList, toString, context.getPolicyCMServer());

        this.context = context;
    }

    /**
     * Use {@link Util#util(ContentListRead, PolicyCMServer)} to get an instance.
     */
    public ContentListUtilImpl(ContentListRead contentList, Object toString, PolicyCMServer server) {
        super(contentList);

        this.contentList = (ContentList) contentList;
        this.server = server;
        this.toString = toString;
    }

    public Iterator<Policy> iterator() {
        return new ContentIdToPolicyIterator(server, contentIds().iterator());
    }

    public <T extends Policy> Iterable<T> policies(final Class<T> policyClass) {
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return new CheckedContentIdToPolicyIterator<T>(
                        server, contentIds().iterator(), policyClass);
            }
        };
    }

    public ContentListContentIds contentIds() {
        return new ContentListContentIds();
    }

    public Iterable<ContentUtil> contents() {
        return new Iterable<ContentUtil>() {
            public Iterator<ContentUtil> iterator() {
                return new ContentIdToContentUtilIterator(server, contentIds().iterator());
            }
        };
    }

    public void add(int index, Policy policy) throws CMModificationException {
        contentIds().add(index, policy.getContentId().getContentId());
    }

    public void add(Policy policy) throws CMModificationException {
        add(size(), policy);
    }

    public void remove(Policy policy) throws CMModificationException {
        contentIds().remove(policy.getContentId());
    }

    public ContentIdUtil get(int i) {
        return contentIds().get(i);
    }

    public <T extends Policy> T get(int i, Class<T> klass) {
        try {
            return getContext().getPolicy(contentIds().get(i), klass);
        } catch (CMException e) {
            throw new CMRuntimeException(
                "While getting entry " + i + " in " + ContentListUtilImpl.this.toString() +
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

        for (ContentId childId : contentIds()) {
            if (childId.equalsIgnoreVersion(policyId)) {
                return true;
            }
        }

        return false;
    }

    public PolicyCMServer getPolicyCMServer() {
        return server;
    }

    @Override
    public String toString() {
        if (toString == null) {
            return "content list";
        }
        else {
            return "content list in " + toString.toString();
        }
    }

    public Iterable<ContentReferenceUtil> references() {
        return new Iterable<ContentReferenceUtil>() {
            public Iterator<ContentReferenceUtil> iterator() {
                return new ContentReferenceIterator(ContentListUtilImpl.this, getContext());
            }};
    }
}
