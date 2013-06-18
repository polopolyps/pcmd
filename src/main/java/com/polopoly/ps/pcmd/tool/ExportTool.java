package com.polopoly.ps.pcmd.tool;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.cm.util.ContentIdFilter;
import com.polopoly.cm.xml.util.export.ExternalIdGenerator;
import com.polopoly.ps.pcmd.argument.ExportParameters;
import com.polopoly.ps.pcmd.argument.ListExportableParameters;
import com.polopoly.ps.pcmd.tool.export.AcceptanceCollectingContentIdFilter;
import com.polopoly.ps.pcmd.tool.export.ContentFileFormat;
import com.polopoly.ps.pcmd.tool.export.NormalizedFileExporter;
import com.polopoly.ps.pcmd.tool.export.RejectionCollectingContentIdFilter;
import com.polopoly.ps.pcmd.tool.xml.export.ContentsExporterFactory;
import com.polopoly.ps.pcmd.tool.xml.export.SingleContentToContentFileExporter;
import com.polopoly.ps.pcmd.tool.xml.export.SingleContentToXMLFileExporter;
import com.polopoly.ps.pcmd.tool.xml.export.contentlistentry.ContentIdFilterToContentReferenceFilterWrapper;
import com.polopoly.ps.pcmd.tool.xml.export.contentlistentry.ContentReferenceFilter;
import com.polopoly.ps.pcmd.tool.xml.export.filteredcontent.AcceptAllContentIdFilter;
import com.polopoly.ps.pcmd.tool.xml.export.filteredcontent.AcceptNoneContentIdFilter;
import com.polopoly.ps.pcmd.tool.xml.export.filteredcontent.OrContentIdFilter;
import com.polopoly.ps.pcmd.tool.xml.export.filteredcontent.PresentContentFilter;
import com.polopoly.ps.pcmd.tool.xml.export.filteredcontent.ProjectContentFilterFactory;
import com.polopoly.ps.pcmd.util.JoiningIterator;
import com.polopoly.ps.pcmd.util.Plural;
import com.polopoly.ps.pcmd.xml.export.SingleContentToFileExporter;
import com.polopoly.ps.pcmd.xml.normalize.DefaultNormalizationNamingStrategy;
import com.polopoly.ps.pcmd.xml.normalize.RMDBundlingNamingStrategy;
import com.polopoly.ps.pcmd.xml.present.PresentFileReader;
import com.polopoly.util.client.PolopolyContext;

public class ExportTool extends ListExportableTool {
    private PolopolyContext context;

    private ExportParameters parameters;

    @Override
    public ExportParameters createParameters() {
        return new ExportParameters();
    }

    @Override
    public void execute(PolopolyContext context, ListExportableParameters listParameters) {
        parameters = (ExportParameters) listParameters;
        this.context = context;

        File outputDirectory = parameters.getOutputDirectory();

        System.err.println("Scanning project content...");

        ContentIdFilter existingObjectsFilter = createExistingObjectsFilter(listParameters);

        Set<ContentId> contentIdsToExport = getIdsToExport(existingObjectsFilter);

        RejectionCollectingContentIdFilter referenceFilter;

        if (parameters.isFilterReferences()) {
            PresentContentFilter alreadyExportedObjectsFilter = createAlreadyExportedObjectsFilter(outputDirectory);

            ContentIdFilter willBeExportedFilter = createWillBeExportedFilter(contentIdsToExport);

            ContentIdFilter existingOrExportedObjectsFilter =
                or(new OrContentIdFilter(existingObjectsFilter, willBeExportedFilter), alreadyExportedObjectsFilter);

            RejectionCollectingContentIdFilter collectingExistingOrExportedObjectsFilter =
                new RejectionCollectingContentIdFilter(existingOrExportedObjectsFilter);

            referenceFilter = collectingExistingOrExportedObjectsFilter;
        } else {
            RejectionCollectingContentIdFilter collectingAcceptAllFilter =
                new RejectionCollectingContentIdFilter(new AcceptAllContentIdFilter());

            referenceFilter = collectingAcceptAllFilter;
        }

        NormalizedFileExporter normalizedFileExporter =
            createExporter(outputDirectory, contentIdsToExport, new ContentIdFilterToContentReferenceFilterWrapper(
                referenceFilter));

        normalizedFileExporter.export(contentIdsToExport);

        logRejected(referenceFilter);
    }

    private ContentIdFilter createWillBeExportedFilter(final Set<ContentId> contentIdsToExport) {
        final Set<ContentId> unversionedIdsToExport = new HashSet<ContentId>();

        for (ContentId idToExport : contentIdsToExport) {
            unversionedIdsToExport.add(idToExport.getContentId());
        }

        return new ContentIdFilter() {
            @Override
            public boolean accept(ContentId contentId) {
                return unversionedIdsToExport.contains(contentId.getContentId());
            }
        };
    }

    private NormalizedFileExporter createExporter(File outputDirectory, Set<ContentId> contentIdsToExport,
                                                  ContentReferenceFilter contentReferenceFilter) {
        PolicyCMServer server = context.getPolicyCMServer();
        ContentsExporterFactory contentsExporterFactory =
            new ContentsExporterFactory(server, context.getUserServer(), contentReferenceFilter);

        ExternalIdGenerator externalIdGenerator = contentsExporterFactory.getExternalIdGenerator();

        SingleContentToFileExporter exporter;

        if (parameters.getFormat() == ContentFileFormat.TEXT) {
            exporter = new SingleContentToContentFileExporter(server, contentReferenceFilter, externalIdGenerator);
        } else {
            exporter =
                new SingleContentToXMLFileExporter(contentsExporterFactory.createContentsExporter(contentIdsToExport));
        }

        DefaultNormalizationNamingStrategy namingStrategy =
            new RMDBundlingNamingStrategy(server, externalIdGenerator, outputDirectory, parameters.getFormat()
                .getExtension());

        return new NormalizedFileExporter(context, exporter, namingStrategy);
    }

