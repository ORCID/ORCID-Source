package org.orcid.core.manager.v3.impl;

import org.springframework.transaction.annotation.Transactional;

import org.orcid.core.manager.v3.PersonDetailsManager;
import org.orcid.core.manager.v3.read_only.impl.PersonDetailsManagerReadOnlyImpl;

@Transactional(value = "transactionManager")
public class PersonDetailsManagerImpl extends PersonDetailsManagerReadOnlyImpl implements PersonDetailsManager {
    
}
