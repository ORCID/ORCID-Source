package org.orcid.core.manager.v3;

import java.util.List;

import org.orcid.core.profile.history.ProfileHistoryEventType;
import org.orcid.persistence.jpa.entities.ProfileHistoryEventEntity;

public interface ProfileHistoryEventManager {

    void recordEvent(ProfileHistoryEventType eventType, String orcid);
    
    List<ProfileHistoryEventEntity> getProfileHistoryForOrcid(String orcid);

}
