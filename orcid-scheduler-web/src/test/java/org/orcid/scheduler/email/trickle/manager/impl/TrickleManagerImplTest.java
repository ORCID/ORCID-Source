package org.orcid.scheduler.email.trickle.manager.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.email.trickle.producer.EmailTrickleItem;
import org.orcid.core.manager.impl.MailGunManager;
import org.orcid.core.manager.v3.EmailMessage;
import org.orcid.persistence.dao.EmailFrequencyDao;
import org.orcid.persistence.dao.EmailScheduleDao;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailFrequencyEntity;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;

public class TrickleManagerImplTest {

    @Mock
    private EmailScheduleDao emailScheduleDaoReadOnly;

    @Mock
    private EmailScheduleDao emailScheduleDao;

    @Mock
    private EmailFrequencyDao emailFrequencyDaoReadOnly;

    @Mock
    private ProfileDao profileDaoReadOnly;

    @Mock
    private MailGunManager mailGunManager;

    @Mock
    private GenericDao<ProfileEventEntity, Long> profileEventDao;

    @InjectMocks
    private TrickleManagerImpl trickleManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAttemptSendSuccess() {
        Mockito.when(emailScheduleDaoReadOnly.getValidScheduleId()).thenReturn(2L);
        Mockito.when(emailFrequencyDaoReadOnly.findByOrcid(Mockito.eq("orcid"))).thenReturn(getEmailFrequencyQuarterlyTipsEnabled());
        Mockito.when(profileDaoReadOnly.getProfileEvents(Mockito.eq("orcid"), Mockito.anyList())).thenReturn(new ArrayList<>());

        ArgumentCaptor<String> fromCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> toCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> htmlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ProfileEventEntity> eventCaptor = ArgumentCaptor.forClass(ProfileEventEntity.class);

        Mockito.when(mailGunManager.sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Boolean.TRUE);
        trickleManager.attemptSend(getEmailTrickleItem());
        Mockito.verify(mailGunManager, Mockito.times(1)).sendEmail(fromCaptor.capture(), toCaptor.capture(), subjectCaptor.capture(), textCaptor.capture(),
                htmlCaptor.capture());
        Mockito.verify(profileDaoReadOnly, Mockito.times(1)).getProfileEvents(Mockito.eq("orcid"), Mockito.anyList());
        Mockito.verify(emailScheduleDaoReadOnly, Mockito.times(1)).getValidScheduleId();
        Mockito.verify(emailScheduleDao, Mockito.times(1)).updateLatestSent(Mockito.eq(2L), Mockito.any(Date.class));
        Mockito.verify(emailFrequencyDaoReadOnly, Mockito.times(1)).findByOrcid(Mockito.eq("orcid"));
        Mockito.verify(profileEventDao, Mockito.times(1)).merge(eventCaptor.capture());

        assertEquals("me", fromCaptor.getValue());
        assertEquals("you", toCaptor.getValue());
        assertEquals("hello", subjectCaptor.getValue());
        assertEquals("hello", textCaptor.getValue());
        assertEquals("<p>hello</p>", htmlCaptor.getValue());

        ProfileEventEntity event = eventCaptor.getValue();
        assertEquals(ProfileEventType.MARCH_2019_SENT, event.getType());
        assertEquals("orcid", event.getOrcid());
    }
    
    @Test
    public void testAttemptSendFailure() {
        Mockito.when(emailScheduleDaoReadOnly.getValidScheduleId()).thenReturn(2L);
        Mockito.when(emailFrequencyDaoReadOnly.findByOrcid(Mockito.eq("orcid"))).thenReturn(getEmailFrequencyQuarterlyTipsEnabled());
        Mockito.when(profileDaoReadOnly.getProfileEvents(Mockito.eq("orcid"), Mockito.anyList())).thenReturn(new ArrayList<>());

        ArgumentCaptor<String> fromCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> toCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> htmlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ProfileEventEntity> eventCaptor = ArgumentCaptor.forClass(ProfileEventEntity.class);

        Mockito.when(mailGunManager.sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Boolean.FALSE);
        trickleManager.attemptSend(getEmailTrickleItem());
        Mockito.verify(mailGunManager, Mockito.times(1)).sendEmail(fromCaptor.capture(), toCaptor.capture(), subjectCaptor.capture(), textCaptor.capture(),
                htmlCaptor.capture());
        Mockito.verify(profileDaoReadOnly, Mockito.times(1)).getProfileEvents(Mockito.eq("orcid"), Mockito.anyList());
        Mockito.verify(emailScheduleDaoReadOnly, Mockito.times(1)).getValidScheduleId();
        Mockito.verify(emailScheduleDao, Mockito.never()).updateLatestSent(Mockito.eq(2L), Mockito.any(Date.class));
        Mockito.verify(emailFrequencyDaoReadOnly, Mockito.times(1)).findByOrcid(Mockito.eq("orcid"));
        Mockito.verify(profileEventDao, Mockito.times(1)).merge(eventCaptor.capture());

        assertEquals("me", fromCaptor.getValue());
        assertEquals("you", toCaptor.getValue());
        assertEquals("hello", subjectCaptor.getValue());
        assertEquals("hello", textCaptor.getValue());
        assertEquals("<p>hello</p>", htmlCaptor.getValue());

        ProfileEventEntity event = eventCaptor.getValue();
        assertEquals(ProfileEventType.MARCH_2019_FAILED, event.getType());
        assertEquals("orcid", event.getOrcid());
    }
    

