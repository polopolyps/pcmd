package com.polopoly.ps.contentimporter.hotdeploy.text;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.codec.binary.Base64;

import com.polopoly.ps.contentimporter.hotdeploy.client.Major;

public class TextContentXmlWriter
{
    private static final String BATCH_START =         "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                                      "<batch xmlns=\"http://www.polopoly.com/polopoly/cm/xmlio\">\n";
    private static final String CONTENT_START =       "  <content>\n";
    private static final String CONTENT_ID_START =    "    <metadata>\n";
    private static final String CONTENT_ID =          "      <contentid>\n" +
                                                      "        <major>%s</major>\n" +
                                                      "        <externalid>%s</externalid>\n" +
                                                      "      </contentid>\n";
    private static final String CONTENT_ID_ALT =      "      <contentid>\n" +
                                                      "        <externalid>%s</externalid>\n" +
                                                      "      </contentid>\n";
    private static final String CONTENT_ID_SECURITY = "      <security-parent>\n" +
                                                      "        <externalid>%s</externalid>\n" +
                                                      "      </security-parent>\n";
    private static final String CONTENT_ID_INPUT =    "      <input-template>\n" +
                                                      "        <externalid>%s</externalid>\n" +
                                                      "      </input-template>\n";
    private static final String WF_ACTIONS_START =    "      <workflowactions>\n";
    private static final String WF_ACTIONS_ACTION =   "        <action>%s</action>\n";
    private static final String WF_ACTIONS_END =      "      </workflowactions>\n";
    private static final String CONTENT_ID_END =      "    </metadata>\n";
    private static final String LIST_START =          "    <contentlist mode=\"%s\" group=\"%s\">\n";
    private static final String LIST_ENTRY =          "      <entry mode=\"modify\" withMetadata=\"false\">\n" +
                                                      "        <metadata>\n" +
                                                      "          <referredContent>\n" +
                                                      "            <contentid>\n" +
                                                      "              <externalid>%s</externalid>\n" +
                                                      "            </contentid>\n" +
                                                      "          </referredContent>\n" +
                                                      "        </metadata>\n" +
                                                      "      </entry>\n";
    private static final String LIST_REF_ENTRY =      "      <entry withMetadata=\"true\">\n" +
                                                      "        <metadata>\n" +
                                                      "          <referredContent>\n" +
                                                      "            <contentid>\n" +
                                                      "              <externalid>%s</externalid>\n" +
                                                      "            </contentid>\n" +
                                                      "          </referredContent>\n" +
                                                      "          <contentid>\n" +
                                                      "            <externalid>%s</externalid>\n" +
                                                      "          </contentid>\n" +
                                                      "        </metadata>\n" +
                                                      "      </entry>\n";
    private static final String LIST_END =            "    </contentlist>\n";
    private static final String ExternalIdReference = "    <contentref group=\"%s\" name=\"%s\">\n" +
                                                      "      <contentid>\n" +
                                                      "        <externalid>%s</externalid>\n" +
                                                      "      </contentid>\n" +
                                                      "    </contentref>\n";
    private static final String COMPONENT =           "    <component group=\"%s\" name=\"%s\"><![CDATA[%s]]></component>\n";
    private static final String FILE_START =          "    <file encoding=\"base64\" name=\"%s\">";
    private static final String FILE_END =            "    </file>";
    private static final String CONTENT_END =         "  </content>\n";
    private static final String BATCH_END =           "</batch>\n";

    private final Writer writer;
    private Set<String> log = new TreeSet<String>();

    public TextContentXmlWriter(final Writer writer)
        throws IOException
    {
        this.writer = writer;
        writer.write(BATCH_START);
    }

    private static class PublishList
    {
        public String id;
        public String group;

        public List<ExternalIdReference> list = new ArrayList<ExternalIdReference>();

        public PublishList(String publishIn, String publishInGroup)
        {
            this.id = publishIn;
            this.group = publishInGroup;
        }
    }

