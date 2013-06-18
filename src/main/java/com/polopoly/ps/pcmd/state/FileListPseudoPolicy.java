package com.polopoly.ps.pcmd.state;

import java.io.File;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.Policy;
import com.polopoly.community.component.IntegerComponent;
import com.polopoly.ps.pcmd.file.DeploymentFile;
import com.polopoly.ps.pcmd.file.FileDeploymentFile;
import com.polopoly.ps.pcmd.util.FetchingIterator;

class FileListPseudoPolicy {
    private static final Logger LOGGER = Logger.getLogger(FileListPseudoPolicy.class.getName());

    public class NoFileAtIndexException extends Exception {
    }

    private Policy delegatePolicy;

    public FileListPseudoPolicy(Policy delegatePolicy) {
        this.delegatePolicy = delegatePolicy;
    }

    void clearFile(int i) throws CMException {
        delegatePolicy.setComponent(getGroup(i), null);
    }

    DeploymentFile getFile(int index) throws NoFileAtIndexException, CMException {
        String fileName = delegatePolicy.getComponent(getGroup(index));

        if (fileName == null) {
            throw new NoFileAtIndexException();
        }

        // we forget the typ of the file stored here and always return file
        // deployment files.
        // that's enough for current needs but might need changing in the
        // future. Note that
        // jar files don't store their full path so they can't easily be
        // restored by their name.
        return new FileDeploymentFile(new File(fileName));
    }

    void setFile(int index, DeploymentFile file) throws CMException {
        delegatePolicy.setComponent(getGroup(index), file.getName());
    }

    private String getGroup(int index) {
        return "list." + index;
    }

    int getHighestIndexInFileList() {
        return new IntegerComponent("highestIndex").getIntValue(delegatePolicy);
    }

    void setHighestIndexInFileList(int i) throws CMException {
        new IntegerComponent("highestIndex").setIntValue(delegatePolicy, i);
    }

    public Iterator<DeploymentFile> iterator() {
        return new FetchingIterator<DeploymentFile>() {
            int i = getHighestIndexInFileList();

            @Override
            protected DeploymentFile fetch() {
                while (i >= 0) {
                    try {
                        return getFile(i--);
                    } catch (CMException e) {
                        LOGGER.log(Level.WARNING, e.getMessage(), e);
                    } catch (NoFileAtIndexException e) {
                        // next
                    }
                }

                return null;
            }
        };
    }

}
