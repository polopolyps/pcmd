package com.polopoly.ps.pcmd.tool.export;

import static com.polopoly.ps.pcmd.util.Plural.count;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.TransformerFactoryConfigurationError;

import com.polopoly.cm.ContentId;
import com.polopoly.ps.pcmd.field.content.AbstractContentIdField;
import com.polopoly.ps.pcmd.xml.export.SingleContentToFileExporter;
import com.polopoly.ps.pcmd.xml.normalize.NormalizationNamingStrategy;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.ContentIdToContentUtilIterator;
import com.polopoly.util.content.ContentUtil;

/**
 * Same as {@link com.polopoly.ps.hotdeploy.xml.export.NormalizedFileExporter},
 * but logs in a way adapted to PCMD.
 */
public class NormalizedFileExporter {
    private static final Logger logger = Logger.getLogger(NormalizedFileExporter.class.getName());

    private NormalizationNamingStrategy namingStrategy;

    private SingleContentToFileExporter singleFileExporter;

    private PolopolyContext context;

    private int exportedCount = 0;

    public NormalizedFileExporter(PolopolyContext context, SingleContentToFileExporter singleContentToFileExporter,
                                  NormalizationNamingStrategy namingStrategy) {
        this.context = context;
        this.namingStrategy = namingStrategy;
        this.singleFileExporter = singleContentToFileExporter;
    }

    public void export(Set<ContentId> contentIdsToExport) {
        ContentIdToContentUtilIterator contentToExportIterator =
            new ContentIdToContentUtilIterator(context, contentIdsToExport.iterator(), false);

        // We might need to change this to IDs rather than content objects
        // sometime to save memory.
        // but for up to a few thousand objects this should be fine.
        Map<File, List<ContentUtil>> contentByFile = new LinkedHashMap<File, List<ContentUtil>>(500);

        while (contentToExportIterator.hasNext()) {
            ContentUtil content = contentToExportIterator.next();

            add(contentByFile, content);
        }

        for (Entry<File, List<ContentUtil>> entry : contentByFile.entrySet()) {
            File file = entry.getKey();
            List<ContentUtil> contents = entry.getValue();

            exportSingleFile(file, contents);

            logExported(contents);
        }

        printStatus(exportedCount);
    }

    private void logExported(List<ContentUtil> contents) {
        for (ContentUtil content : contents) {
            System.out.println(AbstractContentIdField.get(content.getContentId().getContentId(), context));
        }

        if (++exportedCount % 100 == 0) {
            printStatus(exportedCount);
        }
    }

    private void add(Map<File, List<ContentUtil>> contentByFile, ContentUtil content) {
        File file = namingStrategy.getFileName(content);

        List<ContentUtil> contentList = contentByFile.get(file);

        if (contentList == null) {
            contentList = new ArrayList<ContentUtil>(1);
            contentByFile.put(file, contentList);
        }

        contentList.add(content);
    }

    private void exportSingleFile(File file, List<ContentUtil> contents) throws TransformerFactoryConfigurationError {
        try {
            singleFileExporter.exportContentToFile(contents, file);
        } catch (Exception e) {
            logger.log(Level.WARNING, "While exporting " + contents + " to " + file + ": " + e.getMessage(), e);
        }
    }

    private void printStatus(int exportedCount) {
        System.err.println("Exported " + count(exportedCount, "object") + "...");
    }
}
