package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.impl.RecoveryPhoneManagerImpl;
import org.orcid.persistence.dao.ProfileEventDao;
import org.orcid.persistence.dao.ProfileRecoveryPhoneDao;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.orcid.persistence.jpa.entities.ProfileRecoveryPhoneEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
public class RecoveryPhoneManagerTest {

    private static final String ORCID = "0000-0000-0000-0001";

    private static final String PHONE = "+441234567890";

    @Mock
    private ProfileRecoveryPhoneDao profileRecoveryPhoneDao;

    @Mock
    private EncryptionManager encryptionManager;

    @Mock
    private ProfileEventDao profileEventDao;

    @InjectMocks
    private RecoveryPhoneManagerImpl recoveryPhoneManager;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getRecoveryPhoneReturnsNullWhenNoneStored() {
        when(profileRecoveryPhoneDao.findByOrcid(ORCID)).thenReturn(null);
        assertNull(recoveryPhoneManager.getRecoveryPhone(ORCID));
    }

    @Test
    public void getRecoveryPhoneExposesOnlyLastFourAndDates() {
        Date created = new Date(1000L);
        Date modified = new Date(2000L);
        when(profileRecoveryPhoneDao.findByOrcid(ORCID)).thenReturn(entity("hashed", "7890", created, modified));

        RecoveryPhone recoveryPhone = recoveryPhoneManager.getRecoveryPhone(ORCID);

        assertEquals("7890", recoveryPhone.getLastFour());
        assertEquals(created, recoveryPhone.getDateCreated());
        assertEquals(modified, recoveryPhone.getLastModified());
    }

    @Test
    public void saveStoresOnlyAHashAndTheLastFourDigits() {
        when(profileRecoveryPhoneDao.findByOrcid(ORCID)).thenReturn(null);
        when(encryptionManager.hashForInternalUse(PHONE)).thenReturn("hashed-number");

        boolean isNew = recoveryPhoneManager.saveRecoveryPhone(ORCID, PHONE);

        assertTrue(isNew);
        verify(profileRecoveryPhoneDao).upsert(ORCID, "hashed-number", "7890");
        assertEquals(ProfileEventType.PROFILE_RECOVERY_PHONE_ADDED, capturedEventType());
    }

    @Test
    public void savingOverAnExistingNumberIsRecordedAsAnUpdate() {
        when(profileRecoveryPhoneDao.findByOrcid(ORCID)).thenReturn(entity("old-hash", "1234", new Date(), new Date()));
        when(encryptionManager.hashForInternalUse(anyString())).thenReturn("new-hash");

        boolean isNew = recoveryPhoneManager.saveRecoveryPhone(ORCID, PHONE);

        assertFalse(isNew);
        assertEquals(ProfileEventType.PROFILE_RECOVERY_PHONE_UPDATED, capturedEventType());
    }

    @Test
    public void lastFourIsTakenFromTheDigitsOnly() {
        when(profileRecoveryPhoneDao.findByOrcid(ORCID)).thenReturn(null);
        when(encryptionManager.hashForInternalUse(anyString())).thenReturn("hash");

        recoveryPhoneManager.saveRecoveryPhone(ORCID, "+1 (555) 010-9876");

        verify(profileRecoveryPhoneDao).upsert(eq(ORCID), anyString(), eq("9876"));
    }

    @Test
    public void removeIsSilentWhenThereWasNothingToRemove() {
        when(profileRecoveryPhoneDao.deleteByOrcid(ORCID)).thenReturn(false);

        recoveryPhoneManager.removeRecoveryPhone(ORCID);

        verify(profileEventDao, never()).persist(any(ProfileEventEntity.class));
    }

    @Test
    public void removeRecordsAnEventWhenANumberWasDeleted() {
        when(profileRecoveryPhoneDao.deleteByOrcid(ORCID)).thenReturn(true);

        recoveryPhoneManager.removeRecoveryPhone(ORCID);

        assertEquals(ProfileEventType.PROFILE_RECOVERY_PHONE_REMOVED, capturedEventType());
    }

    @Test
    public void matchesComparesTheSuppliedNumberAgainstTheStoredHash() {
        when(profileRecoveryPhoneDao.findByOrcid(ORCID)).thenReturn(entity("hashed-number", "7890", new Date(), new Date()));
        when(encryptionManager.hashMatches(PHONE, "hashed-number")).thenReturn(true);
        when(encryptionManager.hashMatches("+15550000000", "hashed-number")).thenReturn(false);

        assertTrue(recoveryPhoneManager.matches(ORCID, PHONE));
        assertFalse(recoveryPhoneManager.matches(ORCID, "+15550000000"));
    }

    @Test
    public void matchesIsFalseWhenThereIsNoStoredNumber() {
        when(profileRecoveryPhoneDao.findByOrcid(ORCID)).thenReturn(null);
        assertFalse(recoveryPhoneManager.matches(ORCID, PHONE));
    }

    private ProfileEventType capturedEventType() {
        ArgumentCaptor<ProfileEventEntity> captor = ArgumentCaptor.forClass(ProfileEventEntity.class);
        verify(profileEventDao).persist(captor.capture());
        return captor.getValue().getType();
    }

    private static ProfileRecoveryPhoneEntity entity(String hash, String lastFour, Date created, Date modified) {
        ProfileRecoveryPhoneEntity entity = new ProfileRecoveryPhoneEntity() {
            private static final long serialVersionUID = 1L;

            @Override
            public Date getDateCreated() {
                return created;
            }

            @Override
            public Date getLastModified() {
                return modified;
            }
        };
        entity.setHashedPhoneNumber(hash);
        entity.setLastFour(lastFour);
        return entity;
    }

}
