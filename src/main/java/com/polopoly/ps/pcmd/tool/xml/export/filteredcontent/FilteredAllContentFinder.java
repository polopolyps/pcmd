package com.polopoly.ps.pcmd.tool.xml.export.filteredcontent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.cm.search.db.SearchExpression;
import com.polopoly.cm.search.db.Version;
import com.polopoly.cm.util.ContentIdFilter;
import com.polopoly.ps.pcmd.util.Plural;

public class FilteredAllContentFinder {
    private static final int LIMIT = 500;

    private List<ContentIdFilter> filters = new ArrayList<ContentIdFilter>();
    private List<SearchExpression> searchExpressions = new ArrayList<SearchExpression>();

    private PolicyCMServer policyCMServer;

    public FilteredAllContentFinder(PolicyCMServer policyCMServer) {
        this.policyCMServer = policyCMServer;
    }

    public void addFilter(ContentIdFilter filter) {
        filters.add(filter);
    }

    public void addSearchExpression(SearchExpression searchExpression) {
        searchExpressions.add(searchExpression);
    }

    public Set<ContentId> findAllNonPresentContent() throws CMException {
        Set<ContentId> result = new HashSet<ContentId>(200);

        SearchExpression searchExpression = new Version(VersionedContentId.LATEST_COMMITTED_VERSION);

        for (SearchExpression addExpression : searchExpressions) {
            searchExpression = searchExpression.and(addExpression);
        }

        int offset = 0;
        int scanned = 0;

        VersionedContentId[] ids = null;

        System.err.println("Looking for content...");

        do {
            ids = policyCMServer.findContentIdsBySearchExpression(searchExpression, LIMIT, offset);
            offset += ids.length;

            for (VersionedContentId versionedContentId : ids) {
                if (accept(versionedContentId)) {
                    result.add(versionedContentId.getContentId());
                }

                if (++scanned % 100 == 0) {
                    printStatus(scanned, result);
                }
            }
        } while (ids.length == LIMIT);

        printStatus(scanned, result);

        return result;
    }

    private boolean accept(VersionedContentId contentId) {
        for (ContentIdFilter filter : filters) {
            if (!filter.accept(contentId)) {
                return false;
            }
        }

        return true;
    }

    private void printStatus(int scanned, Set<ContentId> result) {
        System.err.println("Scanned " + Plural.count(scanned, "object") + ". Found " + Plural.count(result, "object")
                           + " matching criteria...");
    }
}
