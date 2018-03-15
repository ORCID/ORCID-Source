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
package org.orcid.core.cache.impl;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.cache.OrcidString;
import org.orcid.core.cache.Retriever;
import org.orcid.persistence.dao.SalesForceConnectionDao;
import org.orcid.persistence.jpa.entities.SalesForceConnectionEntity;

public class SalesForceConnectionEntityRetriever implements Retriever<OrcidString, List<SalesForceConnectionEntity>> {
    
    @Resource
    private SalesForceConnectionDao salesForceConnectionDao;

    @Override
    public List<SalesForceConnectionEntity> retrieve(OrcidString key) {
        return salesForceConnectionDao.findByOrcid(key.getOrcid());
    }

}
