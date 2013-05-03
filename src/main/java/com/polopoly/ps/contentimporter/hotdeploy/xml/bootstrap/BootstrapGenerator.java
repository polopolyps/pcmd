package com.polopoly.ps.contentimporter.hotdeploy.xml.bootstrap;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.polopoly.ps.contentimporter.hotdeploy.file.DeploymentFile;
import com.polopoly.ps.contentimporter.hotdeploy.xml.bootstrap.Bootstrap;
import com.polopoly.ps.contentimporter.hotdeploy.xml.bootstrap.BootstrapContent;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.DeploymentFileParser;

public class BootstrapGenerator
{
    private DeploymentFileParser parser;

    public BootstrapGenerator(final DeploymentFileParser parser)
    {
        this.parser = parser;
    }

    public PhaseOneBootstrap generateTemplateBootstrap(final List<DeploymentFile> files)
    {
        TemplateGatherer gatherer = new TemplateGatherer();
        PhaseOneBootstrap bootstrap = new PhaseOneBootstrap();

        generateBootstrap(files, gatherer, bootstrap);
        bootstrap.setDefinedTemplateExternalIds(gatherer.getDefinedTemplateExternalIds());

        return bootstrap;
    }

    public Bootstrap generateContentBootstrap(final List<DeploymentFile> files,
                                              final Set<String> seenDefinitions)
    {
        BootstrapGatherer gatherer = new ContentGatherer(seenDefinitions);
        return generateBootstrap(files, gatherer, new Bootstrap());
    }

    private Bootstrap generateBootstrap(final List<DeploymentFile> files,
                                        final BootstrapGatherer gatherer,
                                        final Bootstrap result)
    {
        for (DeploymentFile deploymentFile : files) {
            parser.parse(deploymentFile, gatherer);
        }

        Iterator<BootstrapContent> bootstrapContentIterator = gatherer.getBootstrapContent().iterator();

        while (bootstrapContentIterator.hasNext()) {
            BootstrapContent bootstrapContent = bootstrapContentIterator.next();

            // we don't bootstrap content that is never defined (these are either system templates
            // or errors), so remove those from the bootstrap.

            if (gatherer.isDefined(bootstrapContent.getExternalId())) {
                result.add(bootstrapContent);
            } else {
                result.addNeverCreatedButReferenced(bootstrapContent);
            }
        }

        return result;
    }
}
