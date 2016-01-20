package com.polopoly.ps.pcmd.tool;

import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.management.ObjectName;

import org.jolokia.client.J4pClient;
import org.jolokia.client.request.J4pExecRequest;
import org.jolokia.client.request.J4pExecResponse;
import org.jolokia.client.request.J4pSearchRequest;
import org.jolokia.client.request.J4pSearchResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.polopoly.cm.ContentId;
import com.polopoly.pcmd.tool.Tool;
import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.util.StringUtil;
import com.polopoly.util.client.PolopolyContext;

public class ReindexTool implements Tool<ReindexParameters> {

    private String serviceUrl;
    private int maxContentIds = 10;
    private J4pClient client;

    @Override
    public void execute(final PolopolyContext context, final ReindexParameters parameters) throws FatalToolException {

        try {
            serviceUrl = parameters.getServiceUrl();
            maxContentIds = parameters.getMaxContentIds();

            initFromServiceUrl();

            if (parameters.isReindexAll()) {
                doReindexAll(parameters.getIndexName());
            } else {
                doReindex(parameters.getIndexName(), parameters.getContentIds());
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new FatalToolException(e);
        }
    }

    private void doReindexAll(final String indexName) {
        try {

            final List<String> cores = filterCores(indexName);

            for (final String core : cores) {
                invokeOperation(core, "reindex()");
                System.out.println("Reindex of \'" + getSolrCoreName(core) + "\' started successfully");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new FatalToolException("Unable to reach Solr Indexer at " + serviceUrl, e);
        }

    }

    private void doReindex(final String indexName, final Iterator<ContentId> contentIds) {
        try {

            final List<String> cores = filterCores(indexName);

            final List<ContentId> ids = Lists.newArrayList();

            while (contentIds.hasNext()) {

                ids.add(contentIds.next());

                if (ids.size() >= maxContentIds) {
                    indexContentIds(cores, ids);
                    ids.clear();
                }

            }

            if (ids.size() > 0) {
                indexContentIds(cores, ids);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new FatalToolException("Unable to reach Solr Indexer at " + serviceUrl, e);
        }
    }

    private List<String> filterCores(final String indexName) {
        final List<String> cores = Lists.newArrayList();
        if (StringUtil.equals(indexName, "all")) {
            cores.addAll(getAvailableIndexes());
        } else {
            for (final String core : getAvailableIndexes()) {
                if (getSolrCoreName(core).equals(indexName)) {
                    cores.add(core);
                }
            }
        }
        return cores;
    }

    private void indexContentIds(final List<String> cores, final List<ContentId> ids) throws Exception {
        for (final String core : cores) {

            final String args = Joiner.on(',').skipNulls().join(
                    Iterables.transform(ids, new Function<ContentId, String>() {

                        @Nullable
                        @Override
                        public String apply(@Nullable final ContentId contentId) {
                            if (contentId != null) {
                                return contentId.getMajor() + "." + contentId.getMinor();
                            }
                            return null;
                        }
                    })
            );

            invokeOperation(core, "reindex(java.lang.String)", args);

            System.out.println("Reindex of \'" + getSolrCoreName(core) + "\' started successfully for " + ids.size() + " contents");
        }
    }

    private void invokeOperation(final String mbeanName, final String operation, final String...args) throws Exception {
        J4pExecRequest request = new J4pExecRequest(new ObjectName(mbeanName), operation, args);
        J4pExecResponse response = client.execute(request);
        if (response == null) {
            throw new FatalToolException("Unable to reach Solr Indexer at " + this.serviceUrl);
        } else {
            final JSONObject object = response.asJSONObject();
            final Object errorValue = object.get("error");
            if (errorValue != null) {
                throw new FatalToolException(errorValue.toString());
            }
        }
    }

    private List<String> getAvailableIndexes() {
        try {
            final List<String> indexes = Lists.newArrayList();
            J4pSearchRequest request = new J4pSearchRequest("com.polopoly:application=solr-indexer,group=solrIndexes,*");
            J4pSearchResponse response = client.execute(request);
            if (response != null) {
                final JSONObject object = response.asJSONObject();
                final Object value = object.get("value");
                if (value instanceof JSONArray) {
                    final JSONArray array = (JSONArray) value;
                    for (int idx = 0; idx < array.size(); idx++) {
                        final String item = array.get(idx).toString();
                        indexes.add(item);
                    }
                }

            }
            return indexes;
        } catch (Exception e) {
            e.printStackTrace();
            throw new FatalToolException("Unable to reach Solr Indexer at " + serviceUrl, e);
        }
    }

    private String getSolrCoreName(final String mbeanAddress) {
        String core = "";
        final Pattern namePattern = Pattern.compile(".*,name=([^,]*).*");
        final Matcher m = namePattern.matcher(mbeanAddress + ",attr=asdasd");
        if(m.matches()) {
            core = m.group(1);
        }

        return core;
    }

    private void initFromServiceUrl() throws MalformedURLException {
        final String url = serviceUrl + "/jolokia";
        client = new J4pClient(url);
    }

    @Override
    public ReindexParameters createParameters() {
        return new ReindexParameters();
    }

    @Override
    public String getHelp() {
        return "Use to reindex all the contents or to reindex only some contentIds.";
    }

}
