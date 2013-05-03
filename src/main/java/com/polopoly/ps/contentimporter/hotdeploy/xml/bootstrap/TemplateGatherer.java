package com.polopoly.ps.contentimporter.hotdeploy.xml.bootstrap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.ps.contentimporter.hotdeploy.client.Major;
import com.polopoly.ps.contentimporter.hotdeploy.file.DeploymentFile;
import com.polopoly.ps.contentimporter.hotdeploy.file.TemplateDefinitionAware;
import com.polopoly.ps.contentimporter.hotdeploy.xml.bootstrap.BootstrapContent;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.ContentListWrapperAwareParseCallback;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.LayoutAwareParseCallback;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.ParseContext;

public class TemplateGatherer
    implements BootstrapGatherer, ContentListWrapperAwareParseCallback, LayoutAwareParseCallback
{
    private static final Logger logger = Logger.getLogger(TemplateGatherer.class.getName());

    private Set<String> definedTemplateExternalIds = new HashSet<String>();
    private Set<String> definedContentExternalIds = new HashSet<String>();;

    private Map<String, Major> contentMajors = new HashMap<String, Major>();
    private Map<String, BootstrapContent> bootstrapByExternalId = new HashMap<String, BootstrapContent>();

    public void classReferenceFound(final DeploymentFile file,
                                    final String string)
    {
    }

    private void resolve(final String externalId,
                         final Major major)
    {
        BootstrapContent bootstrapContent = bootstrapByExternalId.get(externalId);

        if (bootstrapContent != null) {
            if (logger.isLoggable(Level.FINE) && bootstrapContent.getMajor() == Major.UNKNOWN) {
                logger.log(Level.FINE,  "We now know the major of " + externalId + ": " + major + ".");
            }

            bootstrapContent.setMajor(major);
        }
    }

    private void resolveContentListWrapper(final String externalId,
                                           final String contentListWrapperClass)
    {
        BootstrapContent bootstrapContent = bootstrapByExternalId.get(externalId);

        if (bootstrapContent != null) {
            if (logger.isLoggable(Level.FINE) && bootstrapContent.getContentListWrapperClass() == null) {
                logger.log(Level.FINE,  "We now know that " + externalId + " is a content list wrapper; marking as such.");
            }

            bootstrapContent.setContentListWrapperClass(contentListWrapperClass);
        }
    }

    public void contentFound(final ParseContext context,
                             final String externalId,
                             final Major major,
                             final String inputTemplate)
    {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE,  "Found content " + externalId + " of major " + major + " in " + context.getFile() + ".");
        }

        if (isTemplateDefinitionFile(context)) {
            definedTemplateExternalIds.add(externalId);
        } else {
            definedContentExternalIds.add(externalId);
            contentMajors.put(externalId, major);
        }

        // this content might been have referenced before, so we will need to bootstrap it.
        // however, we might not have known the major before, but now we do.

        resolve(externalId, major);
    }

    public void contentListWrapperFound(final ParseContext context,
                                        final String externalId,
                                        final String contentListWrapperClass)
    {
        resolveContentListWrapper(externalId, contentListWrapperClass);
    }

    public void contentReferenceFound(final ParseContext context,
                                      final Major major,
                                      final String externalId)
    {
        if (isTemplateDefinitionFile(context)) {
            if (!isPreviouslyDefinedTemplate(externalId)) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE,  "Found reference to " + externalId + " of major " + major + " in " + context.getFile() + " which needs bootstrapping.");
                }

                bootstrap(context.getFile(), major, externalId);
            } else if (logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "Found reference to " + externalId + " of major " + major + " in " + context.getFile() + " which has been defined and therefore needs no bootstrapping.");
            }
        } else if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "Found reference to " + externalId + " of major " + major + " in " + context.getFile() + " which is not a template file and therefore needs no bootstrapping in phase one.");
        }
    }

    private boolean isTemplateDefinitionFile(final ParseContext context)
    {
        return context.getFile() instanceof TemplateDefinitionAware &&
                ((TemplateDefinitionAware)context.getFile()).isTemplateDefinitionFile();
    }

    private void bootstrap(final DeploymentFile file,
                           Major major,
                           final String externalId)
    {
        if (externalId.equals("")) {
            logger.log(Level.WARNING, "Attempt to bootstrap an empty external ID (major " + major + ") in file " + file + ".");
            return;
        }

        BootstrapContent existingBootstrap = bootstrapByExternalId.get(externalId);

        if (existingBootstrap != null) {
            if (isDisagreeing(major, existingBootstrap)) {
                logger.log(Level.WARNING, "The major of " + externalId + " is unclear: it might be " + existingBootstrap.getMajor() + " or " + major + ".");
            }

            if (existingBootstrap.getMajor() == Major.UNKNOWN) {
                existingBootstrap.setMajor(major);
            }
        } else {
            if (major == Major.UNKNOWN && contentMajors.containsKey(externalId)) {
                major = contentMajors.remove(externalId);
            }

            bootstrapByExternalId.put(externalId, new BootstrapContent(major, externalId));
        }
    }

    private boolean isDisagreeing(final Major aMajor,
                                  final BootstrapContent anotherMajor)
    {
        return anotherMajor.getMajor() != aMajor &&
               aMajor != Major.UNKNOWN &&
               anotherMajor.getMajor() != Major.UNKNOWN;
    }

    private boolean isPreviouslyDefinedTemplate(final String externalId)
    {
        return definedTemplateExternalIds.contains(externalId);
    }

    public Iterable<BootstrapContent> getBootstrapContent()
    {
        return bootstrapByExternalId.values();
    }

    public boolean isDefined(final String externalId)
    {
        return definedTemplateExternalIds.contains(externalId) ||
               definedContentExternalIds.contains(externalId);
    }

    public Set<String> getDefinedTemplateExternalIds()
    {
        return definedTemplateExternalIds;
    }

    public void layoutFound(final ParseContext context,
                            final String externalId,
                            final String layoutClass)
    {
        BootstrapContent bootstrapContent = bootstrapByExternalId.get(externalId);

        if (bootstrapContent != null) {
            if (logger.isLoggable(Level.FINE) && bootstrapContent.getLayoutClass() == null) {
                logger.log(Level.FINE,  "We now know that " + externalId + " is a layout element; marking as such.");
            }

            bootstrapContent.setLayoutClass(layoutClass);
        }
    }
}
