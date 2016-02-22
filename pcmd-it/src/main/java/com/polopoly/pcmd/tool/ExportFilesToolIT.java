package com.polopoly.pcmd.tool;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileDeleteStrategy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.polopoly.cm.ContentFileInfo;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.ps.pcmd.Main;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.DefaultArguments;
import com.polopoly.ps.pcmd.tool.ExportFilesTool;
import com.polopoly.testbase.ImportTestContent;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.content.ContentUtil;
import com.polopoly.util.policy.Util;

@ImportTestContent(files = "com.polopoly.pcmd.tool.ExportFilesToolIT.xml")
public class ExportFilesToolIT extends AbstractIntegrationTestBase {

    private PolopolyContext context;

    List<String> filesDir = new ArrayList<String>();

    @Inject
    private PolicyCMServer cmServer;

    private String imageExternalId = ExportFilesToolIT.class.getName() + ".image";
    private String emptyImageExternalId = ExportFilesToolIT.class.getName() + ".emptyimage";
    private String docExternalId = ExportFilesToolIT.class.getName() + ".docx";
    private String videoExternalId = ExportFilesToolIT.class.getName() + ".video";

    @Before
    public void setup() throws CMException, ArgumentException {
        context = new PolopolyContext(cmServer);

        exportFile(imageExternalId); // image
        exportFile(docExternalId); // files
        exportFile(emptyImageExternalId); // empty image
        exportFile(videoExternalId); // video
    }

    @After
    public void cleanUpFiles() {
        for (String filedir : filesDir) {
            String path = System.getProperty("user.dir") + "/" + filedir;

            File tempFile = new File(path);
            try {
                FileDeleteStrategy.FORCE.delete(tempFile);
                System.out.println(path + " removed...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void exportFile(String externalId) throws ArgumentException, CMException {

        List<String> args = new ArrayList<String>();
        args.add(externalId);

        DefaultArguments arguments = new DefaultArguments("ExportFiles", new HashMap<String, List<String>>(), args);
        arguments.setContext(context);

        Main.execute(new ExportFilesTool(), context, arguments);
    }

    @Test
    public void fileCheckTest() throws CMException, IOException {

        File imageFile = getFile(imageExternalId);
        System.out.println("image check...");
        assertTrue(imageFile.exists() && imageFile.isFile());

        File emptyImageFile = getFile(emptyImageExternalId);
        System.out.println("empty image check...");
        assertFalse(emptyImageFile.isFile());

        File otherFiles = getFile(docExternalId);
        System.out.println("file check...");
        assertTrue(otherFiles.exists() && otherFiles.isFile());

        File videoFile = getFile(videoExternalId);
        System.out.println("video file check...");
        assertTrue(videoFile.exists() && videoFile.isFile());
    }

    private File getFile(String externalId) throws CMException, IOException {

        ContentRead contentRead = cmServer.getContent(new ExternalContentId(externalId));
        ContentUtil content = Util.util(contentRead, cmServer);

        ContentFileInfo[] files = content.listFiles("/", true);

        String filePath = null;
        for (ContentFileInfo file : files) {
            filePath = file.getDirectory() + "/" + file.getName();
        }

        String currentDir = System.getProperty("user.dir");
        filePath = currentDir + "/" + externalId + "/" + filePath;

        filesDir.add(externalId);

        File file = new File(filePath);
        return file;
    }
}
