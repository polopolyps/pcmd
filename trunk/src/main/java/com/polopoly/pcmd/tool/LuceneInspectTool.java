package com.polopoly.pcmd.tool;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Enumeration;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;

import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.search.index.search.RemoteSearchService;
import com.polopoly.cm.search.index.search.SearchHit;
import com.polopoly.cm.search.index.search.SearchResult;
import com.polopoly.management.ServiceNotAvailableException;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.ContentIdToContentIterator;

public class LuceneInspectTool implements Tool<LuceneInspectParameters> {
    public LuceneInspectParameters createParameters() {
        return new LuceneInspectParameters();
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
    public void execute(PolopolyContext context, LuceneInspectParameters parameters) {
        try {
            RemoteSearchService searchService =
                context.getSearchClient().getRemoteSearchService(parameters.getIndex());

            ContentIdToContentIterator it =
                new ContentIdToContentIterator(context,
                    parameters.getContentIds(), parameters.isStopOnException());

            StringBuffer line = new StringBuffer(100);

            boolean first = true;

            while (it.hasNext()) {
                line.setLength(0);

                ContentRead content = it.next();
                String contentIdString = content.getContentId().getContentId().getContentIdString();

                Query query = new TermQuery(new Term("contentid", contentIdString));

                BooleanQuery bool = new BooleanQuery();
                bool.add(new BooleanClause(query, BooleanClause.Occur.MUST));
                query = bool;

                SearchResult result;

                result = searchService.search(query, null, Sort.INDEXORDER, 10, 0);

                if (result.getContentIds().size() != 1) {
                    System.err.println("There were " + result.getContentIds().size() + " results when searching for " + contentIdString + ".");
                }

                for (SearchHit hit : result.getSearchHits()) {
                    if (!first) {
                        System.out.println();
                    }
                    else {
                        first = false;
                    }

                    Document document = hit.getDocument();
                    Enumeration enumeration = document.fields();

                    while (enumeration.hasMoreElements()) {
                        Field field = (Field) enumeration.nextElement();

                        String name = field.name();
                        String[] values = document.getValues(name);

                        for (String value : values) {
                            System.out.println(name + ":" + value);
                        }
                    }
                }
            }
        } catch (ServiceNotAvailableException e) {
            throw new CMRuntimeException(e);
        } catch (RemoteException e) {
            throw new CMRuntimeException(e);
        } catch (IOException e) {
            throw new CMRuntimeException(e);
        }
    }

    public String getHelp() {
        return "Displays the fields in the Lucene document for the specified content objects.";
    }
}
