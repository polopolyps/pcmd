package com.polopoly.pcmd.field.content;

import java.rmi.RemoteException;

import javax.ejb.CreateException;

import com.polopoly.cm.LockInfo;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.pcmd.tool.PolopolyContext;
import com.polopoly.user.server.Caller;
import com.polopoly.user.server.User;

public class LockerField implements Field {

    public String get(ContentRead content, PolopolyContext context) {
        LockInfo lockInfo = content.getLockInfo();

        if (lockInfo == null) {
            return "";
        }
        else {
            Caller locker = lockInfo.getLocker();

            if (locker != null) {
                try {
                    User user = context.getUserServer().getUserByUserId(locker.getUserId());

                    return user.getLoginName();
                } catch (RemoteException e) {
                    System.err.println(locker.getUserId().getPrincipalIdString() + ": " + e);
                } catch (CreateException e) {
                    System.err.println(locker.getUserId().getPrincipalIdString() + ": " + e);
                }
            }

            return "n/a";
        }
    }
}
