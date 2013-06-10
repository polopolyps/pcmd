package com.polopoly.ps.contentimporter.hotdeploy.text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.polopoly.ps.contentimporter.hotdeploy.client.Major;

public class TextContent
{
    private Major major = Major.UNKNOWN;

    private String id;
    private ExternalIdReference securityParent;
    private ExternalIdReference inputTemplate;

    private Map<String, Map<String, String>> components = new HashMap<String, Map<String, String>>();
    private Map<String, Map<String, ExternalIdReference>> references = new HashMap<String, Map<String, ExternalIdReference>>();
    private Map<String, List<ExternalIdReference>> lists = new HashMap<String, List<ExternalIdReference>>();
    private Map<String, byte[]> files = new HashMap<String, byte[]>();

    private List<Publishing> publishings = new ArrayList<Publishing>();
    private List<String> workflowActions = new ArrayList<String>();

    private String templateId;

    public Map<String, List<ExternalIdReference>> getLists()
    {
        return lists;
    }

    public Map<String, Map<String, String>> getComponents()
    {
        return components;
    }

    public Map<String, Map<String, ExternalIdReference>> getReferences()
    {
        return references;
    }

    public void setComponent(final String group,
                             final String name,
                             final String value)
    {
        Map<String, String> groupMap = components.get(group);

        if (groupMap == null) {
            groupMap = new HashMap<String, String>();
            components.put(group, groupMap);
        }

        groupMap.put(name, value);
    }

    public String getComponent(final String group,
                               final String name)
    {
        Map<String, String> groupMap = components.get(group);

        if (groupMap == null) {
            return null;
        }

        return groupMap.get(name);
    }

    public void setReference(final String group,
                             final String name,
                             final ExternalIdReference reference)
    {
        Map<String, ExternalIdReference> groupMap = references.get(group);

        if (groupMap == null) {
            groupMap = new HashMap<String, ExternalIdReference>();
            references.put(group, groupMap);
        }

        groupMap.put(name, reference);
    }

    public ExternalIdReference getReference(final String group,
                                            final String name)
    {
        Map<String, ExternalIdReference> groupMap = references.get(group);

        if (groupMap == null) {
            return null;
        }

        return groupMap.get(name);
    }

    public void setSecurityParent(final ExternalIdReference securityParent)
    {
        this.securityParent = securityParent;
    }

    public ExternalIdReference getSecurityParent()
    {
        return securityParent;
    }

    public void setInputTemplate(final ExternalIdReference inputTemplate)
    {
        this.inputTemplate = inputTemplate;
    }

    public ExternalIdReference getInputTemplate()
    {
        return inputTemplate;
    }

    public List<ExternalIdReference> getList(final String group)
    {
        List<ExternalIdReference> list = lists.get(group);

        if (list == null) {
            list = new ArrayList<ExternalIdReference>();
            lists.put(group, list);
        }

        return list;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public String getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(final String templateId)
    {
        this.templateId = templateId;
    }

    public List<Publishing> getPublishings()
    {
        return publishings;
    }

    void addFile(final String fileName,
                 final InputStream fileData)
        throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(10000);

        int ch;

        while ((ch = fileData.read()) != -1) {
            baos.write(ch);
        }

        files.put(fileName, baos.toByteArray());
    }

    Map<String, byte[]> getFiles()
    {
        return files;
    }

    public void addPublishing(final Publishing publishing)
    {
        publishings.add(publishing);
    }

    public Major getMajor()
    {
        return major;
    }

    public void setMajor(final Major major)
    {
        this.major = major;
    }

    public void addWorkflowAction(String workflowAction) {
        this.workflowActions.add(workflowAction);
    }

    public List<String> getWorkflowActions() {
        return workflowActions;
    }

    @Override
    public String toString()
    {
        if (id == null) {
            return "<no ID specified>";
        } else {
            return id;
        }
    }

    @Override
    public int hashCode()
    {
        return ((id == null) ? 0 : id.hashCode());
    }

    @Override
    public boolean equals(Object obj)
    {
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
}
