package org.orcid.core.manager.v3;

import java.util.List;

import org.orcid.core.profile.history.ProfileHistoryEventType;
import org.orcid.persistence.jpa.entities.ProfileHistoryEventEntity;

public interface ProfileHistoryEventManager {

    void recordEvent(ProfileHistoryEventType eventType, String orcid);
    
    void recordEvent(ProfileHistoryEventType eventType, String orcid, String comments);

    void recordResetPasswordEvent(String orcid, String ipAddress);

    void recordEmailUpdateEvent(String orcid, String ipAddress, String comment);
    
    List<ProfileHistoryEventEntity> getProfileHistoryForOrcid(String orcid);

}
