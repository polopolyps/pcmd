package com.polopoly.ps.contentimporter.hotdeploy.xml.bootstrap;

import java.io.File;
import java.util.List;
import java.util.ListIterator;

import com.polopoly.ps.contentimporter.hotdeploy.discovery.importorder.ImportOrder;
import com.polopoly.ps.contentimporter.hotdeploy.file.DeploymentFile;
import com.polopoly.ps.contentimporter.hotdeploy.file.FileDeploymentDirectory;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.DeploymentFileParser;

public class TwoPhaseBootstrapper {
    public TwoPhaseBootstrapResult bootstrap(final DeploymentFileParser xmlParser, final List<DeploymentFile> files,
                                             final List<List<DeploymentFile>> partialOrder) {
        ImportOrder importOrder = new ImportOrder(new FileDeploymentDirectory(new File(".")));
        ListIterator<List<DeploymentFile>> listIterator = partialOrder.listIterator();

        while (listIterator.hasNext()) {
            importOrder.addAll(listIterator.next());
        }

        ListIterator<DeploymentFile> filesIterator = files.listIterator();

        while (filesIterator.hasNext()) {
            DeploymentFile file = filesIterator.next();
            if (!importOrder.contains(file)) {
                importOrder.add(file);
            }
        }

        BootstrapGenerator bootstrapGenerator = new BootstrapGenerator(xmlParser);

        PhaseOneBootstrap templateBootstrap = bootstrapGenerator.generateTemplateBootstrap(importOrder);
        Bootstrap contentBootstrap =
            bootstrapGenerator.generateContentBootstrap(importOrder, templateBootstrap.getDefinedTemplateExternalIds());

        return new TwoPhaseBootstrapResult(templateBootstrap, contentBootstrap, importOrder);
    }
}
