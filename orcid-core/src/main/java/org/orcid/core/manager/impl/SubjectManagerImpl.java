package org.orcid.core.manager.impl;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.orcid.core.manager.SubjectManager;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.jpa.entities.SubjectEntity;

/**
 * 
 * @author Will Simpson
 * 
 */
public class SubjectManagerImpl implements SubjectManager {

    @Resource(name = "subjectDao")
    private GenericDao<SubjectEntity, String> subjectDao;

    @Override
    public Map<String, String> retrieveSubjectAsMap() {
        Map<String, String> map = new TreeMap<String, String>();
        for (SubjectEntity subject : subjectDao.getAll()) {
            String name = subject.getName();
            map.put(name, name);
        }
        return map;
    }

    public void setSubjectDao(GenericDao<SubjectEntity, String> subjectDao) {
        this.subjectDao = subjectDao;
    }

}
