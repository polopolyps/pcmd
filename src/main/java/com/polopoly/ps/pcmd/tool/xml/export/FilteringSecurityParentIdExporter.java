package com.polopoly.ps.pcmd.tool.xml.export;

import org.w3c.dom.Element;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.xml.util.export.ContentIdExporter;
import com.polopoly.ps.pcmd.tool.xml.export.contentlistentry.ContentReferenceFilter;

public class FilteringSecurityParentIdExporter implements ContentIdExporter {
    static final String DEFAULT_SECURITY_PARENT = "p.SecurityRootDepartment";

    private ContentReferenceFilter filter;
    private ContentIdExporter delegate;

    public FilteringSecurityParentIdExporter(ContentReferenceFilter filter, ContentIdExporter delegate) {
        this.filter = filter;
        this.delegate = delegate;
    }

    public void exportContentId(Element contentIdElement, ContentId contentIdToExport) {
        if (filter.isAllowed(null, contentIdToExport)) {
            delegate.exportContentId(contentIdElement, contentIdToExport);
        } else {
            delegate.exportContentId(contentIdElement, new ExternalContentId(2, DEFAULT_SECURITY_PARENT));
        }
    }

}
