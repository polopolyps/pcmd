package com.polopoly.ps.pcmd.text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.ps.pcmd.client.Major;
import com.polopoly.ps.pcmd.validation.ReferenceValidationException;
import com.polopoly.ps.pcmd.validation.ValidationContext;
import com.polopoly.ps.pcmd.validation.ValidationException;
import com.polopoly.ps.pcmd.validation.ValidationResult;

public class TextContent {
    private static final Logger logger = Logger.getLogger(TextContent.class.getName());

    private Major major = Major.UNKNOWN;
    private String id;
    private Reference securityParent;
    private Reference inputTemplate;

    private Map<String, Map<String, String>> components = new HashMap<String, Map<String, String>>();
    private Map<String, Map<String, Reference>> references = new HashMap<String, Map<String, Reference>>();
    private Map<String, List<Reference>> lists = new HashMap<String, List<Reference>>();
    private Map<String, FileContent> files = new HashMap<String, FileContent>();

    private class FileContent {
        byte[] data;
        URL source;

        public FileContent(byte[] data, URL source) {
            super();
            this.data = data;
            this.source = source;
        }
    }

    private List<Publishing> publishings = new ArrayList<Publishing>();
    private List<String> workflowActions = new ArrayList<String>();

    private String templateId;

    public Map<String, List<Reference>> getLists() {
        return lists;
    }

    public Map<String, Map<String, String>> getComponents() {
        return components;
    }

    public Map<String, Map<String, Reference>> getReferences() {
        return references;
    }

    public void setComponent(String group, String name, String value) {
        Map<String, String> groupMap = components.get(group);

        if (groupMap == null) {
            groupMap = new HashMap<String, String>();
            components.put(group, groupMap);
        }

        groupMap.put(name, value);
    }

    public String getComponent(String group, String name) {
        Map<String, String> groupMap = components.get(group);

        if (groupMap == null) {
            return null;
        }

        return groupMap.get(name);
    }

    public void setReference(String group, String name, Reference reference) {
        Map<String, Reference> groupMap = references.get(group);

        if (groupMap == null) {
            groupMap = new HashMap<String, Reference>();
            references.put(group, groupMap);
        }

        groupMap.put(name, reference);
    }

    public Reference getReference(String group, String name) {
        Map<String, Reference> groupMap = references.get(group);

        if (groupMap == null) {
            return null;
        }

        return groupMap.get(name);
    }

    public void setSecurityParent(Reference securityParent) {
        this.securityParent = securityParent;
    }

    public Reference getSecurityParent() {
        return securityParent;
    }

    public void setInputTemplate(Reference inputTemplate) {
        this.inputTemplate = inputTemplate;
    }

    public Reference getInputTemplate() {
        return inputTemplate;
    }

