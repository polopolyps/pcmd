package com.polopoly.ps.pcmd.validation;

import com.polopoly.ps.pcmd.text.Reference;
import com.polopoly.ps.pcmd.text.TextContent;

public class ValidationFailure {

    private TextContent textContent;
    private Reference reference;
    private String message;

    public ValidationFailure(TextContent textContent, Reference reference, String message) {
        this.setTextContent(textContent);
        this.setReference(reference);
        this.setMessage(message);
    }

    private void setTextContent(TextContent textContent) {
        this.textContent = textContent;
    }

    public TextContent getTextContent() {
        return textContent;
    }

    private void setReference(Reference reference) {
        this.reference = reference;
    }

    public Reference getReference() {
        return reference;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return "In " + textContent + ": " + reference + " was invalid: " + message;
    }
}
