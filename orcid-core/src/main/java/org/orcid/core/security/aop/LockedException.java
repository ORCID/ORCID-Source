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
package org.orcid.core.security.aop;

import org.orcid.core.exception.ApplicationException;
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.locked.LockedRecord;

public class LockedException extends ApplicationException {
    private static final long serialVersionUID = -6900432299998784418L;
    private LockedRecord lockedRecord;

    public LockedException(String msg) {
        super(msg);
    }

    public LockedException(String msg, String orcid) {
        super(msg);
        lockedRecord = new LockedRecord();        
        OrcidIdentifier orcidIdentifier = new OrcidIdentifier();
        orcidIdentifier.setPath(orcid);   
        lockedRecord.setOrcidIdentifier(orcidIdentifier);
    }

    public LockedException(String msg, OrcidIdentifier orcidIdentifier) {
        super(msg);
        lockedRecord = new LockedRecord();
        lockedRecord.setOrcidIdentifier(orcidIdentifier);        
    }

    public LockedRecord getLockedRecord() {
        return lockedRecord;
    }

    public void setLockedRecord(LockedRecord lockedRecord) {
        this.lockedRecord = lockedRecord;
    }
}
