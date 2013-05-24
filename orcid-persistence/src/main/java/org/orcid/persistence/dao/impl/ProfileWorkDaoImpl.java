package org.orcid.persistence.dao.impl;

import javax.persistence.Query;

import org.orcid.persistence.dao.ProfileWorkDao;
import org.orcid.persistence.jpa.entities.ProfileWorkEntity;
import org.orcid.persistence.jpa.entities.keys.ProfileWorkEntityPk;
import org.springframework.transaction.annotation.Transactional;

public class ProfileWorkDaoImpl extends GenericDaoImpl<ProfileWorkEntity, ProfileWorkEntityPk> implements ProfileWorkDao {

    
    public ProfileWorkDaoImpl(){
        super(ProfileWorkEntity.class);
    }
    /**
     * Removes the relationship that exists between a work and a profile.
     * 
     * @param workId
     *          The id of the work that will be removed from the client profile
     * @param clientOrcid
     *          The client orcid 
     * @return true if the relationship was deleted
     * */
    @Override
    @Transactional
    public boolean removeWork(String workId, String clientOrcid) {
        Query query = entityManager.createQuery("delete from ProfileWorkEntity where profile.id=:clientOrcid and work.id=:workId");
        query.setParameter("clientOrcid", clientOrcid);
        query.setParameter("workId", Long.valueOf(workId));
        return query.executeUpdate() > 0 ? true : false;
    }

}
