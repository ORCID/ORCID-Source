package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.orcid.persistence.dao.RejectedGroupingSuggestionDao;
import org.orcid.persistence.jpa.entities.RejectedGroupingSuggestionEntity;

public class RejectedGroupingSuggestionDaoImpl extends GenericDaoImpl<RejectedGroupingSuggestionEntity, String> implements RejectedGroupingSuggestionDao {

    public RejectedGroupingSuggestionDaoImpl() {
        super(RejectedGroupingSuggestionEntity.class);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public RejectedGroupingSuggestionEntity findGroupingSuggestionIdAndOrcid(String orcid, String putCodes) {
        Query query = entityManager.createQuery("FROM RejectedGroupingSuggestionEntity WHERE orcid = :orcid AND id = :putCodes", RejectedGroupingSuggestionEntity.class);
        query.setParameter("orcid", orcid);
        query.setParameter("putCodes", putCodes);
        List<RejectedGroupingSuggestionEntity> result = query.getResultList();
        return result.size() > 0 ? result.get(0) : null;
    }

}
