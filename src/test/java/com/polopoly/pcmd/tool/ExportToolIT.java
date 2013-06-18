package com.polopoly.pcmd.tool;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.geronimo.mail.util.StringBufferOutputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.common.lang.StringUtil;
import com.polopoly.ps.pcmd.Main;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.DefaultArguments;
import com.polopoly.ps.pcmd.tool.ExportTool;
import com.polopoly.testbase.ImportTestContent;
import com.polopoly.user.server.UserServer;
import com.polopoly.util.client.PolopolyContext;

/**
 * filterreferences by default is true - will cause scanning of random paths
 * based on the user.dir used in test cases here.. and there's a possibility of
 * getting unwanted externalid or java null pointer exceptions that will cause
 * errors. as such, all filterreferences for the test case have been set to
 * false
 * 
 */
@ImportTestContent(files = "com.polopoly.pcmd.tool.ExportToolIT.xml")
public class ExportToolIT extends AbstractIntegrationTestBase {

    private PolopolyContext context;

    static List<Map<String, Map<String, String>>> files = new ArrayList<Map<String, Map<String, String>>>();

    @Inject
    private UserServer userServer;

    @Inject
    private PolicyCMServer cmServer;

    private String defaultVideo = ExportToolIT.class.getName() + ".video";
    private String filterRefOffArticle = ExportToolIT.class.getName() + ".article";
    private String dotContentArticle = ExportToolIT.class.getName() + ".article2";
    private String projectContentArticle = ExportToolIT.class.getName() + ".article3";
    private String defaultPolopolySiteTemplate = "p.siteengine.Site";
    private String defaultPolopolyPageTemplate = "p.siteengine.Page";

    @Before
    public void setup() throws CMException, ArgumentException {
        context = new PolopolyContext(userServer, cmServer);
    }

