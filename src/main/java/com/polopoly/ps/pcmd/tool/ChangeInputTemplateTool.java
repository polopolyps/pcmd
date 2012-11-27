package com.polopoly.ps.pcmd.tool;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.*;
import com.polopoly.pcmd.tool.Tool;
import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.ps.pcmd.argument.ContentIdListParameters;
import com.polopoly.util.client.PolopolyContext;

import java.util.Iterator;

/**
 * @author gmola
 *         date: 11/26/12
 */
public class ChangeInputTemplateTool implements Tool<ChangeInputTemplateParameters> {

    @Override
    public void execute(PolopolyContext context, ChangeInputTemplateParameters parameters) throws FatalToolException {
        Iterator<ContentId> contentIdIterator = parameters.getContentIds();
        while(contentIdIterator.hasNext()) {
            ContentId contentId = contentIdIterator.next();
            try {
                CMServer cmServer = context.getCMServer();
                ContentRead originalContent = cmServer.getContent(contentId);

                VersionedContentId newContentId = Creator.createContentVersion(cmServer, originalContent.getContentId());

                Content content = (Content) cmServer.getContent(newContentId);

                cmServer.lock(content.getContentId().getContentId(), content.getContentId().getVersion(), 1000);
                content.setInputTemplateId(parameters.getInputTemplate());

                cmServer.commitContents(new VersionedContentId[]{newContentId});
                cmServer.unlock(content.getContentId());
                
            } catch (CMException e) {
               throw new FatalToolException(e);
            }
        }
    }

    @Override
    public ChangeInputTemplateParameters createParameters() {
        return new ChangeInputTemplateParameters();
    }

    @Override
    public String getHelp() {
        return "pcmd search .... | pcmd changeInputTemplate \"new.input.template\"";
    }
}
