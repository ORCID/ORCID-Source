package org.orcid.core.manager.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.orcid.core.manager.SecurityQuestionManager;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.jpa.entities.SecurityQuestionEntity;
import org.springframework.cache.annotation.Cacheable;

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

    @Override
    public Map<String, String> retrieveSecurityQuestionsAsInternationalizedMap() {
        List<SecurityQuestionEntity> questions = securityQuestionDao.getAll();
        Map<String, String> map = new LinkedHashMap<String, String>();
        for (SecurityQuestionEntity question : questions) {
            map.put(String.valueOf(question.getId()), question.getKey());
        }
        return map;
    }
    
}
