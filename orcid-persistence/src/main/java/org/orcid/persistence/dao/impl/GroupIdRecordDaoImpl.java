/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.GroupIdRecordDao;
import org.orcid.persistence.jpa.entities.GroupIdRecordEntity;

public class GroupIdRecordDaoImpl extends GenericDaoImpl<GroupIdRecordEntity, Long> implements GroupIdRecordDao {

    public GroupIdRecordDaoImpl() {
        super(GroupIdRecordEntity.class);
    }

    @Override
    public List<GroupIdRecordEntity> getGroupIdRecords(int pageSize, int page) {
        TypedQuery<GroupIdRecordEntity> query = entityManager.createQuery("from GroupIdRecordEntity order by dateCreated", GroupIdRecordEntity.class);
        query.setFirstResult(pageSize * (page - 1));
        query.setMaxResults(pageSize);
        return query.getResultList();
    }
    
    @Override
    public boolean exists(String groupId) {
        TypedQuery<Long> query = entityManager.createQuery("select count(*) from GroupIdRecordEntity where trim(lower(groupId)) = trim(lower(:groupId))", Long.class);
        query.setParameter("groupId", groupId);
        Long result = query.getSingleResult();
        return (result != null && result > 0);
    }
    
    @Override
    public GroupIdRecordEntity findByGroupId(String groupId) {
        TypedQuery<GroupIdRecordEntity> query = entityManager.createQuery("from GroupIdRecordEntity where trim(lower(groupId)) = trim(lower(:groupId))", GroupIdRecordEntity.class);
        query.setParameter("groupId", groupId);
        GroupIdRecordEntity result = query.getSingleResult();
        return result;
    }

    @Override
    public GroupIdRecordEntity findByName(String name) {
        TypedQuery<GroupIdRecordEntity> query = entityManager.createQuery("from GroupIdRecordEntity where trim(lower(group_name)) = trim(lower(:group_name))", GroupIdRecordEntity.class);
        query.setParameter("group_name", name);
        GroupIdRecordEntity result = query.getSingleResult();
        return result;
    }
}
