package org.orcid.persistence.dao.impl;

import java.util.List;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

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
    public void remove(Long id) {
        entityManager.remove(entityManager.find(MemberChosenOrgDisambiguatedEntity.class, id));
    }
    
    
}