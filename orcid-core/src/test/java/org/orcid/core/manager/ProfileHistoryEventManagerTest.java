package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.v3.impl.ProfileHistoryEventManagerImpl;
import org.orcid.core.profile.history.ProfileHistoryEventType;
import org.orcid.persistence.dao.ProfileHistoryEventDao;
import org.orcid.persistence.jpa.entities.ProfileHistoryEventEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class ProfileHistoryEventManagerTest {

    @Mock
    private ProfileHistoryEventDao profileHistoryEventDao;

    @InjectMocks
    private ProfileHistoryEventManagerImpl profileHistoryEventManager;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRecordEvent() {
        Mockito.doNothing().when(profileHistoryEventDao).persist(any(ProfileHistoryEventEntity.class));
        profileHistoryEventManager.recordEvent(ProfileHistoryEventType.ACCEPTED_TERMS_CONDITIONS, "some-orcid");
        
        ArgumentCaptor<ProfileHistoryEventEntity> captor = ArgumentCaptor.forClass(ProfileHistoryEventEntity.class);
        Mockito.verify(profileHistoryEventDao).persist(captor.capture());
        ProfileHistoryEventEntity entity = captor.getValue();

        assertEquals("some-orcid", entity.getOrcid());
        assertNotNull(entity.getDateCreated());
        assertNotNull(entity.getLastModified());
        assertNull(entity.getComment());
        assertEquals(ProfileHistoryEventType.ACCEPTED_TERMS_CONDITIONS.getLabel(), entity.getEventType());
    }
    
    @Test
    public void testGetProfileHistoryForOrcid() {
        Mockito.when(profileHistoryEventDao.findByProfile(Mockito.eq("some-orcid"))).thenReturn(null);
        profileHistoryEventManager.getProfileHistoryForOrcid("some-orcid");
        Mockito.verify(profileHistoryEventDao).findByProfile(Mockito.eq("some-orcid"));
    }

}
