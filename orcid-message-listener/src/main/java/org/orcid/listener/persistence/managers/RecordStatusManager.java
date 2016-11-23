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
package org.orcid.listener.persistence.managers;

import org.orcid.listener.persistence.dao.RecordStatusDao;
import org.orcid.listener.persistence.util.AvailableBroker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RecordStatusManager {

    private static Integer FIRST_FAIL = 1;
    
    @Autowired
    private RecordStatusDao dao;
    
    @Transactional
    public void markAsSent(String orcid, AvailableBroker broker) {
        if(dao.exists(orcid)) {
            dao.updateStatus(orcid, broker, 0);
        } else {
            dao.create(orcid, broker, 0);
        }
    }
    
    @Transactional
    public void markAsFailed(String orcid, AvailableBroker broker) {
        if(dao.exists(orcid)) {
            dao.updateStatus(orcid, broker);
        } else {
            dao.create(orcid, broker, FIRST_FAIL);
        }
    }
}
