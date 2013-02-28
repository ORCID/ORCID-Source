/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.orcid.core.manager.RegistrationRoleManager;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.jpa.entities.RegistrationRoleEntity;

/**
 * 
 * @author Will Simpson
 * 
 */
public class RegistrationRoleManagerImpl implements RegistrationRoleManager {

    @Resource(name = "registrationRoleDao")
    private GenericDao<RegistrationRoleEntity, Integer> registrationRoleDao;

    @Override
    public Map<String, String> retrieveRegistrationRolesAsMap() {
        List<RegistrationRoleEntity> roles = registrationRoleDao.getAll();
        Map<String, String> map = new LinkedHashMap<String, String>();
        for (RegistrationRoleEntity role : roles) {
            map.put(role.getRole(), role.getRole());
        }
        return map;
    }

}
