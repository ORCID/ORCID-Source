package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.orcid.persistence.dao.FindMyStuffHistoryDao;
import org.orcid.persistence.jpa.entities.FindMyStuffHistoryEntity;
import org.orcid.persistence.jpa.entities.keys.FindMyStuffHistoryEntityPk;

public class FindMyStuffHistoryDaoImpl extends GenericDaoImpl<FindMyStuffHistoryEntity, FindMyStuffHistoryEntityPk> implements FindMyStuffHistoryDao{

    public FindMyStuffHistoryDaoImpl() {
        super(FindMyStuffHistoryEntity.class);
    } 

    @Override
    @Transactional
    public void setOptOut(String orcid, String finderName, boolean optOut) {
        FindMyStuffHistoryEntity e = super.find(new FindMyStuffHistoryEntityPk(orcid,finderName));
        e.setOptOut(optOut);
        super.persist(e);
    }

    @Override
    public List<FindMyStuffHistoryEntity> findAll(String orcid) {
        TypedQuery<FindMyStuffHistoryEntity> query = entityManager.createQuery("from FindMyStuffHistoryEntity where orcid = :orcid", FindMyStuffHistoryEntity.class);
        query.setParameter("orcid", orcid);
        List<FindMyStuffHistoryEntity> results = query.getResultList();
        return results.isEmpty() ? null : results;
    }

    @Override
    @Transactional
    public void markActioned(String orcid, String finderName) {
        FindMyStuffHistoryEntity e = super.find(new FindMyStuffHistoryEntityPk(orcid,finderName));
        e.setActioned(true);
        super.persist(e);
    }

    @Override
    @Transactional
    public void markOptOut(String orcid, String finderName, boolean state) {
        FindMyStuffHistoryEntity e = super.find(new FindMyStuffHistoryEntityPk(orcid,finderName));
        e.setOptOut(state);
        super.persist(e);
    }

}
