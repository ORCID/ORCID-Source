package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.GroupingSuggestionDao;
import org.orcid.persistence.jpa.entities.GroupingSuggestionEntity;

public class GroupingSuggestionDaoImpl extends GenericDaoImpl<GroupingSuggestionEntity, Long> implements GroupingSuggestionDao {

    public GroupingSuggestionDaoImpl() {
        super(GroupingSuggestionEntity.class);
    }
    
    @Override
    public List<GroupingSuggestionEntity> getGroupingSuggestions(String orcid) {
        TypedQuery<GroupingSuggestionEntity> query = entityManager.createQuery("FROM GroupingSuggestionEntity WHERE orcid = :orcid and dismissedDate IS NULL", GroupingSuggestionEntity.class);
        query.setParameter("orcid", orcid);
        return query.getResultList();
    }

}
