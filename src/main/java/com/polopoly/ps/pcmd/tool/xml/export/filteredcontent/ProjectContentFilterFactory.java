package com.polopoly.ps.pcmd.tool.xml.export.filteredcontent;

import java.io.File;
import java.util.List;

import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.cm.util.ContentIdFilter;
import com.polopoly.ps.pcmd.client.Major;
import com.polopoly.ps.pcmd.util.Plural;
import com.polopoly.ps.pcmd.xml.present.PresentFileReader;

public class ProjectContentFilterFactory {
    private PolicyCMServer server;

    public ProjectContentFilterFactory(PolicyCMServer server) {
        this.server = server;
    }

    public ContentIdFilter getExistingObjectsFilter(List<File> projectContentDirectories) {
        PresentContentFilter projectContentFilter = createProjectContentFilter(server, projectContentDirectories);

        System.err.println(Plural.count(projectContentFilter.getPresentIds(), "object")
                           + " were product or project content.");

        return new OrContentIdFilter(new MajorFilter(Major.MAJOR_CONFIG),
            new SecurityRootDepartmentFilter(), new InputTemplateFilter(server), projectContentFilter);
    }

    protected PresentContentFilter createProjectContentFilter(PolicyCMServer server,
                                                              List<File> projectContentDirectories) {
        PresentContentFilter presentContentFilter = new PresentContentFilter(server);

        if (projectContentDirectories.isEmpty()) {
            PresentFileReader reader = new PresentFileReader(presentContentFilter);

            reader.readAndScanContent();
        } else {
            for (File projectContentDirectory : projectContentDirectories) {
                PresentFileReader reader = new PresentFileReader(projectContentDirectory, presentContentFilter);

                reader.readAndScanContent();
            }
        }

        return presentContentFilter;
    }
}
