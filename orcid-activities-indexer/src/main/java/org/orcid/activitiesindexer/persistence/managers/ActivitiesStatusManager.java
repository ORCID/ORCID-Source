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
package org.orcid.activitiesindexer.persistence.managers;

import java.util.List;

import org.orcid.activitiesindexer.listener.persistence.dao.ActivitiesStatusDao;
import org.orcid.activitiesindexer.persistence.entities.ActivitiesStatusEntity;
import org.orcid.activitiesindexer.persistence.util.ActivityType;
import org.orcid.activitiesindexer.persistence.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ActivitiesStatusManager {

    @Autowired
    private ActivitiesStatusDao dao;

    @Transactional
    public void markAsSent(String orcid, ActivityType type) {
        if (dao.exists(orcid)) {
            dao.success(orcid, type);
        } else {
            dao.create(orcid, type, Constants.OK);
        }
    }

    @Transactional
    public void markAsFailed(String orcid, ActivityType type) {
        if (dao.exists(orcid)) {
            dao.updateFailCount(orcid, type);
        } else {
            dao.create(orcid, type, Constants.FIRST_FAIL);
        }
    }

    @Transactional
    public void markAllAsSent(String orcid) {
        dao.successAll(orcid);
    }

    @Transactional
    public void markAllAsFailed(String orcid) {
        dao.failAll(orcid);
    }

    public List<ActivitiesStatusEntity> getFailedElements(int batchSize) {
        return dao.getFailedElements(batchSize);
    }
}
