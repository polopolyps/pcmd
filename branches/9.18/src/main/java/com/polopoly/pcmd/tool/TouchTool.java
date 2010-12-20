package com.polopoly.pcmd.tool;

import java.util.Iterator;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.policy.Policy;
import com.polopoly.pcmd.field.content.AbstractContentIdField;
import com.polopoly.util.client.PolopolyContext;

public class TouchTool implements Tool<TouchParameters> {

    public TouchParameters createParameters() {
        return new TouchParameters();
    }

    public void execute(PolopolyContext context, TouchParameters parameters) {
        Iterator<ContentId> it = parameters.getContentIds();

        while (it.hasNext()) {
            ContentId contentId = it.next();

            try {
                VersionedContentId versionedId;

                if (contentId instanceof VersionedContentId
                    && ((VersionedContentId) contentId).getVersion() != VersionedContentId.UNDEFINED_VERSION) {
                    versionedId = (VersionedContentId) contentId;
                } else {
                    versionedId = new VersionedContentId(contentId, VersionedContentId.LATEST_COMMITTED_VERSION);
                }

                // first retrieve the old policy so we are sure we can actually
                // load it (i.e. that we have the policy class on the class
                // path).
                Policy  policy = context.getPolicyCMServer().getPolicy(versionedId);

                if (!parameters.isDryRun()) {
                    policy = context.getPolicyCMServer().createContentVersion(versionedId);

                    policy.getContent().commit();
                }
                if (!parameters.isQuiet()) {
                    System.out.println(AbstractContentIdField.get(policy.getContentId(), context));
                }
            } catch (CMException e) {
                String errorString = "While touching " + contentId.getContentIdString() + ": " + e;

                if (parameters.isStopOnException()) {
                    throw new CMRuntimeException(errorString, e);
                } else {
                    System.err.println(errorString);
                }
            }
        }
    }

    public String getHelp() {
        return "Creates a new version of the specified objects and commits it (this requires the policy on the class path).";
    }
}
