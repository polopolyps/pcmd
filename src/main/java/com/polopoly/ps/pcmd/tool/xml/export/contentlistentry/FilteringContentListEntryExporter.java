package com.polopoly.ps.pcmd.tool.xml.export.contentlistentry;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ContentReference;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.collections.ContentListRead;
import com.polopoly.cm.xml.util.export.ContentListEntryExporter;

public class FilteringContentListEntryExporter implements ContentListEntryExporter {
    private static final Logger logger = Logger.getLogger(FilteringContentListEntryExporter.class.getName());

    private ContentListEntryExporter delegate;
    private ContentReferenceFilter filter;

    public FilteringContentListEntryExporter(ContentListEntryExporter delegate, ContentReferenceFilter filter) {
        this.delegate = delegate;
        this.filter = filter;
    }

    public void exportContentListEntry(Element contentListElement, ContentListRead contentList, ContentRead content,
                                       int position) {
        try {
            ContentReference entry = contentList.getEntry(position);
            ContentId referredId = entry.getReferredContentId();
            ContentId referenceMetadata = entry.getReferenceMetaDataId();

            if ((referredId == null || filter.isAllowed(content, referredId))
                && (referenceMetadata == null || filter.isAllowed(content, referenceMetadata))) {
                delegate.exportContentListEntry(contentListElement, contentList, content, position);
            }
        } catch (CMException e) {
            logger.log(Level.WARNING,
                       "While fetching content list entry in "
                           + content.getContentId().getContentId().getContentIdString() + ": " + e.getMessage(), e);
        }
    }
}
