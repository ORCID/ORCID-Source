package org.orcid.core.manager.v3.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import jakarta.annotation.Resource;

import org.orcid.core.manager.v3.ProfileHistoryEventManager;
import org.orcid.core.profile.history.ProfileHistoryEventType;
import org.orcid.persistence.dao.ProfileHistoryEventDao;
import org.orcid.persistence.jpa.entities.ProfileHistoryEventEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfileHistoryEventManagerImpl implements ProfileHistoryEventManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileHistoryEventManagerImpl.class);

    @Resource
    private ProfileHistoryEventDao profileHistoryDao;

    @Override
    public void recordEvent(ProfileHistoryEventType eventType, String orcid) {
        ProfileHistoryEventEntity profileHistoryEvent = new ProfileHistoryEventEntity();
        profileHistoryEvent.setEventType(eventType.getLabel());
        profileHistoryEvent.setOrcid(orcid);
        profileHistoryDao.persist(profileHistoryEvent);
    }
    
    @Override
    public void recordEvent(ProfileHistoryEventType eventType, String orcid, String comments) {
        ProfileHistoryEventEntity profileHistoryEvent = new ProfileHistoryEventEntity();
        profileHistoryEvent.setEventType(eventType.getLabel());
        profileHistoryEvent.setOrcid(orcid);
        profileHistoryEvent.setComment(comments);
        profileHistoryDao.persist(profileHistoryEvent);
    }

    @Override
    public void recordResetPasswordEvent(String orcid, String ipAddress) {
        try {
            ProfileHistoryEventEntity profileHistoryEvent = new ProfileHistoryEventEntity();
            profileHistoryEvent.setEventType(ProfileHistoryEventType.RESET_PASSWORD.getLabel());
            profileHistoryEvent.setOrcid(orcid);
            profileHistoryEvent.setIp(InetAddress.getByName(ipAddress));
            profileHistoryDao.persist(profileHistoryEvent);
        } catch (UnknownHostException e) {
            LOGGER.warn("Unable to persist ip address {} for orcid {}", ipAddress, orcid);
            ProfileHistoryEventEntity profileHistoryEvent = new ProfileHistoryEventEntity();
            profileHistoryEvent.setEventType(ProfileHistoryEventType.RESET_PASSWORD.getLabel());
            profileHistoryEvent.setOrcid(orcid);
            profileHistoryEvent.setComment("IP address: " + ipAddress);
            profileHistoryDao.persist(profileHistoryEvent);
        }
    }

    @Override
    public void recordEmailUpdateEvent(String orcid, String ipAddress, String comment) {
        try {
            ProfileHistoryEventEntity profileHistoryEvent = new ProfileHistoryEventEntity();
            profileHistoryEvent.setEventType(ProfileHistoryEventType.EMAIL_CHANGED.getLabel());
            profileHistoryEvent.setOrcid(orcid);
            profileHistoryEvent.setIp(InetAddress.getByName(ipAddress));
            profileHistoryEvent.setComment(comment);
            profileHistoryDao.persist(profileHistoryEvent);
        } catch (UnknownHostException e) {
            LOGGER.warn("Unable to persist ip address {} for orcid {}", ipAddress, orcid);
            ProfileHistoryEventEntity profileHistoryEvent = new ProfileHistoryEventEntity();
            profileHistoryEvent.setEventType(ProfileHistoryEventType.EMAIL_CHANGED.getLabel());
            profileHistoryEvent.setOrcid(orcid);
            profileHistoryEvent.setComment("IP address: " + ipAddress + " - " + comment);
            profileHistoryDao.persist(profileHistoryEvent);
        }
    }

    @Override
    public List<ProfileHistoryEventEntity> getProfileHistoryForOrcid(String orcid) {
        return profileHistoryDao.findByProfile(orcid);
    }

}