    public void write(final TextContentSet content)
        throws IOException
    {
        SortedMap<String, PublishList> publishings = new TreeMap<String, PublishList>();
        Map<String, String> metadataRefs = new HashMap<String, String>();

        for (TextContent tc : content) {
            // Write "bootstrap" content definitions
            writer.write(CONTENT_START);
            writer.write(CONTENT_ID_START);
            writer.write(String.format(CONTENT_ID, getMajor(tc), tc.getId()));

            if (tc.getInputTemplate() != null) {
                writer.write(String.format(CONTENT_ID_INPUT, tc.getInputTemplate().getExternalId()));
            }

            writer.write(CONTENT_ID_END);
            writer.write(CONTENT_END);

            // Collect publish definitions to be able to insert them inline

            for (Publishing pub : tc.getPublishings()) {
                String publishIn = pub.getPublishIn().getExternalId();
                String key = publishIn + ":" + pub.getPublishInGroup();

                PublishList list = publishings.get(key);

                if (list == null) {
                    list = new PublishList(publishIn, pub.getPublishInGroup());
                    publishings.put(key, list);
                }

                list.list.add(new ExternalIdReference(tc.getId(), pub.getPublishIn().getMetadataExternalId()));
            }

            if (tc.getTemplateId() != null) {
                TextContent template = content.get(tc.getTemplateId());

                if (template == null) {
                    throw new RuntimeException(tc.getId() + " has template " + tc.getTemplateId() + " which was not found in this text content");
                }

                for (Publishing pub : template.getPublishings()) {
                    String publishIn = pub.getPublishIn().getExternalId();
                    String key = publishIn + ":" + pub.getPublishInGroup();

                    PublishList list = publishings.get(key);

                    if (list == null) {
                        list = new PublishList(publishIn, pub.getPublishInGroup());
                        publishings.put(key, list);
                    }

                    list.list.add(new ExternalIdReference(tc.getId(), pub.getPublishIn().getMetadataExternalId()));
                }
            }

            // Check for metadata requirements, since we need to make sure the ExternalIdReference points to the right content

            for (List<ExternalIdReference> list : tc.getLists().values()) {
                for (ExternalIdReference ref : list) {
                    if (ref.getMetadataExternalId() != null) {
                        String earlier = metadataRefs.get(ref.getMetadataExternalId());

                        if (earlier != null) {
                            if (!earlier.equals(ref.getExternalId())) {
                                throw new RuntimeException("Invalid ExternalIdReferences, metadata " +
                                                            ref.getMetadataExternalId() + " is bound to both " +
                                                            ref.getExternalId() + " and " + earlier);
                            }
                        } else {
                            metadataRefs.put(ref.getMetadataExternalId(), ref.getExternalId());
                        }
                    }
                }
            }

            if (tc.getTemplateId() != null) {
                TextContent template = content.get(tc.getTemplateId());

                for (List<ExternalIdReference> list : template.getLists().values()) {
                    for (ExternalIdReference ref : list) {
                        if (ref.getMetadataExternalId() != null) {
                            String earlier = metadataRefs.get(ref.getMetadataExternalId());

                            if (earlier != null) {
                                if (!earlier.equals(ref.getExternalId())) {
                                    throw new RuntimeException("Invalid ExternalIdReferences, metadata " +
                                                                ref.getMetadataExternalId() + " is bound to both " +
                                                                ref.getExternalId() + " and " + earlier);
                                }
                            } else {
                                metadataRefs.put(ref.getMetadataExternalId(), ref.getExternalId());
                            }
                        }
                    }
                }
            }
        }

        for (TextContent tc : content) {
            TextContent template = null;

            if (tc.getTemplateId() != null) {
                template = content.get(tc.getTemplateId());
            }

            writer.write(CONTENT_START);
            writer.write(CONTENT_ID_START);
            writer.write(String.format(CONTENT_ID, getMajor(tc), tc.getId()));

            if (tc.getSecurityParent() != null) {
                writer.write(String.format(CONTENT_ID_SECURITY, tc.getSecurityParent().getExternalId()));
            }

            if (tc.getInputTemplate() != null) {
                writer.write(String.format(CONTENT_ID_INPUT, tc.getInputTemplate().getExternalId()));
            }
            writeWorkflowActions(mergeWorkflowActions(template, tc.getWorkflowActions()));

            writer.write(CONTENT_ID_END);

            if (template == null) {
                if (tc.getTemplateId() != null) {
                    throw new RuntimeException(tc.getId() + " has template " + tc.getTemplateId() + " which was not found in this text content");
                }
            } else {
                if (template.getTemplateId() != null) {
                    throw new RuntimeException(tc.getId() + " has template " + tc.getTemplateId() + " that also has a template, this is not allowed");
                }
            }

            writeComponents(mergeComps(template, tc.getComponents()));
            writeExternalIdReferences(mergeRefMeta(mergeRefs(template, tc.getReferences()), metadataRefs.get(tc.getId())));

            // The mergePublishList function will add publish entries to declared lists directly (at the end)

            writeLists("reset", mergePublishList(tc.getId(), publishings, mergeLists(template, tc.getLists())));
            writeFiles(mergeFiles(template, tc.getFiles()));

            // Write publish entries that does not have corresponding list declaration in this content

            List<String> used = new ArrayList<String>();

            for (Map.Entry<String, PublishList> entry : publishings.tailMap(tc.getId()).entrySet()) {
                if (!entry.getKey().startsWith(tc.getId() + ":")) {
                    break;
                }

                if (entry.getValue().id.equals(tc.getId())) {
                    writeList("modify", entry.getValue().group, entry.getValue().list);
                    used.add(entry.getKey());
                }
            }

            for (String u : used) {
                publishings.remove(u);
            }

            writer.write(CONTENT_END);
        }

        // Write publish entries for publications into external content

        for (String contentid : content(publishings)) {
            writer.write(CONTENT_START);
            writer.write(CONTENT_ID_START);
            writer.write(String.format(CONTENT_ID_ALT, contentid));
            writer.write(CONTENT_ID_END);

            for (Map.Entry<String, PublishList> entry : publishings.tailMap(contentid).entrySet()) {
                if (!entry.getKey().startsWith(contentid)) {
                    break;
                }

                if (entry.getValue().id.equals(contentid)) {
                    writeList("modify", entry.getValue().group, entry.getValue().list);
                }
            }

            writer.write(CONTENT_END);
        }
    }

