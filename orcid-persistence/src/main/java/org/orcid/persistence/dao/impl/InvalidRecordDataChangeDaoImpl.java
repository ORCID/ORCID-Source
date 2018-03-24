package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.InvalidRecordDataChangeDao;
import org.orcid.persistence.jpa.entities.InvalidRecordDataChangeEntity;

public class InvalidRecordDataChangeDaoImpl implements InvalidRecordDataChangeDao {

    @Resource(name="entityManager")
    protected EntityManager entityManager;
    
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<InvalidRecordDataChangeEntity> getByDateCreated(Long lastId, Long pageSize, boolean descendantOrder) {
        String queryStr = "SELECT * FROM invalid_record_data_changes WHERE id {GTorLT} {LAST_SEQUENCE} ORDER BY id {ORDER} LIMIT :pageSize";
        
        String GTorLT = descendantOrder ? "<" : ">";
        String lastIdStr = descendantOrder ? "(select (max(id) + 1) from invalid_record_data_changes)" : "0";
        if(lastId != null) {
            lastIdStr = String.valueOf(lastId);
        }
        
        queryStr = queryStr.replace("{GTorLT}", GTorLT);
        queryStr = queryStr.replace("{LAST_SEQUENCE}", lastIdStr);
        queryStr = queryStr.replace("{ORDER}", descendantOrder ? "DESC" : "ASC");
        
        Query query = entityManager.createNativeQuery(queryStr, InvalidRecordDataChangeEntity.class);        
        query.setParameter("pageSize", pageSize);
        return (List<InvalidRecordDataChangeEntity>) query.getResultList();
    }

    @Override
    public boolean haveNext(Long sequence, boolean descendantOrder) {
        String queryStr = "SELECT COUNT(*) FROM InvalidRecordDataChangeEntity WHERE id {GTorLT} :sequence";        
        TypedQuery<Long> query = entityManager.createQuery(queryStr.replace("{GTorLT}", descendantOrder ? "<" : ">"), Long.class);
        query.setParameter("sequence", sequence);
        return (query.getSingleResult() == 0) ? false : true;
    }

    @Override
    public boolean havePrevious(Long sequence, boolean descendantOrder) {
        String queryStr = "SELECT COUNT(*) FROM InvalidRecordDataChangeEntity WHERE id {GTorLT} :sequence";        
        TypedQuery<Long> query = entityManager.createQuery(queryStr.replace("{GTorLT}", descendantOrder ? ">" : "<"), Long.class);
        query.setParameter("sequence", sequence);        
        return (query.getSingleResult() == 0) ? false : true;
    }   
}
