package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.orcid.core.admin.LockReason;
import org.orcid.core.manager.impl.AdminManagerImpl;

public class AdminManagerTest {
    
    private AdminManager adminManager = new AdminManagerImpl();

    @Test
    public void testGetLockReasons() {
        List<String> lockReasons = adminManager.getLockReasons();
        assertEquals(LockReason.values().length, lockReasons.size());
        for (String lockReasonLabel : lockReasons) {
            LockReason lockReason = LockReason.getLockReasonByLabel(lockReasonLabel);
            assertNotNull(lockReason);
        }
        
        for (LockReason lockReason : LockReason.values()) {
            assertTrue(lockReasons.contains(lockReason.getLabel()));
        }
    }

}
