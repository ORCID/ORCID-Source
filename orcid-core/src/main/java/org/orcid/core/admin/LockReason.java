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
package org.orcid.core.admin;

public enum LockReason {
    
    SPAM("Spam"), DISPUTE("Dispute"), UNCLAIMED("Unclaimed"), OTHER("Other");
    
    private String label;
    
    LockReason(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
    
    public static LockReason getLockReasonByLabel(String label) {
        for (LockReason lockReason : LockReason.values()) {
            if (lockReason.getLabel().equals(label)) {
                return lockReason;
            }
        }
        return null;
    }

}
