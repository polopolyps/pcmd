package com.polopoly.pcmd.field.policy;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.app.policy.SingleValued;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.ContentListAware;
import com.polopoly.cm.collections.ContentList;
import com.polopoly.cm.policy.ContentPolicy;
import com.polopoly.cm.policy.Policy;
import com.polopoly.pcmd.field.content.AbstractContentIdField;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.ContentListIterator;

public class ValueField implements PolicyField {
    public String get(Policy policy, PolopolyContext context) {
        if (policy instanceof SingleValued) {
            try {
                return '"' + ((SingleValued) policy).getValue() + '"';
            } catch (CMException e) {
                System.err.println(e.toString());
                return "";
            }
        }
        // ContentPolicy implements ContentListAware but does not generally represent a content list.
        else if (policy instanceof ContentListAware && !(policy.getClass() == ContentPolicy.class)) {
            try {
                ContentList contentList = ((ContentListAware) policy).getContentList();

                StringBuffer result = new StringBuffer(100);

                result.append("[");

                boolean first = true;

                for (ContentId contentId : new ContentListIterator(contentList)) {
                    if (!first) {
                        result.append(", ");
                    }

                    result.append(AbstractContentIdField.get(contentId, context));

                    first = false;
                }

                result.append("]");

                return result.toString();
            } catch (CMException e) {
                System.err.println(e);

                return "";
            }
        }
        else {
            return "";
        }
    }
}
