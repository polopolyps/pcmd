package com.polopoly.util.content;

import java.util.Iterator;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.client.Content;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.client.InputTemplate;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.FetchingIterator;
import com.polopoly.util.contentid.ContentIdUtil;
import com.polopoly.util.contentid.ContentIterable;
import com.polopoly.util.contentlist.ContentListUtil;
import com.polopoly.util.contentlist.ContentListUtilImpl;
import com.polopoly.util.exception.ContentGetException;
import com.polopoly.util.exception.PolicyGetException;
import com.polopoly.util.policy.InputTemplateUtil;
import com.polopoly.util.policy.Util;

public class ContentUtilImpl extends RuntimeExceptionContentWrapper implements ContentUtil {
    private Content content;
    private PolopolyContext context;

    /**
     * Use {@link Util#util(ContentRead, PolicyCMServer)} to get an instance.
     */
    public ContentUtilImpl(ContentRead content, PolopolyContext context) {
        super(content);

        this.content = (Content) content;
        this.context = context;
    }

    @Override
    public ContentListUtil getContentList() {
        try {
            return new ContentListUtilImpl(content.getContentList(), this, context);
        } catch (CMException e) {
            throw new CMRuntimeException("While getting default content list in " + this + ": " + e.getMessage(), e);
        }
    }

    @Override
    public ContentListUtil getContentList(String contentList) {
        try {
            return new ContentListUtilImpl(content.getContentList(contentList), this, context);
        } catch (CMException e) {
            throw new CMRuntimeException("While getting content list \"" + contentList + "\" in " + this + ": " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        try {
            String contentIdString;

            contentIdString = getExternalIdString();

            if (contentIdString == null) {
                contentIdString = getContentId().unversioned().getContentIdString();
            }

            return getName() + " (" + contentIdString + ")";
        }
        catch (Exception e) {
            return content.getContentId() + " (" + e.toString() + ")";
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ContentRead &&
            ((ContentRead) obj).getContentId().equals(getContentId());
    }

    @Override
    public int hashCode() {
        return getContentId().hashCode();
    }

    @Override
    public void commit() throws CMException {
        try {
            super.commit();
        }
        catch (CMException e) {
            throw new CMException("While committing " + this + ": " + e.getMessage(), e);
        }
    }

    public String getContentIdString() {
        return getContentId().getContentId().getContentIdString();
    }

    @Override
    public ContentIdUtil getContentReference(String groupName, String name) {
        return Util.util(super.getContentReference(groupName, name), context);
    }

    @Override
    public ContentIdUtil getInputTemplateId() {
        return Util.util(super.getInputTemplateId(), context);
    }

    @Override
    public ContentIdUtil getSecurityParentId() {
        return Util.util(super.getSecurityParentId(), context);
    }

    public PolopolyContext getContext() {
        return context;
    }

    public PolicyCMServer getPolicyCMServer() {
        return context.getPolicyCMServer();
    }

    public String getExternalIdString() {
        ExternalContentId externalId = getExternalId();

        if (externalId != null) {
            return externalId.getExternalId();
        }
        else {
            return null;
        }
    }

    public ContentIterable getSecurityParentChain() {
        Iterable<ContentId> securityParentIdIterable =
            new Iterable<ContentId>() {
                public Iterator<ContentId> iterator() {
                    return new FetchingIterator<ContentId>() {
                        ContentUtil at = ContentUtilImpl.this;

                        @Override
                        protected ContentId fetch() {
                            if (at == null) {
                                return null;
                            }

                            ContentIdUtil parent = at.getSecurityParentId();

                            if (parent != null) {
                                try {
                                    at = parent.asContent();
                                } catch (ContentGetException e) {
                                    // the caller will log this for us.
                                    at = null;
                                }
                            }

                            return parent;
                        }

                        @Override
                        public String toString() {
                            return "security parent chain of " + ContentUtilImpl.this;
                        }
                    };
                }};

        return new ContentIterable(getContext(), securityParentIdIterable);
    }

    public InputTemplateUtil getInputTemplate() {
        try {
            return Util.util(context.getPolicy(getInputTemplateId(), InputTemplate.class), context);
        } catch (PolicyGetException e) {
            throw new CMRuntimeException("While getting input template of " + this + ": " + e.getMessage(), e);
        }
    }

    @Override
    public ContentIdUtil getContentId() {
        return Util.util(super.getContentId(), context);
    }
}
