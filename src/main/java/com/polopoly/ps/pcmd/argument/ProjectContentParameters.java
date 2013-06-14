package com.polopoly.ps.pcmd.argument;

import java.io.File;
import java.util.Collections;
import java.util.List;

import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.Arguments;
import com.polopoly.ps.pcmd.argument.NotProvidedException;
import com.polopoly.ps.pcmd.argument.ParameterHelp;
import com.polopoly.ps.pcmd.argument.Parameters;
import com.polopoly.ps.pcmd.parser.BooleanParser;
import com.polopoly.ps.pcmd.parser.ExistingDirectoryParser;
import com.polopoly.util.client.PolopolyContext;

public class ProjectContentParameters implements Parameters {
    private static final String PROJECT_CONTENT_DIR_OPTION = "projectcontent";
    private static final String RESOLVE_IDS_OPTION = "resolve";
    private List<File> projectContentDirectories;
    private boolean resolve;

    public List<File> getProjectContentDirectories() {
        return projectContentDirectories;
    }

    public void getHelp(ParameterHelp help) {
        help.addOption(PROJECT_CONTENT_DIR_OPTION, new ExistingDirectoryParser(),
            "A directory containing content already imported that has already been imported and should thus not be exported. This option may be specified multiple times.");
        help.addOption(RESOLVE_IDS_OPTION, new BooleanParser(), "Whether to resolve the IDs to external IDs (true by default; this slows down printing results).");
    }

    public void parseParameters(Arguments args, PolopolyContext context)
            throws ArgumentException {
        try {
            projectContentDirectories =
                args.getOptions(PROJECT_CONTENT_DIR_OPTION, new ExistingDirectoryParser());
        }
        catch (NotProvidedException e) {
            projectContentDirectories = Collections.emptyList();
        }

        setResolve(args.getFlag(RESOLVE_IDS_OPTION, true));
    }

    public void setResolve(boolean resolve) {
        this.resolve = resolve;
    }

    public boolean isResolve() {
        return resolve;
    }
}
