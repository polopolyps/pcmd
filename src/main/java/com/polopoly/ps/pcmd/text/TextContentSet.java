package com.polopoly.ps.pcmd.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.polopoly.ps.pcmd.text.ExternalIdReference;
import com.polopoly.ps.pcmd.topologicalsort.TopologicalSorter;
import com.polopoly.ps.pcmd.validation.ValidationContext;
import com.polopoly.ps.pcmd.validation.ValidationResult;

public class TextContentSet implements Iterable<TextContent> {
    private Map<String, TextContent> contents = new LinkedHashMap<String, TextContent>();

    public ValidationResult validate(ValidationContext context) {
        ValidationResult result = new ValidationResult();

        for (TextContent content : contents()) {
            context.add(content);
        }

        for (TextContent content : contents()) {
            content.validate(context, result);
        }

        return result;
    }

    public void removeNonValidatingReferences(ValidationResult validationResult) {
        for (TextContent content : contents()) {
            content.removeNonValidatingReferences(validationResult);
        }
    }

    private Collection<TextContent> contents() {
        return contents.values();
    }

    public TextContent get(String id) {
        return contents.get(id);
    }

    public void add(TextContent currentContent) {
        contents.put(currentContent.getId(), currentContent);
    }

    public Iterator<TextContent> iterator() {
        return contents.values().iterator();
    }

    public void sortTopologically() {
        Map<String, TextContentVertex> vertices = new HashMap<String, TextContentVertex>(contents.size());

        for (Map.Entry<String, TextContent> entry : contents.entrySet()) {
            vertices.put(entry.getKey(), new TextContentVertex(entry.getKey(), entry.getValue()));
        }

        for (String id : vertices.keySet()) {
            addReferences(id, vertices);
        }

        List<TextContentVertex> vertexList = new ArrayList<TextContentVertex>(vertices.values());
        TopologicalSorter<TextContentVertex> sorter = new TopologicalSorter<TextContentVertex>(vertexList);
        vertexList = sorter.sort();

        contents.clear();
        for (TextContentVertex vertex : vertexList) {
            contents.put(vertex.getId(), vertex.getTextContent());
        }
    }

    private void addReferences(String id, Map<String, TextContentVertex> knownVertices) {
        TextContent content = contents.get(id);
        TextContentVertex vertex = knownVertices.get(id);

        addListReferences(content, vertex, knownVertices);
        addDirectReferences(content, vertex, knownVertices);
        addSecurityParentReference(content, vertex, knownVertices);
        addInputTemplateReference(content, vertex, knownVertices);
        addPublishingReference(content, vertex, knownVertices);
    }

    private void addListReferences(TextContent content, TextContentVertex vertex,
                                   Map<String, TextContentVertex> knownVertices) {
        for (List<Reference> references : content.getLists().values()) {
            for (Reference r : references) {
                addDependencyToReference(vertex, r, knownVertices);
            }
        }
    }

    private void addDirectReferences(TextContent content, TextContentVertex vertex,
                                     Map<String, TextContentVertex> knownVertices) {
        for (Map<String, Reference> group : content.getReferences().values()) {
            for (Reference r : group.values()) {
                addDependencyToReference(vertex, r, knownVertices);
            }
        }
    }

    private void addSecurityParentReference(TextContent content, TextContentVertex vertex,
                                            Map<String, TextContentVertex> knownVertices) {
        addDependencyToReference(vertex, content.getSecurityParent(), knownVertices);
    }

    private void addInputTemplateReference(TextContent content, TextContentVertex vertex,
                                           Map<String, TextContentVertex> knownVertices) {
        addDependencyToReference(vertex, content.getInputTemplate(), knownVertices);
    }

    private void addPublishingReference(TextContent content, TextContentVertex vertex,
                                        Map<String, TextContentVertex> knownVertices) {
        for (Publishing publishing : content.getPublishings()) {
            Reference publishIn = publishing.getPublishIn();
            if (!(publishIn instanceof ExternalIdReference)) {
                continue;
            }

            String publishInId = ((ExternalIdReference) publishIn).getExternalId();
            TextContentVertex publishInVertex = knownVertices.get(publishInId);
            if (publishInVertex != null) {
                publishInVertex.addDependency(vertex);
            }
        }
    }

    private void addDependencyToReference(TextContentVertex vertex, Reference reference,
                                          Map<String, TextContentVertex> knownVertices) {
        if (!(reference instanceof ExternalIdReference)) {
            return;
        }

        ExternalIdReference externalIdReference = (ExternalIdReference) reference;
        TextContentVertex referredVertex = knownVertices.get(externalIdReference.getExternalId());
        if (referredVertex != null) {
            vertex.addDependency(referredVertex);
        }

        if (externalIdReference.getMetadataExternalId() != null) {
            referredVertex = knownVertices.get(externalIdReference.getMetadataExternalId());
            if (referredVertex != null) {
                vertex.addDependency(referredVertex);
            }
        }
    }

    public int size() {
        return contents.size();
    }

    public void addAll(TextContentSet set) {
        for (TextContent content : set) {
            add(content);
        }
    }
}
