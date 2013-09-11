/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager;

public class LoadOptions {

    public static final LoadOptions ALL = new LoadOptions(true, true, true);
    public static final LoadOptions BIO_ONLY = new LoadOptions(true, false, false);
    public static final LoadOptions BIO_AND_INTERNAL_ONLY = new LoadOptions(true, false, true);
    public static final LoadOptions INTERNAL_ONLY = new LoadOptions(false, false, true);

    private boolean loadBio;
    private boolean loadActivities;
    private boolean loadInternal;

    public LoadOptions(boolean loadBio, boolean loadActivities, boolean loadInternal) {
        super();
        this.loadBio = loadBio;
        this.loadActivities = loadActivities;
        this.loadInternal = loadInternal;
    }

    public boolean isLoadBio() {
        return loadBio;
    }

    public void setLoadBio(boolean loadBio) {
        this.loadBio = loadBio;
    }

    public boolean isLoadActivities() {
        return loadActivities;
    }

    public void setLoadActivities(boolean loadActivities) {
        this.loadActivities = loadActivities;
    }

    public boolean isLoadInternal() {
        return loadInternal;
    }

    public void setLoadInternal(boolean loadInternal) {
        this.loadInternal = loadInternal;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (loadActivities ? 1231 : 1237);
        result = prime * result + (loadBio ? 1231 : 1237);
        result = prime * result + (loadInternal ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LoadOptions other = (LoadOptions) obj;
        if (loadActivities != other.loadActivities)
            return false;
        if (loadBio != other.loadBio)
            return false;
        if (loadInternal != other.loadInternal)
            return false;
        return true;
    }

}