    private int getMajor(final TextContent tc)
    {
        if (tc.getMajor().getIntegerMajor() == -1) {
            log.add("Content with external id '" + tc.getId() + "' has no defined major, using 'Article'");
            return Major.ARTICLE.getIntegerMajor();
        }

        return tc.getMajor().getIntegerMajor();
    }

    // Merge the lists, for now don't merge the individual lists prefer the lists defined in this content.
    // No reason except I don't see a merged list to be useful right now, could be.
    private Map<String, List<ExternalIdReference>> mergeLists(final TextContent template,
                                                              final Map<String, List<ExternalIdReference>> lists)
    {
        if (template == null) {
            return lists;
        }

        if (template.getLists().size() == 0) {
            return lists;
        }

        Map<String, List<ExternalIdReference>> merged = new HashMap<String, List<ExternalIdReference>>();

        merged.putAll(template.getLists());
        merged.putAll(lists);

        return merged;
    }

    private Map<String, byte[]> mergeFiles(final TextContent template,
                                           final Map<String, byte[]> files)
    {
        if (template == null) {
            return files;
        }

        Map<String, byte[]> templateFiles = template.getFiles();

        if (templateFiles.size() == 0) {
            return files;
        }

        Map<String, byte[]> merged = new HashMap<String, byte[]>();

        merged.putAll(templateFiles);
        merged.putAll(files);

        return merged;
    }

