package com.polopoly.ps.pcmd.tool.xml.export;

import static com.polopoly.cm.server.ServerNames.CONTENT_ATTRG_SYSTEM;
import static com.polopoly.cm.server.ServerNames.CONTENT_ATTR_NAME;
import static com.polopoly.ps.pcmd.text.TextContentParser.COMPONENT_PREFIX;
import static com.polopoly.ps.pcmd.text.TextContentParser.FILE_PREFIX;
import static com.polopoly.ps.pcmd.text.TextContentParser.ID_PREFIX;
import static com.polopoly.ps.pcmd.text.TextContentParser.INPUT_TEMPLATE_PREFIX;
import static com.polopoly.ps.pcmd.text.TextContentParser.LIST_PREFIX;
import static com.polopoly.ps.pcmd.text.TextContentParser.MAJOR_PREFIX;
import static com.polopoly.ps.pcmd.text.TextContentParser.NAME_PREFIX;
import static com.polopoly.ps.pcmd.text.TextContentParser.REFERENCE_PREFIX;
import static com.polopoly.ps.pcmd.text.TextContentParser.SECURITY_PARENT_PREFIX;
import static com.polopoly.ps.pcmd.text.TextContentParser.SEPARATOR_CHAR;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import com.polopoly.cm.ContentFileInfo;
import com.polopoly.cm.ContentId;
import com.polopoly.cm.ContentReference;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.collections.ContentList;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.cm.server.ServerNames;
import com.polopoly.cm.xml.util.export.ExternalIdGenerator;
import com.polopoly.ps.pcmd.client.Major;
import com.polopoly.ps.pcmd.tool.xml.export.contentlistentry.ContentReferenceFilter;
import com.polopoly.ps.pcmd.xml.export.ExportException;
import com.polopoly.ps.pcmd.xml.export.SingleContentToFileExporter;

public class SingleContentToContentFileExporter implements SingleContentToFileExporter {
    private PolicyCMServer server;

    private ContentReferenceFilter filter;

    private ExternalIdGenerator externalIdGenerator;

    public SingleContentToContentFileExporter(PolicyCMServer server, ContentReferenceFilter filter,
                                              ExternalIdGenerator externalIdGenerator) {
        this.server = server;
        this.filter = filter;
        this.externalIdGenerator = externalIdGenerator;
    }

