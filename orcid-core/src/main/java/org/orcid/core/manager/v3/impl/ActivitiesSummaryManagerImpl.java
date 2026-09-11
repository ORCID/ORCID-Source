package org.orcid.core.manager.v3.impl;

import org.springframework.transaction.annotation.Transactional;

import org.orcid.core.manager.v3.ActivitiesSummaryManager;
import org.orcid.core.manager.v3.read_only.impl.ActivitiesSummaryManagerReadOnlyImpl;

@Transactional(value = "transactionManager")
public class ActivitiesSummaryManagerImpl extends ActivitiesSummaryManagerReadOnlyImpl implements ActivitiesSummaryManager {    
    
}
