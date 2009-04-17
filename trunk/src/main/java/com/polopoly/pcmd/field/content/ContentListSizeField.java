package com.polopoly.pcmd.field.content;

import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.collections.ContentList;
import com.polopoly.pcmd.tool.PolopolyContext;

public class ContentListSizeField implements Field {
    private String contentListName;

    public ContentListSizeField(String contentList) {
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

            return Integer.toString(contentList.size());
        } catch (CMException e) {
            System.err.println(content.getContentId().getContentIdString() + ": " + e);

            return "";
        }
    }
}
