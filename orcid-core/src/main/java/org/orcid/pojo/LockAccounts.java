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
package org.orcid.pojo;

public class LockAccounts {
    
    private String orcidsToLock;
    
    private String lockReason;

    public String getOrcidsToLock() {
        return orcidsToLock;
    }

    public void setOrcidsToLock(String orcidsToLock) {
        this.orcidsToLock = orcidsToLock;
    }

    public String getLockReason() {
        return lockReason;
    }

    public void setLockReason(String lockReason) {
        this.lockReason = lockReason;
    }
    
}
