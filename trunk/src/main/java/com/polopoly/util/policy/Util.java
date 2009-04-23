package com.polopoly.util.policy;

import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.collections.ContentList;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.util.client.PolopolyContext;

public class Util {
    public static PolopolyContext util(PolicyCMServer server) {
        return new PolopolyContext(server);
    }
    public static PolicyUtil util(Policy policy) {
        return new PolicyUtil(policy);
    }

    public static ContentUtil util(ContentRead content, PolicyCMServer server) {
        return new ContentUtil(content, server);
    }

    /**
     * The use of {@link Util#util(ContentList, PolopolyContext)} is preferred.
     */
    public static ContentListUtil util(ContentList contentList, PolicyCMServer server) {
        return new ContentListUtil(contentList, null, server);
    }

    public static ContentUtil util(ContentRead content, PolopolyContext context) {
        return new ContentUtil(content, context.getPolicyCMServer());
    }

    public static ContentListUtil util(ContentList contentList, PolopolyContext context) {
        return new ContentListUtil(contentList, null, context);
    }
}
