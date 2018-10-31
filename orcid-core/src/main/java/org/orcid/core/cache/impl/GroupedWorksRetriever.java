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

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.orcid.core.cache.OrcidString;
import org.orcid.core.cache.Retriever;
import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.jaxb.model.v3.rc2.record.summary.Works;

/**
 * 
 * @author Will Simpson
 *
 */
public class GroupedWorksRetriever implements Retriever<OrcidString, Works> {


    @Resource(name = "workManagerReadOnlyV3")
    private WorkManagerReadOnly workManagerReadOnly;

    @Override
    @Transactional
    public Works retrieve(OrcidString key) {
        return workManagerReadOnly.getWorksAsGroups(key.getOrcid());
    }

}
