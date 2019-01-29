package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.orcid.persistence.dao.MemberChosenOrgDisambiguatedDao;
import org.orcid.persistence.jpa.entities.MemberChosenOrgDisambiguatedEntity;

public class MemberChosenOrgDisambiguatedDaoImpl implements MemberChosenOrgDisambiguatedDao {

    @Resource(name="entityManager")
    protected EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @Override
    public List<MemberChosenOrgDisambiguatedEntity> getAll() {
        return entityManager.createQuery("from MemberChosenOrgDisambiguatedEntity").getResultList();
    }

    @Override
    @Transactional
    public MemberChosenOrgDisambiguatedEntity merge(MemberChosenOrgDisambiguatedEntity entity) {
        return entityManager.merge(entity);
    }

    @Override
    @Transactional
    public void remove(MemberChosenOrgDisambiguatedEntity entity) {
        entityManager.remove(entity);
    }
    
    
}