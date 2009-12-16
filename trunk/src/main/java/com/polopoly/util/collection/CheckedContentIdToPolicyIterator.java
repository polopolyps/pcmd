package com.polopoly.util.collection;

import static com.polopoly.util.policy.Util.util;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.exception.PolicyGetException;

public class CheckedContentIdToPolicyIterator<T> extends AbstractContentIdIterator<T> {
    private Class<T> klass;

    private static final Logger logger =
        Logger.getLogger(CheckedContentIdToPolicyIterator.class.getName());

    public CheckedContentIdToPolicyIterator(PolopolyContext context, Iterator<? extends ContentId> contentIds,
            Class<T> klass, boolean stopOnException) {
        super(context, contentIds, stopOnException);

        this.klass = klass;
    }

    public CheckedContentIdToPolicyIterator(PolicyCMServer server, Iterator<ContentId> contentIds, Class<T> klass) {
        super(server, contentIds);

        this.klass = klass;
    }

    @Override
    protected T fetch() {
        while (it.hasNext()) {
            ContentId contentId = it.next();

            T result;

            try {
                result = util(server).getPolicy(contentId, klass);
            } catch (PolicyGetException defaultVersionException) {
                try {
                    // maybe a not yet committed version?
                    result = util(server).getPolicy(
                        new VersionedContentId(contentId, VersionedContentId.LATEST_VERSION), klass);
                } catch (PolicyGetException latestException) {
                    if (stopOnException) {
                        throw new CMRuntimeException(defaultVersionException);
                    }
                    else {
                        logger.log(Level.WARNING, "While fetching " + contentId.getContentIdString() + ": " + defaultVersionException.toString());
                        continue;
                    }
                }
            }

            count++;

            return result;
        }

        return null;
    }
}
