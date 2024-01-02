package org.orcid.persistence.dao.impl;

import java.math.BigInteger;

import javax.persistence.Query;

import org.orcid.persistence.dao.ProfileEventDao;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;

/**
 *
 * @author Daniel Palafox
 *
 */
public class ProfileEventDaoImpl extends GenericDaoImpl<ProfileEventEntity, Long> implements ProfileEventDao {
    
    public ProfileEventDaoImpl() { super(ProfileEventEntity.class); }

    @Override
    public boolean isAttemptSend(String orcid, ProfileEventType eventType) {
        Query query = entityManager.createNativeQuery("select count(*) from profile_event where orcid=:orcid and profile_event_type=:eventType");
        query.setParameter("orcid", orcid);
        query.setParameter("eventType", eventType.toString());
        Long result = ((BigInteger)query.getSingleResult()).longValue();
        return (result != null && result > 0);
    }
}
