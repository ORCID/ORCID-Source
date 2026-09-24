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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.manager.impl.RecoveryPhoneManagerImpl;
import org.orcid.persistence.dao.ProfileEventDao;
import org.orcid.persistence.dao.ProfileRecoveryPhoneDao;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.orcid.persistence.jpa.entities.ProfileRecoveryPhoneEntity;

@RunWith(MockitoJUnitRunner.class)
public class RecoveryPhoneManagerTest {

    private static final String ORCID = "0000-0000-0000-0001";

    private static final String PHONE = "+441234567890";

    private static final String ENCRYPTED = "encrypted-number";

    @Mock
    private ProfileRecoveryPhoneDao profileRecoveryPhoneDao;

    @Mock
    private EncryptionManager encryptionManager;

    @Mock
    private ProfileEventDao profileEventDao;

    @InjectMocks
    private RecoveryPhoneManagerImpl recoveryPhoneManager;

    @Test
    public void getRecoveryPhoneReturnsNullWhenNoneStored() {
        when(profileRecoveryPhoneDao.findByOrcid(ORCID)).thenReturn(null);
        assertNull(recoveryPhoneManager.getRecoveryPhone(ORCID));
    }

    @Test
    public void getRecoveryPhoneMapsTheLastFourAndTheDates() {
        Date created = new Date(1000L);
        Date modified = new Date(2000L);
        when(profileRecoveryPhoneDao.findByOrcid(ORCID)).thenReturn(entity(ENCRYPTED, "7890", created, modified));

        RecoveryPhone recoveryPhone = recoveryPhoneManager.getRecoveryPhone(ORCID);

        assertEquals("7890", recoveryPhone.getLastFour());
        assertEquals(created, recoveryPhone.getDateCreated());
        assertEquals(modified, recoveryPhone.getLastModified());
    }

    @Test
    public void getRecoveryPhoneNeverDecrypts() {
        when(profileRecoveryPhoneDao.findByOrcid(ORCID)).thenReturn(entity(ENCRYPTED, "7890", new Date(), new Date()));

        recoveryPhoneManager.getRecoveryPhone(ORCID);

        // The point of keeping the number on its own accessor: the Account settings panel polls this
        // one for the mask and the dates, and no poll should cost a decryption.
        verifyNoInteractions(encryptionManager);
    }

    @Test
    public void getDecryptedPhoneNumberDecryptsTheStoredNumber() {
        when(profileRecoveryPhoneDao.findByOrcid(ORCID)).thenReturn(entity(ENCRYPTED, "7890", new Date(), new Date()));
        when(encryptionManager.decryptForInternalUse(ENCRYPTED)).thenReturn(PHONE);

        String phoneNumber = recoveryPhoneManager.getDecryptedPhoneNumber(ORCID);

        // The stored column is what gets decrypted, and the caller gets the number back in full:
        // the recovery flows text it without the user re-typing it.
        verify(encryptionManager).decryptForInternalUse(ENCRYPTED);
        assertEquals(PHONE, phoneNumber);
    }

    @Test
    public void getDecryptedPhoneNumberReturnsNullWhenNoneStored() {
        when(profileRecoveryPhoneDao.findByOrcid(ORCID)).thenReturn(null);
        assertNull(recoveryPhoneManager.getDecryptedPhoneNumber(ORCID));
    }

    @Test
    public void saveStoresTheEncryptedNumberAndTheLastFour() {
        Date now = new Date();
        when(encryptionManager.encryptForInternalUse(PHONE)).thenReturn(ENCRYPTED);
        when(profileRecoveryPhoneDao.upsert(ORCID, ENCRYPTED, "7890"))
                .thenReturn(new ProfileRecoveryPhoneDao.UpsertResult(entity(ENCRYPTED, "7890", now, now), true));

        RecoveryPhone saved = recoveryPhoneManager.saveRecoveryPhone(ORCID, PHONE);

        // Answered from the row the upsert wrote, never from a second lookup
        assertEquals("7890", saved.getLastFour());
        assertEquals(now, saved.getDateCreated());
        verify(profileRecoveryPhoneDao, never()).findByOrcid(anyString());
        verify(profileRecoveryPhoneDao).upsert(ORCID, ENCRYPTED, "7890");
        // Reversibly encrypted, never hashed, and the plain number never reaches the DAO.
        verify(encryptionManager, never()).hashForInternalUse(anyString());
        verify(profileRecoveryPhoneDao, never()).upsert(eq(ORCID), eq(PHONE), anyString());
        assertEquals(ProfileEventType.PROFILE_RECOVERY_PHONE_ADDED, capturedEventType());
    }

    /*
     * findByOrcid runs on the read-only pool. Here it says there is no row - the
     * answer a lagging replica gives seconds after the primary stored one - while
     * the upsert, on the primary, found the row and replaced it. The event follows
     * the upsert: recording this as an "added" would be wrong in the audit trail.
     */
    @Test
    public void savingOverAnExistingNumberIsRecordedAsAnUpdate() {
        Date created = new Date(System.currentTimeMillis() - 60_000L);
        Date modified = new Date();
        when(profileRecoveryPhoneDao.findByOrcid(ORCID)).thenReturn(null);
        when(encryptionManager.encryptForInternalUse(anyString())).thenReturn("new-encrypted");
        when(profileRecoveryPhoneDao.upsert(ORCID, "new-encrypted", "7890"))
                .thenReturn(new ProfileRecoveryPhoneDao.UpsertResult(entity("new-encrypted", "7890", created, modified), false));

        RecoveryPhone saved = recoveryPhoneManager.saveRecoveryPhone(ORCID, PHONE);

        assertEquals(created, saved.getDateCreated());
        assertEquals(modified, saved.getLastModified());
        verify(profileRecoveryPhoneDao).upsert(ORCID, "new-encrypted", "7890");
        assertEquals(ProfileEventType.PROFILE_RECOVERY_PHONE_UPDATED, capturedEventType());
    }

    @Test
    public void lastFourIsTakenFromTheDigitsOnly() {
        when(encryptionManager.encryptForInternalUse(anyString())).thenReturn(ENCRYPTED);
        when(profileRecoveryPhoneDao.upsert(eq(ORCID), anyString(), anyString()))
                .thenReturn(new ProfileRecoveryPhoneDao.UpsertResult(entity(ENCRYPTED, "9876", new Date(), new Date()), true));

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

    private ProfileEventType capturedEventType() {
        ArgumentCaptor<ProfileEventEntity> captor = ArgumentCaptor.forClass(ProfileEventEntity.class);
        verify(profileEventDao).persist(captor.capture());
        return captor.getValue().getType();
    }

    private static ProfileRecoveryPhoneEntity entity(String encrypted, String lastFour, Date created, Date modified) {
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
        entity.setEncryptedPhoneNumber(encrypted);
        entity.setLastFour(lastFour);
        return entity;
    }

}
