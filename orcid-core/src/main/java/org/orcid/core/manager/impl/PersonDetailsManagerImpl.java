package org.orcid.core.manager.impl;

import org.springframework.transaction.annotation.Transactional;

import org.orcid.core.manager.PersonDetailsManager;
import org.orcid.core.manager.read_only.impl.PersonDetailsManagerReadOnlyImpl;

@Transactional(value = "transactionManager")
public class PersonDetailsManagerImpl extends PersonDetailsManagerReadOnlyImpl implements PersonDetailsManager {
    
}
