package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.orcid.core.manager.impl.BackupCodeManagerImpl;
import org.orcid.persistence.dao.BackupCodeDao;
import org.orcid.persistence.jpa.entities.BackupCodeEntity;

/**
 * BackupCodeManagerImpl holds a DAO and an EncryptionManager and nothing else, and this
 * test already drove it entirely through Mockito -- the Spring context it loaded was
 * never read from. The context boot is gone; the mock set is unchanged.
 *
 * <p>
 * The two Answer stubs are kept rather than replaced by constant returns. hashForInternalUse
 * echoes its argument so that the codes handed back to the caller can be compared against the
 * hashes handed to the DAO; a constant would make every entity carry the same hash and the
 * "each returned code was persisted" loop would pass on any implementation. hashMatches
 * compares its two arguments for the same reason: with a constant true, verify() would report
 * success for the three codes the test expects it to reject.
 */
@RunWith(MockitoJUnitRunner.class)
public class BackupCodeManagerTest {

    @Mock
    private EncryptionManager encryptionManager;

    @Mock
    private BackupCodeDao backupCodeDao;

    @InjectMocks
    private BackupCodeManagerImpl backupCodeManager;

    @Test
    public void testCreateBackupCodes() {
        when(backupCodeDao.merge(any(BackupCodeEntity.class))).thenReturn(null);
        when(encryptionManager.hashForInternalUse(anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (String) args[0];
            }
        });
        
        List<String> codes = backupCodeManager.createBackupCodes("orcid");
        assertNotNull(codes);
        assertFalse(codes.isEmpty());
        
        ArgumentCaptor<BackupCodeEntity> captor = ArgumentCaptor.forClass(BackupCodeEntity.class);
        verify(backupCodeDao, times(14)).merge(captor.capture());
        List<BackupCodeEntity> persistedEntities = captor.getAllValues();
        
        assertEquals(persistedEntities.size(), codes.size());
        for (BackupCodeEntity entity : persistedEntities) {
            assertNotNull(entity.getHashedCode());
            assertNotNull(entity.getOrcid());
            assertNull(entity.getUsedDate());
            assertEquals("orcid", entity.getOrcid());
            
            boolean matchFound = false;
            for (String code : codes) {
                if (entity.getHashedCode().equals(code)) {
                    matchFound = true;
                }
            }
            assertTrue(matchFound);
        }
    }

    @Test
    public void testVerify() {
        when(backupCodeDao.getUnusedBackupCodes(eq("orcid"))).thenReturn(getBackupCodes());
        when(encryptionManager.hashMatches(anyString(), anyString())).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return ((String) args[0]).equals((String) args[1]);
            }
        });
        
        assertTrue(backupCodeManager.verify("orcid", "hashedCode0"));
        assertTrue(backupCodeManager.verify("orcid", "hashedCode1"));
        assertTrue(backupCodeManager.verify("orcid", "hashedCode2"));
        assertTrue(backupCodeManager.verify("orcid", "hashedCode3"));
        assertTrue(backupCodeManager.verify("orcid", "hashedCode4"));
        assertTrue(backupCodeManager.verify("orcid", "hashedCode5"));
        assertTrue(backupCodeManager.verify("orcid", "hashedCode6"));
        assertTrue(backupCodeManager.verify("orcid", "hashedCode7"));
        assertTrue(backupCodeManager.verify("orcid", "hashedCode8"));
        assertTrue(backupCodeManager.verify("orcid", "hashedCode9"));
        
        assertFalse(backupCodeManager.verify("orcid", "erm...."));
        assertFalse(backupCodeManager.verify("orcid", "not"));
        assertFalse(backupCodeManager.verify("orcid", "right"));
    }

    @Test
    public void testRemoveBackupCodes() {
        doNothing().when(backupCodeDao).removedUsedBackupCodes(eq("orcid"));
        backupCodeManager.removeUnusedBackupCodes("orcid");
        verify(backupCodeDao, times(1)).removedUsedBackupCodes(eq("orcid"));
    }

    private List<BackupCodeEntity> getBackupCodes() {
        List<BackupCodeEntity> codes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            BackupCodeEntity backupCode = new BackupCodeEntity();
            backupCode.setOrcid("orcid");
            backupCode.setHashedCode("hashedCode" + i);
            codes.add(backupCode);
        }
        return codes;
    }

}
