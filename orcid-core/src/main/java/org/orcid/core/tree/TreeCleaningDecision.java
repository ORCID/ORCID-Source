package org.orcid.core.tree;

/**
 * @author Will Simpson
 */
public class TreeCleaningDecision {

    public static final TreeCleaningDecision CLEANING_REQUIRED = new TreeCleaningDecision(true, false);
    public static final TreeCleaningDecision DEFAULT = new TreeCleaningDecision(false, true);
    public static final TreeCleaningDecision IGNORE = new TreeCleaningDecision(false, false);

    private boolean needingCleaning;

    private boolean needingPropertyChecking;

    private TreeCleaningDecision(boolean needingCleaning, boolean needingPropertyChecking) {
        this.needingCleaning = needingCleaning;
        this.needingPropertyChecking = needingPropertyChecking;
    }

    /**
     * @return True if the object needs removing from the tree
     */
    public boolean isNeedingCleaning() {
        return needingCleaning;
    }

    /**
     * @return True if the properties of the object need checking
     */

    public boolean isNeedingPropertyChecking() {
        return needingPropertyChecking;
    }

}
