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