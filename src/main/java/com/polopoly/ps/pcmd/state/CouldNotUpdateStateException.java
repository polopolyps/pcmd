package com.polopoly.ps.pcmd.state;

import com.polopoly.ps.pcmd.file.DeploymentFile;

/**
 * Thrown by {@link DirectoryState#reset(DeploymentFile, boolean)} when the
 * changes could not be recorded to the state due to an error.
 */
public class CouldNotUpdateStateException extends Exception {
    public CouldNotUpdateStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public CouldNotUpdateStateException(Throwable cause) {
        super(cause);
    }

    public CouldNotUpdateStateException(String message) {
        super(message);
    }
}