    public List<Reference> getList(String group) {
        List<Reference> list = lists.get(group);

        if (list == null) {
            list = new ArrayList<Reference>();
            lists.put(group, list);
        }

        return list;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void removeNonValidatingReferences(ValidationResult validationResult) {
        for (Entry<String, Map<String, Reference>> groupEntry : references.entrySet()) {
            Iterator<Entry<String, Reference>> referenceEntryIt = groupEntry.getValue().entrySet().iterator();

            while (referenceEntryIt.hasNext()) {
                Entry<String, Reference> referenceEntry = referenceEntryIt.next();

                try {
                    validationResult.validate(referenceEntry.getValue());
                } catch (ReferenceValidationException e) {
                    warnInvalidReference(e);

                    referenceEntryIt.remove();
                }
            }
        }

        for (Entry<String, List<Reference>> listEntry : lists.entrySet()) {
            Iterator<Reference> referenceIt = listEntry.getValue().iterator();

            while (referenceIt.hasNext()) {
                Reference reference = referenceIt.next();

                try {
                    validationResult.validate(reference);
                } catch (ReferenceValidationException e) {
                    warnInvalidReference(e);
                    referenceIt.remove();
                    validationResult.wasFixed(reference);
                }
            }
        }

        if (securityParent != null) {
            try {
                validationResult.validate(securityParent);
            } catch (ReferenceValidationException e) {
                warnInvalidReference(e);

                securityParent = null;

                validationResult.wasFixed(securityParent);
            }
        }
    }

    private void warnInvalidReference(ReferenceValidationException e) {
        logger.log(Level.WARNING, "The reference " + e.getFailure().getReference() + " in " + this
                                  + " failed to validate (" + e.getMessage() + "). Removing the reference.");
    }

    public void validate(ValidationContext context, ValidationResult result) {
        if (id == null) {
            result.addFailure(this, "Content without ID specified.");
        }

        if (inputTemplate == null && templateId == null) {
            result.addFailure(this, "No input template or content template.");
        }

        if (inputTemplate != null) {
            try {
                inputTemplate.validateTemplate(context);
            } catch (ValidationException v) {
                result.addFailure(this, inputTemplate, "Input template: " + v.getMessage());
            }
        }

        if (templateId != null) {
            try {
                context.validateTextContentExistence(templateId);
            } catch (ValidationException e) {
                result.addFailure(this, "Template: " + e.getMessage());
            }
        }

        for (Entry<String, Map<String, Reference>> groupEntry : references.entrySet()) {
            String group = groupEntry.getKey();

            Iterator<Entry<String, Reference>> referenceEntryIt = groupEntry.getValue().entrySet().iterator();

            while (referenceEntryIt.hasNext()) {
                Entry<String, Reference> referenceEntry = referenceEntryIt.next();

                String name = referenceEntry.getKey();
                Reference reference = referenceEntry.getValue();

                try {
                    reference.validate(context);
                } catch (ValidationException v) {
                    result.addFailure(this, reference, "Reference " + group + ":" + name + " to " + reference + ": "
                                                       + v.getMessage());
                }
            }
        }

        for (Entry<String, List<Reference>> listEntry : lists.entrySet()) {
            Iterator<Reference> referenceIt = listEntry.getValue().iterator();

            while (referenceIt.hasNext()) {
                Reference reference = referenceIt.next();

                try {
                    reference.validate(context);
                } catch (ValidationException v) {
                    result.addFailure(this, reference, "Reference in list " + listEntry.getKey() + " to " + reference
                                                       + ": " + v.getMessage());
                }
            }
        }

        if (securityParent != null) {
            try {
                securityParent.validate(context);
            } catch (ValidationException v) {
                result.addFailure(this, securityParent, "security parent: " + v.getMessage());
            }
        }

        for (Publishing publishing : publishings) {
            try {
                publishing.getPublishIn().validate(context);
            } catch (ValidationException v) {
                result.addFailure(this, publishing.getPublishIn(), "Content to publish in: " + v.getMessage());
            }
        }
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public List<Publishing> getPublishings() {
        return publishings;
    }

    public void addFile(String fileName, InputStream fileData, URL fileUrl) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(10000);

        int ch;

        while ((ch = fileData.read()) != -1) {
            baos.write(ch);
        }

        files.put(fileName, new FileContent(baos.toByteArray(), fileUrl));
    }

    public Iterable<String> getFileNames() {
        return files.keySet();
    }

    public URL getFileSource(String fileName) throws NoSuchFileException {
        FileContent result = files.get(fileName);

        if (result == null) {
            throw new NoSuchFileException("The file " + fileName + " was not defined in " + this + ".");
        }

        return result.source;
    }

    public byte[] getFileData(String fileName) throws NoSuchFileException {
        FileContent result = files.get(fileName);

        if (result == null) {
            throw new NoSuchFileException("The file " + fileName + " was not defined in " + this + ".");
        }

        return result.data;
    }

    public void addPublishing(Publishing publishing) {
        publishings.add(publishing);
    }

    public Major getMajor() {
        return major;
    }

    public void setMajor(Major major) {
        this.major = major;
    }

    @Override
    public String toString() {
        if (id == null) {
            return "<no ID specified>";
        } else {
            return id;
        }
    }

    @Override
    public int hashCode() {
        return ((id == null) ? 0 : id.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TextContent other = (TextContent) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public void addWorkflowAction(String workflowAction) {
        this.workflowActions.add(workflowAction);
    }

    public List<String> getWorkflowActions() {
        return workflowActions;
    }
}
