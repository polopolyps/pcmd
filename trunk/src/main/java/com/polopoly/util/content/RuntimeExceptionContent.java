package com.polopoly.util.content;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.ContentRead;

/**
 * A content object that throws RuntimeExceptions rather than checked
 * CMExceptions for the exceptions that are least likely to occur in practice.
 */
public interface RuntimeExceptionContent extends ContentRead {
    String getName();

    String[] getComponentGroupNames();

    String[] getComponentNames(String groupName);

    String getComponent(String groupName, String name);

    String[] getContentReferenceGroupNames();

    String[] getContentReferenceNames(String groupName);

    ContentId getContentReference(String groupName, String name);

    ContentId getInputTemplateId();

    ContentId getOutputTemplateId(String mode);

    ExternalContentId getExternalId();

    void setComponent(String group, String name, String value);
}
