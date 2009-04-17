package com.polopoly.pcmd.tool;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.search.db.ComponentValue;
import com.polopoly.cm.search.db.Major;
import com.polopoly.cm.search.db.OrderByContentId;
import com.polopoly.cm.search.db.ReferringTo;
import com.polopoly.cm.search.db.SearchExpression;
import com.polopoly.cm.search.db.Version;
import com.polopoly.cm.server.ServerNames;
import com.polopoly.pcmd.argument.ArgumentException;
import com.polopoly.pcmd.argument.Arguments;
import com.polopoly.pcmd.argument.NotProvidedException;
import com.polopoly.pcmd.argument.ParameterHelp;
import com.polopoly.pcmd.argument.Parameters;
import com.polopoly.pcmd.field.content.AbstractContentIdField;
import com.polopoly.pcmd.parser.ComponentParser;
import com.polopoly.pcmd.parser.ContentIdParser;
import com.polopoly.pcmd.parser.ContentRefParser;
import com.polopoly.pcmd.parser.IntegerParser;
import com.polopoly.pcmd.util.Component;
import com.polopoly.pcmd.util.ContentReference;

public class SearchTool implements Tool<SearchToolParameters> {
    static final int DEFAULT_BATCH_SIZE = 500;
    private static final String WILDCARD = "weirdUnlikelyString";

    public SearchToolParameters createParameters() {
        return new SearchToolParameters();
    }

    public void execute(PolopolyContext context, SearchToolParameters parameters) {
        SearchExpression searchExpr = null;

        ContentId inputTemplate = parameters.getInputTemplate();

        if (inputTemplate != null) {
            searchExpr = new ReferringTo(
                ServerNames.CONTENT_ATTRG_SYSTEM,
                ServerNames.CONTENT_ATTR_INPUT_TEMPLATEID,
                inputTemplate);
        }

        Component component = parameters.getComponent();

        if (component != null) {
            String componentValue = parameters.getComponentValue();

            if (component != null && componentValue != null) {
                searchExpr = add(searchExpr, new ComponentValue(component.getGroup(), component.getComponent(), componentValue));
            }
            else {
                searchExpr = add(searchExpr, new ComponentValue(component.getGroup(),
                        component.getComponent(), "thisIsAVeryUnlikelyValue4711", ComponentValue.NOT_EQUALS));
            }
        }

        ContentReference contentref = parameters.getContentRef();
        ContentId contentrefValue = parameters.getContentRefValue();

        if (contentref != null || contentrefValue != null) {
            String group = contentref.getGroup();

            if (group == null || group.equals("*")) {
                group = WILDCARD;
            }

            String name = contentref.getReference();

            if (name == null || name.equals("*")) {
                name = WILDCARD;
            }

            ReferringTo referringTo = new ReferringTo(group, name, contentrefValue);

            if (group == WILDCARD) {
                referringTo.setGroupOp(ReferringTo.NOT_EQUALS);
            }

            if (name == WILDCARD) {
                referringTo.setNameOp(ReferringTo.NOT_EQUALS);
            }

            searchExpr = add(searchExpr, referringTo);
        }

        searchExpr = add(searchExpr, new Version(VersionedContentId.LATEST_COMMITTED_VERSION));

        Integer major = parameters.getMajor();

        if (major != null) {
            searchExpr = add(searchExpr, new Major(major));
        }

        searchExpr = OrderByContentId.descending(searchExpr);

        System.err.println("Starting search using search expression...");

        int at = 0;
        int batchSize = parameters.getBatchSize();

        try {
            while (true) {
                VersionedContentId[] ids = context.getPolicyCMServer().findContentIdsBySearchExpression(searchExpr, batchSize, at);

                if (ids.length < batchSize && at == 0) {
                    System.err.println("Found " + (at + ids.length) + " result(s).");
                }

                if (at > 0 || ids.length == batchSize) {
                    System.err.println("Processing objects " + (at+1) + " to " + (at + ids.length) + ".");
                }

                for (int i = 0; i < ids.length; i++) {
                    System.out.println(AbstractContentIdField.get(ids[i].getContentId(), context));
                }

                if (ids.length < batchSize && at > 0) {
                    System.err.println("Found " + (at + ids.length) + " result(s).");
                }

                if (ids.length < batchSize) {
                    break;
                }

                at += ids.length;
            }
        } catch (CMException e) {
            throw new CMRuntimeException("While searching using " + searchExpr + ": " + e, e);
        }
    }

    private static SearchExpression add(SearchExpression a, SearchExpression b) {
        if (a != null) {
            return a.and(b);
        }
        else {
            return b;
        }
    }

    public String getHelp() {
        return "Uses a search expression to search for content.";
    }
}

class SearchToolParameters implements Parameters {
    private static final String MAJOR = "major";
    private static final String COMPONENT = "component";
    private static final String CONTENTREF = "contentref";
    private static final String CONTENTREFVALUE = "refersto";
    private static final String COMPONENTVALUE = "componentvalue";
    private static final String INPUTTEMPLATE = "inputtemplate";
    private static final String BATCH_SIZE = "batchsize";
    private ContentId inputTemplate;
    private Component component;
    private ContentReference contentRef;
    private ContentId contentRefValue;
    private String componentValue;
    private Integer major;
    private int batchSize = SearchTool.DEFAULT_BATCH_SIZE;

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
        try {
            setInputTemplate(args.getOption(INPUTTEMPLATE, new ContentIdParser(context)));
        } catch (NotProvidedException e) {
        }

        try {
            setComponent(args.getOption(COMPONENT, new ComponentParser()));
            setComponentValue(args.getOptionString(COMPONENTVALUE));
        } catch (NotProvidedException e) {
        }

        try {
            setContentRefValue(args.getOption(CONTENTREFVALUE, new ContentIdParser()));
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
    }

    public void getHelp(ParameterHelp help) {
        help.addOption(MAJOR, new IntegerParser(), "Restrict search to objects with this major.");
        help.addOption(COMPONENT, new IntegerParser(), "Restrict search to objects with this component set (specify component value for a specific value).");
        help.addOption(COMPONENTVALUE, null, "Restrict search to objects with [component] set to this value.");
        help.addOption(CONTENTREF, new ContentRefParser(), "Restrict search to objects with this content reference set (use * as wildcard).");
        help.addOption(CONTENTREFVALUE, new ContentIdParser(), "Restrict search to [contentref] pointing to this object.");
        help.addOption(INPUTTEMPLATE, new ContentIdParser(), "Restrict search to objects with this input template.");
        help.addOption(BATCH_SIZE, new IntegerParser(), "The number of content IDs to request in one batch while searching. Defaults to " + SearchTool.DEFAULT_BATCH_SIZE + ".");
    }
}
