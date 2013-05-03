package com.polopoly.ps.pcmd.tool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileDeleteStrategy;

import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.xml.DocumentImporter;
import com.polopoly.cm.xml.DocumentImporterFactory;
import com.polopoly.cm.xml.io.DispatchingDocumentImporter;
import com.polopoly.pcmd.tool.Tool;
import com.polopoly.ps.contentimporter.StandardContentImportHandler;
import com.polopoly.ps.contentimporter.hotdeploy.file.DeploymentFile;
import com.polopoly.ps.contentimporter.hotdeploy.xml.bootstrap.Bootstrap;
import com.polopoly.ps.contentimporter.hotdeploy.xml.bootstrap.BootstrapFileWriter;
import com.polopoly.ps.contentimporter.hotdeploy.xml.bootstrap.TwoPhaseBootstrapResult;
import com.polopoly.ps.contentimporter.hotdeploy.xml.bootstrap.TwoPhaseBootstrapper;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.ContentXmlParser;
import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.util.client.PolopolyContext;

public class ImportTool implements Tool<ImportParameters> {
    private LinkedHashSet<URL> resources;
    private boolean isZipFile = false;
    private Set<String> zipFiles;
    private DispatchingDocumentImporter dispatchdocimporter;
    private boolean hasImportOrder = false;
    private List<DeploymentFile> files;
    private List<List<DeploymentFile>> partialOrder;

    @Override
    public void execute(PolopolyContext context, ImportParameters parameters) throws FatalToolException {
        resources = parameters.getResources();
        isZipFile = parameters.isZipFile();
        zipFiles = parameters.getZipFiles();
        hasImportOrder = parameters.hasImportOrder();
        files = parameters.getFiles();
        partialOrder = parameters.getPartialOrder();

        if (isZipFile) {
            Iterator<String> it = zipFiles.iterator();

            try {
                dispatchdocimporter = new DispatchingDocumentImporter(context.getPolicyCMServer());
            } catch (ParserConfigurationException e) {
                System.err.println(e.getMessage());
            }

            while (it.hasNext()) {
                try {
                    String zipFilePath = it.next();

                    File file = new File(zipFilePath);
                    JarFile jarFile = new JarFile(file);
                    dispatchdocimporter.importXML(jarFile);
                    Policy[] policy = dispatchdocimporter.getImportedContent();
                    if (policy.length > 0) {
                        System.out.println("Finish import zip file");
                    }
                } catch (FileNotFoundException e) {
                    System.err.println(e.getMessage());
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }

        } else {
            if (hasImportOrder) {
                TwoPhaseBootstrapper bootstrapper = new TwoPhaseBootstrapper();
                ContentXmlParser xmlParser = new ContentXmlParser();

                TwoPhaseBootstrapResult boostrapResult = bootstrapper.bootstrap(xmlParser, files, partialOrder);
                Bootstrap boostrap = boostrapResult.getContentBootstrap();
                BootstrapFileWriter boostrapFileWriter = new BootstrapFileWriter(boostrap);
                Writer out;
                try {
                    String temporaryBootstrapFile =
                        System.getProperty("user.dir") + System.getProperty("file.separator") + "bootstrap.xml";
                    out = new FileWriter(temporaryBootstrapFile);
                    boostrapFileWriter.write(out);
                    out.close();
                    LinkedHashSet<URL> bootstrapFiles = new LinkedHashSet<URL>();
                    bootstrapFiles.add(new File(temporaryBootstrapFile).toURI().toURL());
                    DocumentImporter importer =
                        DocumentImporterFactory.getDocumentImporter(context.getPolicyCMServer());
                    StandardContentImportHandler importHandler = new StandardContentImportHandler(importer);
                    importHandler.importContent(bootstrapFiles);
                    importHandler.importContent(resources);
                    File tempFile = new File(temporaryBootstrapFile);

                    FileDeleteStrategy.FORCE.delete(tempFile);
                    System.out.println(temporaryBootstrapFile + " removed...");

                } catch (IOException e1) {
                    System.err.println(e1.getMessage());
                } catch (CMException e) {
                    System.err.println(e.getMessage());
                } catch (ParserConfigurationException e) {
                    System.err.println(e.getMessage());
                }

            } else {
                try {
                    DocumentImporter importer =
                        DocumentImporterFactory.getDocumentImporter(context.getPolicyCMServer());
                    StandardContentImportHandler importHandler = new StandardContentImportHandler(importer);
                    importHandler.importContent(resources);
                } catch (CMException e) {
                    System.err.println(e.getMessage());
                } catch (ParserConfigurationException e) {
                    System.err.println(e.getMessage());
                }
            }

        }

    }

    @Override
    public ImportParameters createParameters() {
        return new ImportParameters();
    }

    @Override
    public String getHelp() {
        return "Imports content files that have been modified since last time they were imported (or were never imported).";
    }

}
