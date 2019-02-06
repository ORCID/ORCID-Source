package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.MemberOBOWhitelistedClientDao;
import org.orcid.persistence.jpa.entities.MemberOBOWhitelistedClientEntity;

public class MemberOBOWhitelistedClientDaoImpl extends GenericDaoImpl<MemberOBOWhitelistedClientEntity, Long> implements MemberOBOWhitelistedClientDao {

    public MemberOBOWhitelistedClientDaoImpl() {
        super(MemberOBOWhitelistedClientEntity.class);
    }

    @Override
    public List<MemberOBOWhitelistedClientEntity> getWhitelistForClient(String clientDetailsId) {
        TypedQuery<MemberOBOWhitelistedClientEntity> query = entityManager.createQuery("from MemberOBOWhitelistedClientEntity where clientDetailsEntity.id = :clientDetailsId", MemberOBOWhitelistedClientEntity.class);
        query.setParameter("clientDetailsId", clientDetailsId);
        return query.getResultList();
    }

}