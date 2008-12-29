package com.polopoly.pcmd.tool;

import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.cm.search.index.RmiSearchClient;
import com.polopoly.user.server.UserServer;

public class PolopolyContext {
    private CmClient client;
    private RmiSearchClient searchClient;

    public PolopolyContext(CmClient cmClient, RmiSearchClient searchClient) {
        this.client = cmClient;
        this.searchClient = searchClient;
    }

    public PolicyCMServer getPolicyCMServer() {
        return client.getPolicyCMServer();
    }

    public UserServer getUserServer() {
        return client.getUserServer();
    }

    public RmiSearchClient getSearchClient() {
        return searchClient;
    }
}
