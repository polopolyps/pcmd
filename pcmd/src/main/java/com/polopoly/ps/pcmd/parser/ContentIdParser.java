package com.polopoly.ps.pcmd.parser;

import java.rmi.RemoteException;

import javax.ejb.FinderException;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ContentIdFactory;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.user.server.PrincipalId;
import com.polopoly.user.server.User;
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
                }

                if (result == null) {
                    // maybe a user?
                    try {
                        PrincipalId userId = getUser(context, string);

                        result = context.getPolicyCMServer().findContentIdByExternalId(new ExternalContentId(userId.getPrincipalIdString()));
                    } catch (ArgumentException ae) {
                        // nope, not a user.
                    }
                }

                if (result == null) {
                    throw new ParseException(this, string, "Expected a numerical content ID, a user name or an existing external ID");
                }

                return result;
            } catch (CMException f) {
                throw new CMRuntimeException("While looking up external ID \"" + string + "\": " + f, f);
            }
        }
    }

    public static PrincipalId getUser(PolopolyContext context, String userName) throws ArgumentException {
        try {
            User userObject = context.getUserServer().getUserByLoginName(userName);

            return userObject.getUserId();
        } catch (RemoteException e) {
            throw new ArgumentException("While fetching user \"" + userName + "\": " + e.getMessage());
        } catch (FinderException e) {
            throw new ArgumentException("Found no user with name \"" + userName + "\".");
        }
    }
}
