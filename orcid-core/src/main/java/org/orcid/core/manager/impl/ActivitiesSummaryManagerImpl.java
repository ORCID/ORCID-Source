package org.orcid.core.manager.impl;

import org.springframework.transaction.annotation.Transactional;

import org.orcid.core.manager.ActivitiesSummaryManager;
import org.orcid.core.manager.read_only.impl.ActivitiesSummaryManagerReadOnlyImpl;

@Transactional(value = "transactionManager")
public class ActivitiesSummaryManagerImpl extends ActivitiesSummaryManagerReadOnlyImpl implements ActivitiesSummaryManager {    
    
}
