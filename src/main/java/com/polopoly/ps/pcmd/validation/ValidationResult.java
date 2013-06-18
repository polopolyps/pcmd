package com.polopoly.ps.pcmd.validation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.polopoly.ps.pcmd.text.Reference;
import com.polopoly.ps.pcmd.text.TextContent;

public class ValidationResult {
    private Set<ValidationFailure> failures = new HashSet<ValidationFailure>();

    private Map<Reference, ValidationFailure> failureByReference = new HashMap<Reference, ValidationFailure>();

    public void addFailure(TextContent textContent, String message) {
        ValidationFailure failure = new ValidationFailure(textContent, null, message);

        failures.add(failure);
    }

    public void addFailure(TextContent textContent, Reference reference, String message) {
        ValidationFailure failure = new ValidationFailure(textContent, reference, message);

        failures.add(failure);
        failureByReference.put(reference, failure);
    }

    public void validate(Reference reference) throws ReferenceValidationException {
        ValidationFailure result = failureByReference.get(reference);

        if (result != null) {
            throw new ReferenceValidationException(result);
        }
    }

    public void wasFixed(Reference reference) {
        failures.remove(failureByReference.remove(reference));
    }

    public boolean isFailed() {
        return !failures.isEmpty();
    }

    public String getMessage() {
        StringBuffer result = new StringBuffer(1000);

        for (ValidationFailure failure : failures) {
            if (result.length() > 0) {
                result.append(", ");
            }

            result.append(failure.toString());
        }

        return result.toString();
    }

}
