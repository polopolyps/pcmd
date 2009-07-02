package com.polopoly.pcmd.tool;

import java.util.Iterator;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.VersionInfo;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.client.impl.exceptions.EJBFinderException;
import com.polopoly.pcmd.field.content.AbstractContentIdField;
import com.polopoly.util.client.PolopolyContext;

public class VersionsTool implements Tool<VersionsParameters> {

    public VersionsParameters createParameters() {
        return new VersionsParameters();
    }

    public void execute(PolopolyContext context,
            VersionsParameters parameters) {
        Iterator<ContentId> it = parameters.getContentIds();

        while (it.hasNext()) {
            ContentId contentId = it.next();

            try {
                ContentRead content =
                    context.getPolicyCMServer().getContent(new VersionedContentId(contentId, VersionedContentId.LATEST_VERSION));

                VersionedContentId latest = null;
                VersionedContentId latestCommitted = null;
                VersionedContentId defaultStage = null;

                if (parameters.isPrintSymbolicVersions()) {
                    latest = content.getContentId();
                    latestCommitted = context.getPolicyCMServer().translateSymbolicContentId(
                            new VersionedContentId(contentId, VersionedContentId.LATEST_COMMITTED_VERSION));
                    defaultStage = context.getPolicyCMServer().translateSymbolicContentId(
                            new VersionedContentId(contentId, VersionedContentId.DEFAULT_STAGE_VERSION));
                }

                VersionInfo[] versions = context.getPolicyCMServer().getContentHistory(contentId).getVersionInfos();

                for (int i = versions.length-1; i >= 0; i--) {
                    VersionInfo versionInfo = versions[i];
                    VersionedContentId versionedId = new VersionedContentId(contentId, versionInfo.getVersion());

                    try {
                        content = context.getPolicyCMServer().getContent(versionedId);

                        printVersion(content, latest, latestCommitted, defaultStage);
                    } catch (EJBFinderException e) {
                        System.err.println("The version " + versionInfo.getVersion() + " did not exist.");
                    }
                }
            } catch (CMException e) {
                if (parameters.isStopOnException()) {
                    throw new CMRuntimeException(contentId.getContentIdString(), e);
                }
                else {
                    System.err.println(AbstractContentIdField.get(contentId, context) + ": " + e);
                }
            }
        }
    }

    private void printVersion(ContentRead content,
            VersionedContentId latest, VersionedContentId latestCommitted,
            VersionedContentId defaultStage) {
        StringBuffer line = new StringBuffer(40);

        line.append(content.getContentId().getContentIdString());

        if (content.getContentId().equals(latest)) {
            line.append(" LATEST");
        }

        if (content.getContentId().equals(latestCommitted)) {
            line.append(" LATEST_COMMITTED");
        }

        if (content.getContentId().equals(defaultStage)) {
            line.append(" DEFAULT_STAGE");
        }

        System.out.println(line);
    }

    public String getHelp() {
        return "Lists the versions of a content object.";
    }
}
