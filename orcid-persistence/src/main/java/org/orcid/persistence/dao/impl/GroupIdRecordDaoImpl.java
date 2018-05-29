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
        TypedQuery<GroupIdRecordEntity> query = entityManager.createQuery("from GroupIdRecordEntity where trim(lower(groupId)) = trim(lower(:groupId))",
                GroupIdRecordEntity.class);
        query.setParameter("groupId", groupId);
        GroupIdRecordEntity result = query.getSingleResult();
        return result;
    }

    @Override
    public GroupIdRecordEntity findByName(String name) {
        TypedQuery<GroupIdRecordEntity> query = entityManager.createQuery("from GroupIdRecordEntity where trim(lower(group_name)) = trim(lower(:group_name))",
                GroupIdRecordEntity.class);
        query.setParameter("group_name", name);
        GroupIdRecordEntity result = query.getSingleResult();
        return result;
    }

    @Override
    public boolean haveAnyPeerReview(String groupId) {
        TypedQuery<Long> query = entityManager.createQuery("select count(*) from PeerReviewEntity where trim(lower(groupId)) = trim(lower(:groupId))", Long.class);
        query.setParameter("groupId", groupId);
        Long result = query.getSingleResult();
        return (result != null && result > 0);
    }

    @Override
    public boolean duplicateExists(Long putCode, String groupId) {
        StringBuilder queryString = new StringBuilder("select count(*) from GroupIdRecordEntity where trim(lower(groupId)) = trim(lower(:groupId))");
        if (putCode != null) {
            queryString.append(" and id != :putCode");
        }
        TypedQuery<Long> query = entityManager.createQuery(queryString.toString(), Long.class);
        query.setParameter("groupId", groupId);

        if (putCode != null) {
            query.setParameter("putCode", putCode);
        }
        Long result = query.getSingleResult();
        return (result != null && result > 0);
    }
}
