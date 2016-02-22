package com.polopoly.ps.pcmd.tool.xml.export;

import java.util.HashSet;
import java.util.Set;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.cm.xml.util.export.ContentExporterFactory;
import com.polopoly.cm.xml.util.export.DefaultContentContentsExporter;
import com.polopoly.cm.xml.util.export.DefaultContentListEntryExporter;
import com.polopoly.cm.xml.util.export.DefaultContentListExporter;
import com.polopoly.cm.xml.util.export.DefaultContentMetadataExporter;
import com.polopoly.cm.xml.util.export.DefaultContentReferenceExporter;
import com.polopoly.cm.xml.util.export.ExternalIdGenerator;
import com.polopoly.cm.xml.util.export.tree.CreatingExternalContentIdExporter;
import com.polopoly.ps.pcmd.tool.xml.export.contentlistentry.ContentReferenceFilter;
import com.polopoly.ps.pcmd.tool.xml.export.contentlistentry.FilteringContentListEntryExporter;
import com.polopoly.ps.pcmd.tool.xml.export.contentlistentry.OnlyContentInSetContentReferenceFilter;
import com.polopoly.ps.pcmd.tool.xml.export.contentlistentry.OrContentReferenceFilter;
import com.polopoly.user.server.UserServer;

public class ContentsExporterFactory {
    private ExternalIdGenerator externalIdGenerator;

    private ContentReferenceFilter contentReferenceFilter;

    private PolicyCMServer cmServer;
    private UserServer userServer;

    public ContentsExporterFactory(PolicyCMServer cmServer, UserServer userServer,
                                   ContentReferenceFilter contentReferenceFilter) {
        this.contentReferenceFilter = contentReferenceFilter;

        this.cmServer = cmServer;
        this.userServer = userServer;

        externalIdGenerator = new PreserveExistingPrefixOthersExternalIdGenerator(cmServer, "export.");
    }

    public DefaultContentContentsExporter createContentsExporter(Set<ContentId> contentIdsToExport) {
        CreatingExternalContentIdExporter contentIdExporter = new CreatingExternalContentIdExporter(cmServer);
        contentIdExporter.setExternalIdGenerator(externalIdGenerator);

        ContentExporterFactory exporterFactory = ContentExporterFactory.getInstanceFor(cmServer, userServer);
        DefaultContentContentsExporter exporter = exporterFactory.createNewExporter(contentIdExporter);
        DefaultContentListExporter contentListExporter = new DefaultContentListExporter(cmServer, userServer);

        // Set up deep copy content list entry exporter with default
        // underlying exporter
        DefaultContentListEntryExporter contentListEntryExporter =
            new DefaultContentListEntryExporter(cmServer, userServer);
        contentListEntryExporter.setContentIdExporter(contentIdExporter);
        contentListEntryExporter.setMetadataExporter(exporter);

        OnlyContentInSetContentReferenceFilter onlyExportedContentFilter =
            new OnlyContentInSetContentReferenceFilter(unversioned(contentIdsToExport));

        OrContentReferenceFilter filter =
            new OrContentReferenceFilter(onlyExportedContentFilter, contentReferenceFilter);

        FilteringContentListEntryExporter filteringContentListEntryExporter =
            new FilteringContentListEntryExporter(contentListEntryExporter, filter);

        contentListExporter.setEntryExporter(filteringContentListEntryExporter);
        exporter.setContentListExporter(contentListExporter);

        DefaultContentMetadataExporter metadataExporter = new DefaultContentMetadataExporter(cmServer);
        metadataExporter.setSecurityParentIdExporter(new FilteringSecurityParentIdExporter(filter, contentIdExporter));
        exporter.setContentMetadataExporter(metadataExporter);

        DefaultContentReferenceExporter contentReferenceExporter = new DefaultContentReferenceExporter(cmServer);
        contentReferenceExporter.setContentIdExporter(contentIdExporter);
        exporter.setContentReferenceExporter(new FilteringContentReferenceExporter(filter, contentReferenceExporter));

        return exporter;
    }

    private Set<ContentId> unversioned(Set<ContentId> contentIdsToExport) {
        Set<ContentId> result = new HashSet<ContentId>(contentIdsToExport.size());

        for (ContentId contentIdToExport : contentIdsToExport) {
            result.add(contentIdToExport.getContentId());
        }

        return result;
    }

    public ExternalIdGenerator getExternalIdGenerator() {
        return externalIdGenerator;
    }

}
