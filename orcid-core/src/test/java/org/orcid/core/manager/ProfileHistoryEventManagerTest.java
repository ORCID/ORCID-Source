package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.manager.v3.impl.ProfileHistoryEventManagerImpl;
import org.orcid.core.profile.history.ProfileHistoryEventType;
import org.orcid.persistence.dao.ProfileHistoryEventDao;
import org.orcid.persistence.jpa.entities.ProfileHistoryEventEntity;

/**
 * ProfileHistoryEventManagerImpl has one collaborator, a DAO, and this test already drove it
 * entirely through Mockito -- the Spring context it loaded was never read from. The context
 * boot is gone; the mock set is unchanged.
 *
 * <p>
 * The field on the implementation is called {@code profileHistoryDao}, not
 * {@code profileHistoryEventDao}; @InjectMocks matches it by type, which is unambiguous because
 * it is the only ProfileHistoryEventDao in play.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProfileHistoryEventManagerTest {

    @Mock
    private ProfileHistoryEventDao profileHistoryEventDao;

    @InjectMocks
    private ProfileHistoryEventManagerImpl profileHistoryEventManager;

    @Test
    public void testRecordEvent() {
        Mockito.doNothing().when(profileHistoryEventDao).persist(any(ProfileHistoryEventEntity.class));
        profileHistoryEventManager.recordEvent(ProfileHistoryEventType.ACCEPTED_TERMS_CONDITIONS, "some-orcid");
        
        ArgumentCaptor<ProfileHistoryEventEntity> captor = ArgumentCaptor.forClass(ProfileHistoryEventEntity.class);
        Mockito.verify(profileHistoryEventDao).persist(captor.capture());
        ProfileHistoryEventEntity entity = captor.getValue();

        assertEquals("some-orcid", entity.getOrcid());
        assertNull(entity.getComment());
        assertEquals(ProfileHistoryEventType.ACCEPTED_TERMS_CONDITIONS.getLabel(), entity.getEventType());
    }
    
    @Test
    public void testRecordEventWithComments() {
        Mockito.doNothing().when(profileHistoryEventDao).persist(any(ProfileHistoryEventEntity.class));
        profileHistoryEventManager.recordEvent(ProfileHistoryEventType.SET_DEFAULT_VIS_TO_PRIVATE, "some-orcid", "deprecated/deactivated");
        
        ArgumentCaptor<ProfileHistoryEventEntity> captor = ArgumentCaptor.forClass(ProfileHistoryEventEntity.class);
        Mockito.verify(profileHistoryEventDao).persist(captor.capture());
        ProfileHistoryEventEntity entity = captor.getValue();

        assertEquals("some-orcid", entity.getOrcid());
        assertEquals("deprecated/deactivated", entity.getComment());
        assertEquals(ProfileHistoryEventType.SET_DEFAULT_VIS_TO_PRIVATE.getLabel(), entity.getEventType());
    }
    
    @Test
    public void testGetProfileHistoryForOrcid() {
        Mockito.when(profileHistoryEventDao.findByProfile(Mockito.eq("some-orcid"))).thenReturn(null);
        profileHistoryEventManager.getProfileHistoryForOrcid("some-orcid");
        Mockito.verify(profileHistoryEventDao).findByProfile(Mockito.eq("some-orcid"));
    }

}