    @Test
    public void testAttemptSendSkipped() {
        Mockito.when(emailScheduleDaoReadOnly.getValidScheduleId()).thenReturn(2L);
        Mockito.when(emailFrequencyDaoReadOnly.findByOrcid(Mockito.eq("orcid"))).thenReturn(getEmailFrequencyQuarterlyTipsDisabled());
        Mockito.when(profileDaoReadOnly.getProfileEvents(Mockito.eq("orcid"), Mockito.anyList())).thenReturn(new ArrayList<>());

        ArgumentCaptor<ProfileEventEntity> eventCaptor = ArgumentCaptor.forClass(ProfileEventEntity.class);

        Mockito.when(mailGunManager.sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Boolean.FALSE);
        trickleManager.attemptSend(getEmailTrickleItem());
        Mockito.verify(mailGunManager, Mockito.never()).sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(profileDaoReadOnly, Mockito.times(1)).getProfileEvents(Mockito.eq("orcid"), Mockito.anyList());
        Mockito.verify(emailScheduleDaoReadOnly, Mockito.times(1)).getValidScheduleId();
        Mockito.verify(emailScheduleDao, Mockito.never()).updateLatestSent(Mockito.eq(2L), Mockito.any(Date.class));
        Mockito.verify(emailFrequencyDaoReadOnly, Mockito.times(1)).findByOrcid(Mockito.eq("orcid"));
        Mockito.verify(profileEventDao, Mockito.times(1)).merge(eventCaptor.capture());

        ProfileEventEntity event = eventCaptor.getValue();
        assertEquals(ProfileEventType.MARCH_2019_SKIPPED, event.getType());
        assertEquals("orcid", event.getOrcid());
    }
    
    @Test
    public void testAttemptSendAlreadyProcessed() {
        Mockito.when(emailScheduleDaoReadOnly.getValidScheduleId()).thenReturn(2L);
        Mockito.when(emailFrequencyDaoReadOnly.findByOrcid(Mockito.eq("orcid"))).thenReturn(getEmailFrequencyQuarterlyTipsEnabled());
        Mockito.when(profileDaoReadOnly.getProfileEvents(Mockito.eq("orcid"), Mockito.anyList())).thenReturn(Arrays.asList(getProfileEventEntity(ProfileEventType.MARCH_2019_SENT, "orcid")));

        Mockito.when(mailGunManager.sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Boolean.FALSE);
        trickleManager.attemptSend(getEmailTrickleItem());
        Mockito.verify(mailGunManager, Mockito.never()).sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(profileDaoReadOnly, Mockito.times(1)).getProfileEvents(Mockito.eq("orcid"), Mockito.anyList());
        Mockito.verify(emailScheduleDaoReadOnly, Mockito.times(1)).getValidScheduleId();
        Mockito.verify(emailScheduleDao, Mockito.never()).updateLatestSent(Mockito.eq(2L), Mockito.any(Date.class));
        Mockito.verify(emailFrequencyDaoReadOnly, Mockito.never()).findByOrcid(Mockito.eq("orcid"));
        Mockito.verify(profileEventDao, Mockito.never()).merge(Mockito.any(ProfileEventEntity.class));
    }

    private EmailTrickleItem getEmailTrickleItem() {
        EmailTrickleItem item = new EmailTrickleItem();
        item.setOrcid("orcid");
        item.setSuccessType(ProfileEventType.MARCH_2019_SENT);
        item.setFailureType(ProfileEventType.MARCH_2019_FAILED);
        item.setSkippedType(ProfileEventType.MARCH_2019_SKIPPED);
        item.setEmailMessage(getEmailMessage());
        return item;
    }

    private EmailFrequencyEntity getEmailFrequencyQuarterlyTipsEnabled() {
        EmailFrequencyEntity freq = new EmailFrequencyEntity();
        freq.setDateCreated(new Date());
        freq.setLastModified(new Date());
        freq.setOrcid("orcid");
        freq.setSendAdministrativeChangeNotifications(0f);
        freq.setSendChangeNotifications(0f);
        freq.setSendMemberUpdateRequests(0f);
        freq.setSendQuarterlyTips(Boolean.TRUE);
        return freq;
    }

    private EmailMessage getEmailMessage() {
        EmailMessage message = new EmailMessage();
        message.setFrom("me");
        message.setTo("you");
        message.setBodyText("hello");
        message.setBodyHtml("<p>hello</p>");
        message.setSubject("hello");
        return message;
    }
    
    private ProfileEventEntity getProfileEventEntity(ProfileEventType type, String orcid) {
        ProfileEventEntity event = new ProfileEventEntity();
        event.setDateCreated(new Date());
        event.setLastModified(new Date());
        event.setOrcid(orcid);
        event.setType(type);
        return event;
    }
    
    private EmailFrequencyEntity getEmailFrequencyQuarterlyTipsDisabled() {
        EmailFrequencyEntity freq = getEmailFrequencyQuarterlyTipsEnabled();
        freq.setSendQuarterlyTips(Boolean.FALSE);
        return freq;
    }

}
