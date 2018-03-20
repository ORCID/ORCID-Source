package org.orcid.core.tree;

public interface TreeCleaningStrategy {

    TreeCleaningDecision needsStripping(Object obj);

}
