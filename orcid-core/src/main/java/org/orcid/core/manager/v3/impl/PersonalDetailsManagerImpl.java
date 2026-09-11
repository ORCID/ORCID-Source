package org.orcid.core.manager.v3.impl;

import org.springframework.transaction.annotation.Transactional;

import org.orcid.core.manager.v3.PersonalDetailsManager;
import org.orcid.core.manager.v3.read_only.impl.PersonalDetailsManagerReadOnlyImpl;

/**
* 
* @author Angel Montenegro
* 
*/

@Transactional(value = "transactionManager")
public class PersonalDetailsManagerImpl extends PersonalDetailsManagerReadOnlyImpl implements PersonalDetailsManager {

}
