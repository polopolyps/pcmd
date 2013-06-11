package com.polopoly.ps.pcmd.tool;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.polopoly.ps.contentimporter.hotdeploy.file.DeploymentFile;
import com.polopoly.ps.contentimporter.hotdeploy.file.FileDeploymentFile;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.Arguments;
import com.polopoly.ps.pcmd.argument.ParameterHelp;
import com.polopoly.ps.pcmd.argument.Parameters;
import com.polopoly.ps.pcmd.argument.RestartableIterator;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.FetchingIterator;

public class ImportParameters implements Parameters {

    private LinkedHashSet<URL> resources;
    private boolean isZipFile = false;
    private Set<String> zipFiles;
    private boolean hasImportOrder = false;
    private List<DeploymentFile> files;
    private List<List<DeploymentFile>> partialOrder;

    public List<DeploymentFile> getFiles() {
        return files;
    }

    public void setFiles(List<DeploymentFile> files) {
        this.files = files;
    }

    public List<List<DeploymentFile>> getPartialOrder() {
        return partialOrder;
    }

    public void setPartialOrder(List<List<DeploymentFile>> partialOrder) {
        this.partialOrder = partialOrder;
    }

    public Set<String> getZipFiles() {
        return zipFiles;
    }

    public void setZipFiles(Set<String> zipFiles) {
        this.zipFiles = zipFiles;
    }

    @Override
    public void parseParameters(Arguments args, PolopolyContext context) throws ArgumentException {
        resources = new LinkedHashSet<URL>();
        zipFiles = new LinkedHashSet<String>();
        files = new ArrayList<DeploymentFile>();
        partialOrder = new ArrayList<List<DeploymentFile>>();

        if (args.getArgumentCount() > 0) {
            for (int i = 0; i < args.getArgumentCount(); i++) {
                String arg = args.getArgument(i);
                File file = new File(arg);
                listFilesInDirectory(file);
            }
        } else {
            Iterable<String> fileDirPathIterable = new Iterable<String>() {
                List<String> emptyList = Collections.emptyList();

                Iterable<String> stdInIterable = new RestartableIterator<String>(getStdInFileDirPath());

                public Iterator<String> iterator() {

                    return stdInIterable.iterator();

                }
            };

            for (String arg : fileDirPathIterable) {
                File file = new File(arg);
                listFilesInDirectory(file);
            }

        }

    }

    @Override
    public void getHelp(ParameterHelp help) {
        String arg1 =
            "A directory or file, optionally followed by a colon and the name of the JAR file the directory will be packed into (e.g. \"project/target/classes:project-1.0.jar\"."
                + "\n\tDirectories or files to import. If a directory, an _import_order file would be expected in it. If not, all files in the directory will be imported.";
        help.setArguments(null, arg1);
    }

    public LinkedHashSet<URL> getResources() {
        return resources;
    }

    public void setResources(LinkedHashSet<URL> resources) {
        this.resources = resources;
    }

    public void listFilesInDirectory(File file) {
        String fileName = file.getName();

        if (file.isDirectory()) {
            for (File fileEntry : file.listFiles()) {
                if (fileEntry.getName().contains("_import_order")) {
                    hasImportOrder = true;
                    List<DeploymentFile> importFiles = new ArrayList<DeploymentFile>();

                    try {
                        FileInputStream fstream = new FileInputStream(fileEntry);
                        DataInputStream in = new DataInputStream(fstream);
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        String strLine;
                        String currentDir = fileEntry.getParent();

                        while ((strLine = br.readLine()) != null) {
                            FileDeploymentFile deploymentFile =
                                new FileDeploymentFile(new File(currentDir + File.separator + strLine));
                            importFiles.add(deploymentFile);

                            resources.add(new File(currentDir + File.separator + strLine).toURI().toURL());
                        }

                        in.close();
                        partialOrder.add(importFiles);
                    } catch (Exception e) {
                        System.err.println("Error: " + e.getMessage());
                    }

                }
            }

            for (File fileEntry : file.listFiles()) {
                listFilesInDirectory(fileEntry);
            }
        } else {
            if (fileName.endsWith(".zip") || fileName.endsWith(".jar")) {
                setZipFile(true);
                zipFiles.add(file.getAbsolutePath());
            }

            try {
                if (!fileName.contains("_import_order")) {
                    FileDeploymentFile deploymentFile = new FileDeploymentFile(file);
                    files.add(deploymentFile);
                    resources.add(file.toURI().toURL());
                }
            } catch (MalformedURLException e) {
                System.err.println("Unable parse URL " + file.getName() + ": " + e.getMessage());
            }
        }
    }

    public boolean isZipFile() {
        return isZipFile;
    }

    public void setZipFile(boolean isZipFile) {
        this.isZipFile = isZipFile;
    }

    private Iterator<String> stdInFileDirPathIterator = new FetchingIterator<String>() {
        private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        private boolean first = true;

        @Override
        protected String fetch() {
            if (first) {
                System.err.println("Reading file/directory path from standard input...");
                first = false;
            }

            try {
                String readLine = reader.readLine();

                if (readLine == null) {
                    return null;
                }

                if (readLine.trim().length() == 0) {
                    return fetch();
                }

                return readLine;
            } catch (IOException e) {
                System.err.println(e.toString());

                return null;
            }
        }
    };

    public Iterator<String> getStdInFileDirPath() {
        return stdInFileDirPathIterator;
    }

    public boolean hasImportOrder() {
        return hasImportOrder;
    }

    public void setHasImportOrder(boolean hasImportOrder) {
        this.hasImportOrder = hasImportOrder;
    }
}
