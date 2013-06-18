package com.polopoly.ps.pcmd.validation;

import com.polopoly.ps.pcmd.text.TextContent;

public interface ValidationContext {

    void validateContentExistence(String externalId) throws ValidationException;

    void validateTemplateExistence(String externalId) throws ValidationException;

    void add(TextContent textContent);

    void validateTextContentExistence(String templateId) throws ValidationException;

}
