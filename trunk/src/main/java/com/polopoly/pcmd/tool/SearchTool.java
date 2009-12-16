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
import com.polopoly.pcmd.field.content.AbstractContentIdField;
import com.polopoly.pcmd.util.Component;
import com.polopoly.pcmd.util.ContentReference;
import com.polopoly.util.client.PolopolyContext;

public class SearchTool implements Tool<SearchParameters> {
    static final int DEFAULT_BATCH_SIZE = 500;
    private static final String WILDCARD = "weirdUnlikelyString";

    public SearchParameters createParameters() {
        return new SearchParameters();
    }

    public void execute(PolopolyContext context, SearchParameters parameters) {
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
            String group = (contentref == null ? null : contentref.getGroup());

            if (group == null || group.equals("*")) {
                group = WILDCARD;
            }

            String name = (contentref == null ? null : contentref.getReference());

            if (name == null || name.equals("*")) {
                name = WILDCARD;
            }

            ReferringTo referringTo;

            if (group == WILDCARD && name == WILDCARD) {
                referringTo = new ReferringTo(contentrefValue);
            }
            else {
                referringTo = new ReferringTo(group, name, contentrefValue);

                if (group == WILDCARD) {
                    referringTo.setGroupOp(ReferringTo.NOT_EQUALS);
                }

                if (name == WILDCARD) {
                    referringTo.setNameOp(ReferringTo.NOT_EQUALS);
                }
            }

            searchExpr = add(searchExpr, referringTo);
        }

        searchExpr = add(searchExpr, new Version(VersionedContentId.LATEST_COMMITTED_VERSION));

        Integer major = parameters.getMajor();

        if (major != null) {
            searchExpr = add(searchExpr, new Major(major));
        }

        Integer sinceVersion = parameters.getSinceVersion();

        if (sinceVersion != null) {
            searchExpr = add(searchExpr, new Version(sinceVersion, Version.GREATER_THAN_OR_EQ));
        }

        Integer untilVersion = parameters.getUntilVersion();

        if (untilVersion != null) {
            searchExpr = add(searchExpr, new Version(untilVersion, Version.LESS_THAN_OR_EQ));
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

