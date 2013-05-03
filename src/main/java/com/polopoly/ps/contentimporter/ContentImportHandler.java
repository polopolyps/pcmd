package com.polopoly.ps.contentimporter;

import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Handles actual content imports to the Polopoly system available through the
 * {@link TestApplication}
 * 
 */
public interface ContentImportHandler {
    public void importContent(Set<URL> resources);

    public void importContentByImportOrder(LinkedHashSet<URL> resources);
}
