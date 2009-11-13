package com.polopoly.util.policy;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.client.InputTemplate;
import com.polopoly.cm.collections.ContentListRead;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.content.ContentUtil;
import com.polopoly.util.content.ContentUtilImpl;
import com.polopoly.util.contentid.ContentIdUtil;
import com.polopoly.util.contentlist.ContentListUtil;
import com.polopoly.util.contentlist.ContentListUtilImpl;

public class Util {
    public static PolopolyContext util(PolicyCMServer server) {
        return new PolopolyContext(server);
    }
    public static PolicyUtil util(Policy policy) {
        return new PolicyUtilImpl(policy);
    }

    public static ContentUtil util(ContentRead content, PolicyCMServer server) {
        return new ContentUtilImpl(content, util(server));
    }

    public static ContentUtil util(ContentRead content, PolopolyContext context) {
        return new ContentUtilImpl(content, context);
    }

    /**
     * The use of {@link Util#util(ContentListRead, PolopolyContext)} is preferred.
     */
    public static ContentListUtil util(ContentListRead contentList, PolicyCMServer server) {
        return new ContentListUtilImpl(contentList, null, server);
    }

    public static ContentListUtil util(ContentListRead contentList, PolopolyContext context) {
        return new ContentListUtilImpl(contentList, null, context);
    }

    public static ContentIdUtil util(ContentId contentId, PolopolyContext context) {
        if (contentId == null) {
            return null;
        }

        return new ContentIdUtil(context, contentId);
    }
    public static InputTemplateUtil util(InputTemplate inputTemplate, PolopolyContext context) {
        return new InputTemplateUtilImpl(inputTemplate, context);
    }
}
