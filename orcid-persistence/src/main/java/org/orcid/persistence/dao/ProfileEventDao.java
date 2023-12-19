package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;

/**
 *
 * @author Daniel Palafox
 *
 */
public interface ProfileEventDao extends GenericDao<ProfileEventEntity, Long> {

    boolean isAttemptSend(String orcid, ProfileEventType eventType);
    
}
