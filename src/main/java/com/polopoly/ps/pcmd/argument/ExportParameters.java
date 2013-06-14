package com.polopoly.ps.pcmd.argument;

import java.io.File;
import java.util.Iterator;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.util.ContentIdFilter;
import com.polopoly.ps.pcmd.field.content.AbstractContentIdField;
import com.polopoly.ps.pcmd.parser.BooleanParser;
import com.polopoly.ps.pcmd.parser.ExistingDirectoryParser;
import com.polopoly.ps.pcmd.tool.export.ContentFileFormat;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.FetchingIterator;

public class ExportParameters extends ListExportableParameters implements Parameters {
    private static final String ALL_OPTION = "all";
    public static final String EXPORT_PRESENT_OPTION = "exportpresent";
    private static final String FILTER_REFERENCES = "filterreferences";
    private static final String INCLUDE_REFERRERS_OPTION = "includereferrers";
    private static final String TEXT_OPTION = "textformat";

    private File outputDirectory;
    private boolean exportPresent;
    private boolean idsAreArguments;
    private boolean filterReferences;
    private boolean includeReferrers;

    private ContentFileFormat format = ContentFileFormat.XML;

    private ContentIdListParameters idListParameters = new ContentIdListParameters() {
        @Override
        protected int getFirstContentIdIndex() {
            return 1;
        }
    };

    public boolean isExportPresent() {
        return exportPresent;
    }

    public void setExportPresent(boolean exportPresent) {
        this.exportPresent = exportPresent;
    }

    @Override
    public void getHelp(ParameterHelp help) {
        help.setArguments(new ExistingDirectoryParser(),
                          "The directory to write the exported files to (any existing files will be overwritten).");

        idListParameters.getHelp(help);

        super.getHelp(help);

        /*
         * help.addOption(ALL_OPTION, new BooleanParser(),
         * "Whether to export all exportable objects (if not specified, the objects to be exported will be expected on standard in or as arguments)"
         * );
         */

        help.addOption(EXPORT_PRESENT_OPTION,
                       new BooleanParser(),
                       "Whether to export objects even though they are part of the project content or the Polopoly installation (defaults to false).");

        help.addOption(FILTER_REFERENCES, new BooleanParser(),
                       "Whether to remove references to content that is neither project content nor part of the export (defaults to true).");

        /*
         * help.addOption(INCLUDE_REFERRERS_OPTION, new BooleanParser(),
         * "Whether to search for and include all content that has references to the specified content (will only retain content that is project content if "
         * + EXPORT_PRESENT_OPTION + " is true).");
         */
        help.addOption(TEXT_OPTION, new BooleanParser(), "Whether to export the content in text format.");
    }

    @Override
    public void parseParameters(Arguments args, PolopolyContext context) throws ArgumentException {
        outputDirectory = args.getArgument(0, new ExistingDirectoryParser());

        super.parseParameters(args, context);

        idsAreArguments = !args.getFlag(ALL_OPTION, false);

        if (idsAreArguments) {
            try {
                idListParameters.parseParameters(args, context);
            } catch (ArgumentException e) {
                throw new ArgumentException(e.getMessage() + " (specify " + ALL_OPTION
                                            + "=true to export all objects rather than a list of specific objects)");
            }
        }

        exportPresent = args.getFlag(EXPORT_PRESENT_OPTION, false);

        filterReferences = args.getFlag(FILTER_REFERENCES, true);

        if (args.getFlag(TEXT_OPTION, false)) {
            format = ContentFileFormat.TEXT;
        }

        setIncludeReferrers(args.getFlag(INCLUDE_REFERRERS_OPTION, false));
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public boolean isIdsArguments() {
        return idsAreArguments;
    }

    public Iterable<ContentId> getIdArgumentsIterator(final ContentIdFilter excludeFilter, final PolopolyContext context) {
        return new Iterable<ContentId>() {
            public Iterator<ContentId> iterator() {
                return new FetchingIterator<ContentId>() {
                    private Iterator<ContentId> delegate = idListParameters.iterator();

                    @Override
                    protected ContentId fetch() {
                        while (delegate.hasNext()) {
                            ContentId candidate = delegate.next();

                            if (!excludeFilter.accept(candidate)) {
                                return candidate;
                            } else {
                                System.err.println("Skipping " + AbstractContentIdField.get(candidate, context)
                                                   + " since it is part of project or product content.");
                            }
                        }

                        return null;
                    }
                };
            }
        };
    }

    public void setIncludeReferrers(boolean includeReferrers) {
        this.includeReferrers = includeReferrers;
    }

    public boolean isIncludeReferrers() {
        return includeReferrers;
    }

    public boolean isFilterReferences() {
        return filterReferences;
    }

    public ContentFileFormat getFormat() {
        return format;
    }
}