    @After
    public void cleanUpFiles() throws CMException {
        String currentDir = System.getProperty("user.dir");

        for (Map<String, Map<String, String>> entry : files) {

            for (Entry<String, Map<String, String>> entryDetail : entry.entrySet()) {
                String externalId = entryDetail.getKey();
                String filePath = entryDetail.getValue().get("filePath");
                String contentDir = currentDir + "/" + entryDetail.getValue().get("contentDir");

                try {
                    File file = new File(filePath);
                    FileDeleteStrategy.FORCE.delete(file);

                    if (!isPolopolyTemplate(externalId)) {
                        File folderDir = new File(filePath.substring(0, filePath.lastIndexOf("/")));
                        if (folderDir.exists() && folderDir.list().length == 0) {
                            FileDeleteStrategy.FORCE.delete(folderDir);
                        }
                    }

                    File contentDirFile = new File(contentDir);
                    if (contentDirFile.exists() && contentDirFile.list().length == 0) {
                        FileDeleteStrategy.FORCE.delete(contentDirFile);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isPolopolyTemplate(String fileName) {
        if (fileName.startsWith("p.")) {
            return true;
        }
        return false;
    }

    private void exportFile(String externalId, String optionKey, String optionValue) throws ArgumentException,
        CMException {

        List<String> args = new ArrayList<String>();
        String currentDir = System.getProperty("user.dir");
        args.add(currentDir);
        args.add(externalId);

        Map<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("filterreferences", Arrays.asList("false"));

        if (!StringUtil.isEmpty(optionKey) && !StringUtil.isEmpty(optionValue))
            options.put(optionKey, Arrays.asList(optionValue));

        DefaultArguments arguments = new DefaultArguments("export", options, args);
        arguments.setContext(context);

        Main.execute(new ExportTool(), context, arguments);
    }

    @Test
    public void testDefaultExport() throws CMException, IOException, ArgumentException {
    	exportFile(defaultVideo, "", "");

        Map<String, String> fileDetail = getFile(defaultVideo, "content", false);
        File file = new File(fileDetail.get("filePath"));

        assertTrue(file.exists() && file.isFile());
        assertTrue(file.getPath().endsWith(".xml"));
    }

    @Test
    public void testFilterReferencesTurnOff() throws CMException, IOException, ArgumentException {
    	exportFile(filterRefOffArticle, "", "");

        Map<String, String> fileDetail = getFile(filterRefOffArticle, "content", false);
        File file = new File(fileDetail.get("filePath"));

        String content = getFileContent(file);

        assertNotNull(content);
        assertTrue(content.contains("<externalid>export.7."));
    }

    @Test
    public void testTextFormat() throws CMException, IOException, ArgumentException {
    	exportFile(dotContentArticle, "textformat", "true");

        Map<String, String> fileDetail = getFile(dotContentArticle, "content", true);

        File file = new File(fileDetail.get("filePath"));
        assertTrue(file.exists() && file.isFile());
        assertTrue(file.getPath().endsWith(".content"));
    }

    @Test
    public void testProjectContent() throws ArgumentException, CMException, IOException {
    	exportFile(projectContentArticle, "", "");
    	
        StringBuffer err = new StringBuffer();
        System.setErr(new PrintStream(new StringBufferOutputStream(err)));

        List<String> args = new ArrayList<String>();
        String currentDir = System.getProperty("user.dir");
        args.add(currentDir);
        args.add(projectContentArticle);

        Map<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("projectcontent", Arrays.asList(currentDir + "/content"));
        options.put("filterreferences", Arrays.asList("false"));

        DefaultArguments arguments = new DefaultArguments("export", options, args);
        arguments.setContext(context);

        Main.execute(new ExportTool(), context, arguments);

        assertTrue(err.toString().contains("Skipping " + projectContentArticle
                                               + " since it is part of project or product content"));

        Map<String, String> fileDetail = getFile(projectContentArticle, "content", false);
        File file = new File(fileDetail.get("filePath"));

        assertTrue(file.isFile());
    }
    
    @Test
    public void testExportPresentTurnedOn() throws IOException, CMException, ArgumentException {
    	exportFile(defaultPolopolySiteTemplate, "exportpresent", "true");
    	 
        Map<String, String> fileDetail = getFile(defaultPolopolySiteTemplate, "system", false);

        File file = new File(fileDetail.get("filePath"));
        assertTrue(file.exists() && file.isFile());
    }

    @Test
    public void testExportPresentTurnedOff() throws IOException, CMException, ArgumentException {
    	exportFile(defaultPolopolyPageTemplate, "exportpresent", "false");
    	 
        Map<String, String> fileDetail = getFile(defaultPolopolyPageTemplate, "system", false);

        File file = new File(fileDetail.get("filePath"));
        assertFalse(file.exists());
    }

    private Map<String, String> getFile(String externalId, String contentType, boolean isText) throws CMException,
        IOException {
        Map<String, String> fileDetails = new HashMap<String, String>();
        Map<String, Map<String, String>> fileObjects = new HashMap<String, Map<String, String>>();

        String currentDir = System.getProperty("user.dir");
        String templateName = cmServer.getPolicy(new ExternalContentId(externalId)).getInputTemplate().getName();
        String fileName = externalId + (isText ? ".content" : ".xml");

        String filePath = "";

        if (contentType.equals("content"))
            filePath = currentDir + "/" + contentType + "/" + templateName + "/" + fileName;
        else
            filePath = currentDir + "/" + contentType + "/" + fileName;

        fileDetails.put("fileName", fileName);
        fileDetails.put("filePath", filePath);
        fileDetails.put("externalId", externalId);
        fileDetails.put("contentDir", contentType.equals("content") ? "content" : "system");

        fileObjects.put(externalId, fileDetails);

        if (!files.contains(fileObjects)) {
            files.add(fileObjects);
        }

        return fileDetails;
    }

    private String getFileContent(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String content = null;
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            content = sb.toString();
        } finally {
            br.close();
        }

        return content;
    }
}
