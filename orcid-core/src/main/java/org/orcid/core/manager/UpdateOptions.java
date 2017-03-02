/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
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

/**
 * 
 * @author Will Simpson
 *
 */
public class UpdateOptions {

    public static final UpdateOptions ALL = new UpdateOptions(true, true, true);
    public static final UpdateOptions NO_ACTIVITIES = new UpdateOptions(false, false, false);
    public static final UpdateOptions AFFILIATIONS_ONLY = new UpdateOptions(false, true, false);
    public static final UpdateOptions FUNDINGS_ONLY = new UpdateOptions(false, false, true);

    private boolean updateWorks;
    private boolean updateAffiliations;
    private boolean updateFundings;

    public UpdateOptions(boolean updateWorks, boolean updateAffiliations, boolean updateFundings) {
        this.updateWorks = updateWorks;
        this.updateAffiliations = updateAffiliations;
        this.updateFundings = updateFundings;
    }

    public boolean isUpdateWorks() {
        return updateWorks;
    }

    public boolean isUpdateAffiliations() {
        return updateAffiliations;
    }

    public boolean isUpdateFundings() {
        return updateFundings;
    }

}