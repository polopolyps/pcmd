package com.polopoly.ps.pcmd.tool;

import com.polopoly.cm.ContentId;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.Arguments;
import com.polopoly.ps.pcmd.argument.NotProvidedException;
import com.polopoly.ps.pcmd.argument.ParameterHelp;
import com.polopoly.ps.pcmd.argument.Parameters;
import com.polopoly.ps.pcmd.parser.BooleanParser;
import com.polopoly.ps.pcmd.parser.ComponentParser;
import com.polopoly.ps.pcmd.parser.ContentIdParser;
import com.polopoly.ps.pcmd.parser.ContentRefParser;
import com.polopoly.ps.pcmd.parser.IntegerParser;
import com.polopoly.ps.pcmd.util.Component;
import com.polopoly.ps.pcmd.util.ContentReference;
import com.polopoly.util.client.PolopolyContext;

public class SearchParameters implements Parameters {
    private static final String MAJOR = "major";

    private static final String COMPONENT = "component";

    private static final String CONTENTREF = "contentref";

    private static final String CONTENTREFVALUE = "refersto";

    private static final String COMPONENTVALUE = "componentvalue";

    private static final String INPUTTEMPLATE = "inputtemplate";

    private static final String BATCH_SIZE = "batchsize";

    private static final String SINCE = "sinceversion";

    private static final String UNTIL = "untilversion";

    private static final String RESOLVE_EXTERNAL_ID = "resolveid";

    private ContentId inputTemplate;

    private Component component;

    private ContentReference contentRef;

    private ContentId contentRefValue;

    private String componentValue;

    private Integer major;

    private int batchSize = SearchTool.DEFAULT_BATCH_SIZE;

    private Integer sinceVersion;

    private Integer untilVersion;

    private boolean resolveExternalId = true;

    public void setInputTemplate(ContentId inputTemplate) {
        this.inputTemplate = inputTemplate;
    }

    public ContentId getInputTemplate() {
        return inputTemplate;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponentValue(String componentValue) {
        this.componentValue = componentValue;
    }

    public String getComponentValue() {
        return componentValue;
    }

    public void setMajor(Integer major) {
        this.major = major;
    }

    public Integer getMajor() {
        return major;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public void setContentRef(ContentReference contentRef) {
        this.contentRef = contentRef;
    }

    public ContentReference getContentRef() {
        return contentRef;
    }

    public void setContentRefValue(ContentId contentRefValue) {
        this.contentRefValue = contentRefValue;
    }

    public ContentId getContentRefValue() {
        return contentRefValue;
    }

    public void parseParameters(Arguments args, PolopolyContext context)
            throws ArgumentException {
        setResolveExternalId(args.getFlag(RESOLVE_EXTERNAL_ID, true));

        try {
            setInputTemplate(args.getOption(INPUTTEMPLATE, new ContentIdParser(
                    context)));
        } catch (NotProvidedException e) {
        }

        try {
            setComponent(args.getOption(COMPONENT, new ComponentParser()));
            setComponentValue(args.getOptionString(COMPONENTVALUE));
        } catch (NotProvidedException e) {
        }

        try {
            setContentRefValue(args.getOption(CONTENTREFVALUE,
                    new ContentIdParser(context)));
        } catch (NotProvidedException e) {
        }

        try {
            setContentRef(args.getOption(CONTENTREF, new ContentRefParser()));
        } catch (NotProvidedException e) {
        }

        try {
            setMajor(args.getOption(MAJOR, new IntegerParser()));
        } catch (NotProvidedException e) {
        }

        try {
            setBatchSize(args.getOption(BATCH_SIZE, new IntegerParser()));
        } catch (NotProvidedException e) {
        }

        try {
            setSinceVersion(args.getOption(SINCE, new IntegerParser()));
        } catch (NotProvidedException e) {
        }

        try {
            setUntilVersion(args.getOption(UNTIL, new IntegerParser()));
        } catch (NotProvidedException e) {
        }
    }

    public void getHelp(ParameterHelp help) {
        help.addOption(MAJOR, new IntegerParser(),
                "Restrict search to objects with this major.");
        help
                .addOption(
                        COMPONENT,
                        new IntegerParser(),
                        "Restrict search to objects with this component set (specify component value for a specific value).");
        help
                .addOption(COMPONENTVALUE, null,
                        "Restrict search to objects with [component] set to this value.");
        help
                .addOption(
                        CONTENTREF,
                        new ContentRefParser(),
                        "Restrict search to objects with this content reference set (use * as wildcard).");
        help.addOption(CONTENTREFVALUE, new ContentIdParser(),
                "Restrict search to [contentref] pointing to this object.");
        help.addOption(INPUTTEMPLATE, new ContentIdParser(),
                "Restrict search to objects with this input template.");
        help
                .addOption(
                        BATCH_SIZE,
                        new IntegerParser(),
                        "The number of content IDs to request in one batch while searching. Defaults to "
                                + SearchTool.DEFAULT_BATCH_SIZE + ".");
        help
                .addOption(
                        SINCE,
                        new IntegerParser(),
                        "Search object whose last committed version is greater than or equal to this version");
        help
                .addOption(
                        UNTIL,
                        new IntegerParser(),
                        "Search object whose last committed version is less than or equal to this version");
        help
                .addOption(
                        RESOLVE_EXTERNAL_ID,
                        new BooleanParser(),
                        "Whether to print external IDs rather than numerical IDs if available (reduces performance; defaults to true).");
    }

    public void setSinceVersion(Integer sinceVersion) {
        this.sinceVersion = sinceVersion;
    }

    public Integer getSinceVersion() {
        return sinceVersion;
    }

    public void setUntilVersion(Integer untilVersion) {
        this.untilVersion = untilVersion;
    }

    public Integer getUntilVersion() {
        return untilVersion;
    }

    public boolean isResolveExternalId() {
        return resolveExternalId;
    }

    public void setResolveExternalId(boolean resolveExternalId) {
        this.resolveExternalId = resolveExternalId;
    }

}
