package org.orcid.core.manager.impl;

import org.springframework.transaction.annotation.Transactional;

import org.orcid.core.manager.PersonalDetailsManager;
import org.orcid.core.manager.read_only.impl.PersonalDetailsManagerReadOnlyImpl;

/**
* 
* @author Angel Montenegro
* 
*/

@Transactional(value = "transactionManager")
public class PersonalDetailsManagerImpl extends PersonalDetailsManagerReadOnlyImpl implements PersonalDetailsManager {

}
