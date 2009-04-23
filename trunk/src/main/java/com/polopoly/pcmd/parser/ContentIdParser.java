package com.polopoly.pcmd.parser;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ContentIdFactory;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.util.client.PolopolyContext;

public class ContentIdParser implements Parser<ContentId> {
    private PolopolyContext context;

    public ContentIdParser(PolopolyContext context) {
        this.context = context;
    }

    public ContentIdParser() {
    }

    public String getHelp() {
        return "<major>.<minor>[.<version>] / <external ID>";
    }

    public ContentId parse(String string) throws ParseException {
        try {
            return ContentIdFactory.createContentId(string.trim());
        } catch (IllegalArgumentException e) {
            if (context == null) {
                throw new ParseException(this, string, "Expected a numerical content ID.");
            }

            try {
                // don't trim anything. an external ID can end or begin with a space.
                VersionedContentId result =
                    context.getPolicyCMServer().findContentIdByExternalId(new ExternalContentId(string));

                if (result == null) {
                    // maybe a versioned external ID? Note that this may be ambiguous if the external ID itself contains a dot
                    int i = string.lastIndexOf('.');

                    if (i != -1) {
                        String versionString = string.substring(i+1);
                        String externalId = string.substring(0, i);

                        try {
                            int version = Integer.parseInt(versionString);

                            result =
                                context.getPolicyCMServer().translateSymbolicContentId(
                                        new ExternalContentId(new ExternalContentId(externalId), version));
                        } catch (NumberFormatException e2) {
                            // no, not a versioned external ID
                        }
                    }

                    if (result == null) {
                        throw new ParseException(this, string, "Expected a numerical content ID or an existing external ID");
                    }
                }

                return result;
            } catch (CMException f) {
                throw new CMRuntimeException("While looking up external ID \"" + string + "\": " + f, f);
            }
        }
    }

}
