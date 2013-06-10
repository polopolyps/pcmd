package com.polopoly.ps.contentimporter.hotdeploy.xml.bootstrap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.ps.contentimporter.hotdeploy.client.Major;
import com.polopoly.ps.contentimporter.hotdeploy.file.DeploymentFile;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.ParseContext;

public class ContentGatherer implements BootstrapGatherer {
    private static final Logger logger = Logger.getLogger(ContentGatherer.class.getName());

    private Set<String> definedExternalIds;
    private Map<String, BootstrapContent> bootstrapByExternalId = new HashMap<String, BootstrapContent>();

    public ContentGatherer(final Set<String> definedExternalIds) {
        this.definedExternalIds = definedExternalIds;
    }

    public ContentGatherer() {
        this(new HashSet<String>());
    }

    public void classReferenceFound(final DeploymentFile file, final String string) {
    }

    private void resolve(final String externalId, final Major major, final String inputTemplate) {
        BootstrapContent bootstrapContent = bootstrapByExternalId.get(externalId);

        if (bootstrapContent != null) {
            if (bootstrapContent.getMajor() == Major.UNKNOWN) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, "We now know the major of " + externalId + ": " + major + ".");
                }
                System.out.println("We now know the major of " + externalId + ": " + major + ".");
            }

            bootstrapContent.setMajor(major);
            if (inputTemplate != null) {
                bootstrapContent.setInputTemplate(inputTemplate);
            }
        }
    }

    public void contentFound(final ParseContext context, final String externalId, final Major major,
                             final String inputTemplate) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Found content " + externalId + " of major " + major + " in " + context.getFile()
                                   + ".");
        }
        System.out.println("Found content " + externalId + " of major " + major + " in " + context.getFile() + ".");

        definedExternalIds.add(externalId);

        // this content might been have referenced before, so we will need to
        // bootstrap it.
        // however, we might not have known the major before, but now we do.

        resolve(externalId, major, inputTemplate);
    }

    public void contentReferenceFound(final ParseContext context, final Major major, final String externalId) {
        if (isNotYetDefined(externalId)) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE,
                           "Found reference to " + externalId + " of major " + major + " in " + context.getFile()
                               + " which needs bootstrapping.");
            }
            System.out.println("Found reference to " + externalId + " of major " + major + " in " + context.getFile()
                               + " which needs bootstrapping.");

            bootstrap(context.getFile(), major, externalId);
        } else if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINER,
                       "Found reference to " + externalId + " of major " + major + " in " + context.getFile()
                           + " which has been defined and therefore needs no bootstrapping.");
            System.out.println("Found reference to " + externalId + " of major " + major + " in " + context.getFile()
                               + " which has been defined and therefore needs no bootstrapping.");
        }
    }

    private void bootstrap(final DeploymentFile file, final Major major, final String externalId) {
        if (externalId.equals("")) {
            logger.log(Level.WARNING, "Attempt to bootstrap an empty external ID (major " + major + ") in file " + file
                                      + ".");
            System.out.println("Attempt to bootstrap an empty external ID (major " + major + ") in file " + file + ".");
            return;
        }

        BootstrapContent existingBootstrap = bootstrapByExternalId.get(externalId);

        if (existingBootstrap != null) {
            if (isDisagreeing(major, existingBootstrap)) {
                logger.log(Level.WARNING, "The major of " + externalId + " is unclear: it might be "
                                          + existingBootstrap.getMajor() + " or " + major + ".");
                System.out.println("The major of " + externalId + " is unclear: it might be "
                                   + existingBootstrap.getMajor() + " or " + major + ".");
            }

            if (existingBootstrap.getMajor() == Major.UNKNOWN) {
                existingBootstrap.setMajor(major);
            }
        } else {
            bootstrapByExternalId.put(externalId, new BootstrapContent(major, externalId));
        }
    }

    private boolean isDisagreeing(final Major aMajor, final BootstrapContent anotherMajor) {
        return anotherMajor.getMajor() != aMajor && aMajor != Major.UNKNOWN && anotherMajor.getMajor() != Major.UNKNOWN;
    }

    private boolean isNotYetDefined(final String externalId) {
        return !definedExternalIds.contains(externalId);
    }

    public Iterable<BootstrapContent> getBootstrapContent() {
        return bootstrapByExternalId.values();
    }

    public boolean isDefined(final String externalId) {
        return definedExternalIds.contains(externalId);
    }
}