    private ContentIdFilter createExistingObjectsFilter(ListExportableParameters listParameters) {
        return new ProjectContentFilterFactory(context.getPolicyCMServer()).getExistingObjectsFilter(listParameters
            .getProjectContentDirectories());
    }

    private PresentContentFilter createAlreadyExportedObjectsFilter(File outputDirectory) {
        PresentContentFilter alreadyExportedObjectsFilter = new PresentContentFilter(context.getPolicyCMServer());

        System.err.println("Scanning output directory for existing content...");
        new PresentFileReader(outputDirectory, alreadyExportedObjectsFilter).readAndScanContent();

        return alreadyExportedObjectsFilter;
    }

    private void logRejected(RejectionCollectingContentIdFilter collectingFilter) {
        Set<ContentId> rejected = collectingFilter.getCollectedIds();

        if (rejected.isEmpty()) {
            return;
        }

        System.out
            .println("The exported content had references to " + Plural.count(rejected, "content object")
                     + " not part either of the exported data or of project content. These references were excluded. ");

        collectingFilter.printCollectedObjects(context);
    }

    @Override
    protected void logNotExportedBecausePresent(PolopolyContext context,
                                                AcceptanceCollectingContentIdFilter collectingFilter) {
        Set<ContentId> notExportedBecausePresent = collectingFilter.getCollectedIds();

        if (notExportedBecausePresent.isEmpty()) {
            return;
        }

        System.err.println(Plural.count(notExportedBecausePresent, "objects") + " were not exported because they are "
                           + "part of the project data or Polopoly content. Use --"
                           + ExportParameters.EXPORT_PRESENT_OPTION + " to export these too.");
        collectingFilter.printCollectedObjects(context);
    }

    private ContentIdFilter or(ContentIdFilter filter, PresentContentFilter presentContentFilter) {
        if (presentContentFilter.getPresentIds().isEmpty()) {
            return filter;
        } else {
            return new OrContentIdFilter(filter, presentContentFilter);
        }
    }

    private Set<ContentId> getIdsToExport(ContentIdFilter presentContentFilter) {
        int since = parameters.getSince();
        Iterable<ContentId> idIterable;

        ContentIdFilter excludeFilter =
            parameters.isExportPresent() ? new AcceptNoneContentIdFilter() : presentContentFilter;

        if (parameters.isIdsArguments()) {
            idIterable = parameters.getIdArgumentsIterator(excludeFilter, context);
        } else {
            idIterable = super.getIdsToExport(context, since, excludeFilter);
        }

        if (parameters.isIncludeReferrers()) {
            idIterable =
                findReferrers(idIterable, (parameters.isFilterReferences() ? presentContentFilter
                                                                          : new AcceptAllContentIdFilter()));
        }

        Set<ContentId> contentIdsToExport = new HashSet<ContentId>(250);

        for (ContentId idToExport : idIterable) {
            contentIdsToExport.add(idToExport);
        }

        return contentIdsToExport;
    }

    private Iterable<ContentId> findReferrers(final Iterable<ContentId> idIterable, ContentIdFilter contentIdFilter) {
        System.out.println("Finding refererring content...");

        final Set<ContentId> allReferrers = new HashSet<ContentId>(100);

        int searched = 0;

        System.out.println("HAS: " + idIterable.iterator().hasNext());

        for (ContentId contentId : idIterable) {
            ContentId[] referrers;
            try {
                referrers =
                    context.getPolicyCMServer().getReferringContentIds(contentId,
                                                                       VersionedContentId.LATEST_COMMITTED_VERSION);

                for (ContentId referrer : referrers) {
                    allReferrers.add(referrer.getContentId());
                }
            } catch (CMException e) {
                System.err.println("Finding referrers of " + contentId.getContentIdString() + ": " + e);
            }

            if (++searched % 10 == 0) {
                System.out.println("Found referrers for " + searched + " objects...");
            }
        }

        for (ContentId contentId : idIterable) {
            allReferrers.remove(contentId);
        }

        int unfilteredSize = allReferrers.size();

        Iterator<ContentId> it = allReferrers.iterator();

        RejectionCollectingContentIdFilter rejectionCollectingFilter =
            new RejectionCollectingContentIdFilter(contentIdFilter);

        while (it.hasNext()) {
            if (!rejectionCollectingFilter.accept(it.next().getContentId())) {
                it.remove();
            }
        }

        System.out.println("Found " + unfilteredSize + " referring objects. " + allReferrers.size()
                           + " of these were retained.");

        if (allReferrers.size() != unfilteredSize) {
            System.out.println("Non-retained objects:");
            rejectionCollectingFilter.printCollectedObjects(context);
        }

        return new Iterable<ContentId>() {
            public Iterator<ContentId> iterator() {
                return new JoiningIterator<ContentId>(idIterable.iterator(), allReferrers.iterator());
            }
        };
    }

    @Override
    public String getHelp() {
        return "Exports content as XML";
    }

}
