package com.polopoly.ps.pcmd.tool;

import java.text.DateFormat;
import java.util.Date;
import java.util.Set;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.search.db.Version;
import com.polopoly.cm.util.ContentIdFilter;
import com.polopoly.pcmd.tool.Tool;
import com.polopoly.ps.pcmd.argument.ListExportableParameters;
import com.polopoly.ps.pcmd.client.Major;
import com.polopoly.ps.pcmd.field.content.AbstractContentIdField;
import com.polopoly.ps.pcmd.tool.export.AcceptanceCollectingContentIdFilter;
import com.polopoly.ps.pcmd.tool.xml.export.filteredcontent.ExcludeMetadataVersionFilter;
import com.polopoly.ps.pcmd.tool.xml.export.filteredcontent.FilteredAllContentFinder;
import com.polopoly.ps.pcmd.tool.xml.export.filteredcontent.HotdeployStatusFilter;
import com.polopoly.ps.pcmd.tool.xml.export.filteredcontent.InputTemplateFilter;
import com.polopoly.ps.pcmd.tool.xml.export.filteredcontent.MajorFilter;
import com.polopoly.ps.pcmd.tool.xml.export.filteredcontent.NegatingContentIdFilter;
import com.polopoly.ps.pcmd.tool.xml.export.filteredcontent.OrContentIdFilter;
import com.polopoly.ps.pcmd.tool.xml.export.filteredcontent.ProjectContentFilterFactory;
import com.polopoly.ps.pcmd.tool.xml.export.filteredcontent.SecurityRootDepartmentFilter;
import com.polopoly.ps.pcmd.util.Plural;
import com.polopoly.util.client.PolopolyContext;

public class ListExportableTool implements Tool<ListExportableParameters> {
    public ListExportableParameters createParameters() {
        return new ListExportableParameters();
    }

    public void execute(PolopolyContext context, ListExportableParameters parameters) {
        System.err.println("Scanning project content...");

        ContentIdFilter existingObjectsFilter =
            new ProjectContentFilterFactory(context.getPolicyCMServer()).getExistingObjectsFilter(parameters
                .getProjectContentDirectories());

        int since = parameters.getSince();

        for (ContentId contentId : getIdsToExport(context, since, existingObjectsFilter)) {
            printResultContentId(contentId, parameters, context);
        }
    }

    private void printResultContentId(ContentId contentId, ListExportableParameters parameters, PolopolyContext context) {
        if (parameters.isResolve()) {
            System.out.println(AbstractContentIdField.get(contentId, context));
        } else {
            System.out.println(contentId.getContentIdString());
        }
    }

    public Iterable<ContentId> getIdsToExport(PolopolyContext context, int since, ContentIdFilter excludeFilter) {
        FilteredAllContentFinder finder = new FilteredAllContentFinder(context.getPolicyCMServer());

        if (since > 0) {
            System.out.println("Scanning content created since version " + since + " ("
                               + DateFormat.getDateTimeInstance().format(new Date(since)) + ")");

            finder.addSearchExpression(new Version(since, Version.GREATER_THAN_OR_EQ));
        }

        AcceptanceCollectingContentIdFilter collectingFilter = new AcceptanceCollectingContentIdFilter(excludeFilter);

        finder.addFilter(new ExcludeMetadataVersionFilter());
        finder.addFilter(new NegatingContentIdFilter(new OrContentIdFilter(new MajorFilter(Major.CONTENT),
            new MajorFilter(Major.MAJOR_CONFIG), new InputTemplateFilter(context.getPolicyCMServer()),
            new HotdeployStatusFilter(context.getPolicyCMServer()), new SecurityRootDepartmentFilter(),
            collectingFilter)));

        try {
            Set<ContentId> result = finder.findAllNonPresentContent();

            logNotExportedBecausePresent(context, collectingFilter);

            return result;
        } catch (CMException e) {
            System.err.println(e.toString());

            System.exit(1);
            return null;
        }
    }

    protected void logNotExportedBecausePresent(PolopolyContext context,
                                                AcceptanceCollectingContentIdFilter collectingFilter) {
        Set<ContentId> notExportedBecausePresent = collectingFilter.getCollectedIds();

        if (notExportedBecausePresent.isEmpty()) {
            return;
        }

        System.err.println(Plural.count(notExportedBecausePresent, "objects") + " were not included because they are "
                           + "part of the project data or Polopoly content.");
        collectingFilter.printCollectedObjects(context);
    }

    public String getHelp() {
        return "Lists all objects in the system that can be exported.";
    }

}
