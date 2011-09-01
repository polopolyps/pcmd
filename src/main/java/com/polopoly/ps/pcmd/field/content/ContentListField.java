package com.polopoly.ps.pcmd.field.content;

import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.collections.ContentList;
import com.polopoly.util.client.PolopolyContext;

public class ContentListField implements Field {
    private String contentListName;

    public ContentListField(String contentList) {
        this.contentListName = contentList;
    }

    public String get(ContentRead content, PolopolyContext context) {
        try {
            ContentList contentList;

            if (contentListName == null) {
                contentList = content.getContentList();
            }
            else {
                contentList = content.getContentList(contentListName);
            }

            int size = contentList.size();

            StringBuffer result = new StringBuffer(100);

            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    result.append(",");
                }

                result.append(AbstractContentIdField.get(contentList.getEntry(i).getReferredContentId(), context));
            }

            return result.toString();
        } catch (CMException e) {
            System.err.println(content.getContentId().getContentIdString() + ": " + e);

            return "";
        }
    }
}
