package com.polopoly.ps.pcmd.validation;

public class ReferenceValidationException extends ValidationException {

    private ValidationFailure failure;

    public ReferenceValidationException(ValidationFailure failure) {
        super(failure.getMessage());

        this.setFailure(failure);
    }

    private void setFailure(ValidationFailure failure) {
        this.failure = failure;
    }

    public ValidationFailure getFailure() {
        return failure;
    }

}
