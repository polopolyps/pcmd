package com.polopoly.ps.pcmd.xml.normalize;

import static com.polopoly.ps.pcmd.client.Major.INPUT_TEMPLATE;

import java.io.File;

import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.cm.xml.util.export.ExternalIdGenerator;
import com.polopoly.ps.pcmd.client.Major;
import com.polopoly.ps.pcmd.tool.xml.export.PreserveExistingPrefixOthersExternalIdGenerator;

public class DefaultNormalizationNamingStrategy implements NormalizationNamingStrategy {
    public static final String CONTENT_DIRECTORY = "content";

    private static final String SYSTEM_TEMPLATE_PREFIX = "p.";

    private File contentDirectory;
    private File templateDirectory;

    private String extension;

    protected PolicyCMServer server;

    protected ExternalIdGenerator externalIdGenerator;

    public DefaultNormalizationNamingStrategy(PolicyCMServer server, ExternalIdGenerator externalIdGenerator,
                                              File directory, String extension) {
        templateDirectory = directory;
        this.extension = extension;
        this.server = server;
        this.externalIdGenerator = externalIdGenerator;

        contentDirectory = new File(directory, CONTENT_DIRECTORY);
        mkdir(contentDirectory);
    }

    public DefaultNormalizationNamingStrategy(PolicyCMServer server, File directory, String extension) {
        this(server, new PreserveExistingPrefixOthersExternalIdGenerator(server, "export."), directory, extension);
    }

    private static void mkdir(File directory) {
        if (!directory.exists()) {
            if (!directory.mkdir()) {
                System.err.println("Could not create directory " + directory.getAbsolutePath());
                System.exit(1);
            }
        }
    }

    public File getFileName(ContentRead content) {
        String externalId = externalIdGenerator.generateExternalId(content);

        String inputTemplateName = getInputTemplateName(content);

        return getFileName(Major.getMajor(content.getContentId().getMajor()), externalId, inputTemplateName);
    }

    private String getInputTemplateName(ContentRead content) {
        try {
            ContentRead inputTemplate = server.getContent(content.getInputTemplateId());

            String inputTemplateName = inputTemplate.getExternalId().getExternalId();

            if (inputTemplateName == null) {
                return "Unknown";
            }

            return inputTemplateName;
        } catch (CMException e) {
            // swallow exception.
            return "Unknown";
        }
    }

    public File getFileName(Major major, String externalId, String inputTemplate) {
        if (major == INPUT_TEMPLATE) {
            return getTemplateFileName(externalId);
        } else {
            return getContentFileName(externalId, inputTemplate);
        }
    }

    private File getContentFileName(String externalId, String inputTemplate) {
        File directory;

        if (inputTemplate != null && !inputTemplate.equals("")) {
            directory = new File(contentDirectory, inputTemplate);

            mkdir(directory);
        } else {
            directory = contentDirectory;
        }

        return toSafeFile(directory, externalId + '.' + extension);
    }

    private File getTemplateFileName(String externalId) {
        File directory = templateDirectory;

        if (externalId.startsWith(SYSTEM_TEMPLATE_PREFIX)) {
            directory = new File(directory, "system");

            mkdir(directory);
        }

        return toSafeFile(directory, externalId + '.' + extension);
    }

    private File toSafeFile(File directory, String unsafeFileName) {
        StringBuffer safeFileName = new StringBuffer(unsafeFileName);

        for (int i = 0; i < safeFileName.length(); i++) {
            char ch = safeFileName.charAt(i);

            // maybe we should encode these instead somehow? What could happen
            // is if there are two external IDs such as "kommun.håbo" and
            // "kommun.habo": they will be written two the same file.
            if (ch == ':') {
                ch = '.';
            } else if (ch == '/') {
                ch = '.';
            } else if (ch == '\\') {
                ch = '.';
            } else if (ch == 'ä') {
                ch = 'a';
            } else if (ch == 'ö') {
                ch = 'o';
            } else if (ch == 'å') {
                ch = 'a';
            } else if (ch == 'ü') {
                ch = 'u';
            } else if (ch == 'Ä') {
                ch = 'A';
            } else if (ch == 'Ö') {
                ch = 'O';
            } else if (ch == 'Å') {
                ch = 'A';
            } else if (ch == 'Ü') {
                ch = 'U';
            } else if (ch > 127) {
                ch = '.';
            }

            safeFileName.setCharAt(i, ch);
        }

        return new File(directory, safeFileName.toString());

    }
}