    @Override
    public void exportContentToFile(List<? extends ContentRead> contents, File file)
        throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerConfigurationException,
        TransformerException, FileNotFoundException, ExportException {
        try {
            Writer writer = startFile(file);

            boolean first = true;

            for (ContentRead content : contents) {
                if (first) {
                    first = false;
                } else {
                    writeln(writer);
                }

                exportSingleContent(file, writer, content);
            }

            endFile(writer);
        } catch (CMException e) {
            throw new ExportException(e);
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    public void exportSingleContentToFile(ContentRead content, File file) throws ExportException {
        try {
            Writer writer = startFile(file);

            exportSingleContent(file, writer, content);

            endFile(writer);
        } catch (CMException e) {
            throw new ExportException(e);
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    private void exportSingleContent(File file, Writer writer, ContentRead content) throws CMException, IOException,
        ExportException, FileNotFoundException {
        writeId(content, writer);

        writeMajor(content, writer);

        writeName(content, writer);

        writeSecurityParent(content, writer);

        writeComponents(content, writer);

        writeContentReferences(content, writer);

        writeFiles(content, file, writer);
    }

    private void endFile(Writer writer) throws IOException {
        writer.close();
    }

    private Writer startFile(File file) throws UnsupportedEncodingException, FileNotFoundException {
        Writer writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        return writer;
    }

    private void writeMajor(ContentRead content, Writer writer) throws IOException {
        Major major = Major.getMajor(content.getContentId().getMajor());
        if (major != Major.UNKNOWN) {
            writeln(writer, MAJOR_PREFIX + SEPARATOR_CHAR + major.getName());
        }
    }

    private void writeId(ContentRead content, Writer writer) throws CMException, IOException, ExportException {
        writeln(writer, ID_PREFIX + SEPARATOR_CHAR + escape(getId(content)));
    }

    private String getId(ContentRead content) {
        return externalIdGenerator.generateExternalId(content);
    }

    private void writeFiles(ContentRead content, File file, Writer writer) throws CMException, IOException,
        FileNotFoundException {
        ContentFileInfo[] contentFiles = content.listFiles("/", true);

        for (ContentFileInfo contentFile : contentFiles) {
            if (contentFile.isDirectory()) {
                continue;
            }

            File outputFile = new File(file.getParent(), getId(content) + "." + contentFile.getName());

            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);

            content.exportFile(contentFile.getPath(), fileOutputStream);

            fileOutputStream.close();

            writeln(writer,
                    FILE_PREFIX + SEPARATOR_CHAR + contentFile.getPath() + SEPARATOR_CHAR + outputFile.getName());
        }
    }

    private void writeContentReferences(ContentRead content, Writer writer) throws CMException, IOException,
        ExportException {
        String[] groups = content.getContentReferenceGroupNames();

        Arrays.sort(groups);

        for (String group : groups) {
            String[] names = content.getContentReferenceNames(group);

            if (isContentList(names)) {
                ContentList contentList = content.getContentList(group);

                int size = contentList.size();

                for (int i = 0; i < size; i++) {
                    ContentReference entry = contentList.getEntry(i);
                    ContentId rmd = entry.getReferenceMetaDataId();

                    try {
                        if (rmd != null) {
                            writeln(writer, LIST_PREFIX + SEPARATOR_CHAR + escape(group) + SEPARATOR_CHAR
                                            + escape(toContentId(content, entry.getReferredContentId()))
                                            + SEPARATOR_CHAR + escape(toContentId(content, rmd)));
                        } else {
                            writeln(writer, LIST_PREFIX + SEPARATOR_CHAR + escape(group) + SEPARATOR_CHAR
                                            + escape(toContentId(content, entry.getReferredContentId())));
                        }
                    } catch (NotExportableException e) {
                        System.out.println("Skipped reference to " + entry.getReferredContentId().getContentIdString()
                                           + " in " + group + " in " + content.getContentId().getContentIdString()
                                           + " because " + e.toString());

                    }
                }
            } else {
                Arrays.sort(names);

                for (String name : names) {
                    if (group.equals(ServerNames.CONTENT_ATTRG_SYSTEM)
                        && name.equals(ServerNames.CONTENT_ATTR_INPUT_TEMPLATEID)) {
                        continue;
                    }

                    try {
                        writeln(writer,
                                REFERENCE_PREFIX + SEPARATOR_CHAR + escape(group) + ':' + escape(name) + SEPARATOR_CHAR
                                    + escape(toContentId(content, content.getContentReference(group, name))));
                    } catch (NotExportableException e) {
                    }
                }
            }
        }
    }

    private boolean isContentList(String[] names) {
        for (String name : names) {
            try {
                Integer.parseInt(name);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return true;
    }

    private void writeComponents(ContentRead content, Writer writer) throws CMException, IOException {
        String[] groups = content.getComponentGroupNames();

        Arrays.sort(groups);

        for (String group : groups) {
            String[] names = content.getComponentNames(group);

            Arrays.sort(names);

            for (String name : names) {
                // skip name.
                if (group.equals(CONTENT_ATTRG_SYSTEM) && name.equals(CONTENT_ATTR_NAME)) {
                    continue;
                }

                String value = content.getComponent(group, name);

                writeln(writer, COMPONENT_PREFIX + SEPARATOR_CHAR + escape(group) + SEPARATOR_CHAR + escape(name)
                                + SEPARATOR_CHAR + escape(value));
            }
        }
    }

    private void writeSecurityParent(ContentRead content, Writer writer) throws IOException, ExportException {
        ContentId securityParentId = content.getSecurityParentId();

        if (securityParentId != null) {
            try {
                writeln(writer, SECURITY_PARENT_PREFIX + SEPARATOR_CHAR
                                + escape(toContentId(content, securityParentId)));
            } catch (NotExportableException e) {
            }
        }
    }

    private void writeName(ContentRead content, Writer writer) throws IOException, ExportException, CMException {
        writeln(writer, INPUT_TEMPLATE_PREFIX + SEPARATOR_CHAR + getInputTemplateName(content));

        String contentName = content.getComponent(CONTENT_ATTRG_SYSTEM, CONTENT_ATTR_NAME);

        if (contentName != null) {
            writeln(writer, NAME_PREFIX + SEPARATOR_CHAR + escape(contentName));
        }
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace(":", "\\:").replace("\n", "\\n").replace("\r", "");
    }

    private void writeln(Writer writer) throws IOException {
        writer.write("\n");
    }

    private void writeln(Writer writer, String string) throws IOException {
        writer.write(string);
        writeln(writer);
    }

    private String toContentId(ContentRead inContent, ContentId contentId) throws ExportException,
        NotExportableException {
        try {
            if (filter != null && !filter.isAllowed(inContent, contentId)) {
                throw new NotExportableException("Not allowed by filter.");
            }

            ContentRead referredContent = server.getContent(contentId);

            return getId(referredContent);
        } catch (CMException e) {
            throw new ExportException("Getting external ID of " + contentId.getContentIdString() + ":  "
                                      + e.getMessage(), e);
        }
    }

    private String toString(ContentRead content) {
        return content.getContentId().getContentId().getContentIdString();
    }

    private String getInputTemplateName(ContentRead content) throws ExportException {
        try {
            ContentRead inputTemplate = server.getContent(content.getInputTemplateId());

            ExternalContentId externalId = inputTemplate.getExternalId();

            if (externalId == null) {
                throw new ExportException("Input template " + toString(inputTemplate) + " had no external ID.");
            }

            return externalId.getExternalId();
        } catch (CMException e) {
            throw new ExportException("Getting input template of " + toString(content) + ": " + e.getMessage(), e);
        }
    }
}