    private Map<String, Map<String, ExternalIdReference>> mergeRefMeta(final Map<String, Map<String, ExternalIdReference>> refs,
                                                                       final String referredId)
    {
        if (referredId == null) {
            return refs;
        }

        Map<String, Map<String, ExternalIdReference>> merged = new HashMap<String, Map<String,ExternalIdReference>>();
        merged.putAll(refs);

        Map<String, ExternalIdReference> meta = merged.get("polopoly.ExternalIdReferenceMetaData");

        if (meta == null) {
            meta = new HashMap<String, ExternalIdReference>();
            meta.put("referredId", new ExternalIdReference(referredId));
            merged.put("polopoly.ExternalIdReferenceMetaData", meta);
        } else {
            ExternalIdReference ref = meta.get("referredId");

            if (ref != null) {
                if (!ref.getExternalId().equals(referredId)) {
                    throw new RuntimeException("Invalid metadata ExternalIdReference, added to list as ExternalIdReference to " +
                                                referredId + " but refers to " + ref.getExternalId());
                }
            } else {
                HashMap<String, ExternalIdReference> mod = new HashMap<String, ExternalIdReference>();

                mod.putAll(meta);
                mod.put("referredId", new ExternalIdReference(referredId));
                merged.put("polopoly.ExternalIdReferenceMetaData", mod);
            }
        }

        return merged;
    }

    private Map<String, Map<String, ExternalIdReference>> mergeRefs(final TextContent template,
                                                                    final Map<String, Map<String, ExternalIdReference>> ExternalIdReferences)
    {
        if (template == null) {
            return ExternalIdReferences;
        }

        if (template.getReferences().size() == 0) {
            return ExternalIdReferences;
        }

        Map<String, Map<String, ExternalIdReference>> merged = new HashMap<String, Map<String,ExternalIdReference>>();

        Set<String> keys = new HashSet<String>();
        keys.addAll(ExternalIdReferences.keySet());
        keys.addAll(template.getReferences().keySet());

        for (String key : keys) {
            Map<String, ExternalIdReference> r1 = ExternalIdReferences.get(key);
            Map<String, ExternalIdReference> r2 = template.getReferences().get(key);

            if (r1 == null) {
                merged.put(key, r2);
            } else if (r2 == null) {
                merged.put(key, r1);
            } else {
                Map<String, ExternalIdReference> child = new HashMap<String, ExternalIdReference>();

                child.putAll(r2);
                child.putAll(r1);

                merged.put(key,  child);
            }
        }

        return merged;
    }

    private Map<String, Map<String, String>> mergeComps(final TextContent template,
                                                        final Map<String, Map<String, String>> components)
    {
        if (template == null) {
            return components;
        }

        if (template.getComponents().size() == 0) {
            return components;
        }

        Map<String, Map<String, String>> merged = new HashMap<String, Map<String,String>>();

        Set<String> keys = new HashSet<String>();

        keys.addAll(components.keySet());
        keys.addAll(template.getComponents().keySet());

        for (String key : keys) {
            Map<String, String> r1 = components.get(key);
            Map<String, String> r2 = template.getComponents().get(key);

            if (r1 == null) {
                merged.put(key, r2);
            } else if (r2 == null) {
                merged.put(key, r1);
            } else {
                Map<String, String> child = new HashMap<String, String>();

                child.putAll(r2);
                child.putAll(r1);

                merged.put(key,  child);
            }
        }

        return merged;
    }

    private List<String> mergeWorkflowActions(TextContent template, List<String> workflowActions)
    {
        if (template == null) {
            return workflowActions;
        }

        if (template.getWorkflowActions().size() == 0) {
            return workflowActions;
        }

        List<String> mergedList = new ArrayList<String>(template.getWorkflowActions());
        mergedList.addAll(workflowActions);
        return mergedList;
    }

