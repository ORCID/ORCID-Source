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

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.orcid.core.manager.SecurityQuestionManager;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.jpa.entities.SecurityQuestionEntity;

/**
 * 
 * @author Will Simpson
 * 
 */
public class SecurityQuestionManagerImpl implements SecurityQuestionManager {

    @Resource(name = "securityQuestionDao")
    private GenericDao<SecurityQuestionEntity, Integer> securityQuestionDao;

    @Override
    public Map<String, String> retrieveSecurityQuestionsAsMap() {
        List<SecurityQuestionEntity> questions = securityQuestionDao.getAll();
        Map<String, String> map = new TreeMap<String, String>();
        for (SecurityQuestionEntity question : questions) {
            map.put(String.valueOf(question.getId()), question.getQuestion());
        }
        return map;
    }

}
