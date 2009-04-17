package com.polopoly.pcmd.tool;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

import org.apache.lucene.search.Sort;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.search.index.search.RemoteSearchService;
import com.polopoly.cm.search.index.search.SearchResult;
import com.polopoly.management.ServiceNotAvailableException;
import com.polopoly.pcmd.field.content.AbstractContentIdField;

public class LuceneTool implements Tool<LuceneParameters> {
    public static final int DEFAULT_BATCH_SIZE = 100;

    public LuceneParameters createParameters() {
        return new LuceneParameters();
    }

    @SuppressWarnings("unchecked")
    public void execute(PolopolyContext context, LuceneParameters parameters) {
        try {
            RemoteSearchService searchService =
                context.getSearchClient().getRemoteSearchService(parameters.getIndex());

            int limit = parameters.getBatchSize();
            int at = 0;

            SearchResult result;

            do {
                result = searchService.search(parameters.getQuery(), null, Sort.INDEXORDER, limit, at);

                for (ContentId contentId : (List<ContentId>) result.getContentIds()) {
                    System.out.println(AbstractContentIdField.get(contentId, context));
                }

                at += limit;
            } while (result.getContentIds().size() == limit);
        } catch (ServiceNotAvailableException e) {
            throw new CMRuntimeException(e);
        } catch (RemoteException e) {
            throw new CMRuntimeException(e);
        } catch (IOException e) {
            throw new CMRuntimeException(e);
        }
    }

    public String getHelp() {
        return "Searches an indexserver index.";
    }
}