    private Set<String> content(final SortedMap<String, PublishList> publishings)
    {
        Set<String> set = new TreeSet<String>();

        for (PublishList pl : publishings.values()) {
            set.add(pl.id);
        }

        return set;
    }

    private Map<String, List<ExternalIdReference>> mergePublishList(final String id,
                                                                    final Map<String, PublishList> publishings,
                                                                    final Map<String, List<ExternalIdReference>> lists)
    {
        Map<String, List<ExternalIdReference>> result = new HashMap<String, List<ExternalIdReference>>();

        for (Map.Entry<String, List<ExternalIdReference>> entry : lists.entrySet()) {
            PublishList publish = publishings.get(id + ":" + entry.getKey());

            if (publish == null) {
                result.put(entry.getKey(), entry.getValue());
            } else {
                List<ExternalIdReference> l = new ArrayList<ExternalIdReference>();

                l.addAll(entry.getValue());
                l.addAll(publish.list);

                result.put(entry.getKey(), l);
                publishings.remove(id + ":" + entry.getKey());
            }
        }

        return result;
    }

    private void writeFiles(final Map<String, byte[]> files)
        throws IOException
    {
        List<String> names = new ArrayList<String>(files.keySet());
        Collections.sort(names);

        for (String name : names) {
            writer.write(String.format(FILE_START, name));
            writeBase64(files.get(name));
            writer.write(FILE_END);
        }
    }

    private void writeBase64(final byte[] bs)
        throws IOException
    {
        writer.write(new String(Base64.encodeBase64(bs,  true)));
    }

    private void writeLists(final String mode,
                            final Map<String, List<ExternalIdReference>> lists)
        throws IOException
    {
        for (Map.Entry<String, List<ExternalIdReference>> list : lists.entrySet()) {
            writeList(mode, list.getKey(), list.getValue());
        }
    }

    private void writeList(final String mode,
                           final String group,
                           final List<ExternalIdReference> list)
        throws IOException
    {
        writer.write(String.format(LIST_START, mode, group));

        for (ExternalIdReference ref : list) {
            if (ref.getMetadataExternalId() != null) {
                writer.write(String.format(LIST_REF_ENTRY, ref.getExternalId(), ref.getMetadataExternalId()));
            } else {
                writer.write(String.format(LIST_ENTRY, ref.getExternalId()));
            }
        }

        writer.write(LIST_END);
    }

    private void writeExternalIdReferences(final Map<String, Map<String, ExternalIdReference>> ExternalIdReferences)
        throws IOException
    {
        for (Map.Entry<String, Map<String, ExternalIdReference>> group : ExternalIdReferences.entrySet()) {
            for (Map.Entry<String, ExternalIdReference> nameValue : group.getValue().entrySet()) {
                writer.write(String.format(ExternalIdReference, group.getKey(), nameValue.getKey(), nameValue.getValue()));
            }
        }
    }

    private void writeComponents(final Map<String, Map<String, String>> components)
        throws IOException
    {
        for (Map.Entry<String, Map<String, String>> group : components.entrySet()) {
            for (Map.Entry<String, String> nameValue : group.getValue().entrySet()) {
                writer.write(String.format(COMPONENT, group.getKey(), nameValue.getKey(), nameValue.getValue()));
            }
        }
    }

    private void writeWorkflowActions(List<String> workflowActions)
        throws IOException
    {
        if (workflowActions.size() == 0) {
            return;
        }
        writer.write(WF_ACTIONS_START);
        for (String action : workflowActions) {
            writer.write(String.format(WF_ACTIONS_ACTION, action));
        }
        writer.write(WF_ACTIONS_END);
    }

    public List<String> getLog() {
        return new ArrayList<String>(log);
    }

    public void close()
        throws IOException
    {
        writer.write(BATCH_END);
        writer.flush();
        writer.close();
    }
}
