package com.polopoly.ps.pcmd.tool.xml.export;

import org.w3c.dom.Element;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.xml.util.export.ContentReferenceExporter;
import com.polopoly.cm.xml.util.export.ExportFailureException;
import com.polopoly.ps.pcmd.tool.xml.export.contentlistentry.ContentReferenceFilter;

public class FilteringContentReferenceExporter implements ContentReferenceExporter {
    private ContentReferenceExporter delegate;

    private ContentReferenceFilter filter;

    public FilteringContentReferenceExporter(ContentReferenceFilter filter, ContentReferenceExporter delegate) {
        this.delegate = delegate;
        this.filter = filter;
    }

    public void exportContentReference(Element contentElement, String group, String name, ContentRead referringContent) {
        try {
            ContentId referred = referringContent.getContentReference(group, name);

            if (filter.isAllowed(referringContent, referred)) {
                delegate.exportContentReference(contentElement, group, name, referringContent);
            }
        } catch (CMException e) {
            throw new ExportFailureException("Couldn't export content reference " + group + ":" + name + " from "
                                             + referringContent.getContentId(), e);
        }
    }

}
