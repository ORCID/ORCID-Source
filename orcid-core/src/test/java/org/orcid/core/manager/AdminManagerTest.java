/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
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
