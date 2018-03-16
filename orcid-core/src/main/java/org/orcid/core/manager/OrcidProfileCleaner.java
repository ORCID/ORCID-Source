package org.orcid.core.manager;

public interface OrcidProfileCleaner {

    /**
     * Removes objects that have a blank value or content property.
     */
    public void clean(Object object);

}
