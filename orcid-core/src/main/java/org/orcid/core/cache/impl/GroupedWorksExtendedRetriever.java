package org.orcid.core.cache.impl;

import org.orcid.core.cache.OrcidString;
import org.orcid.core.cache.Retriever;
import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.pojo.WorksExtended;

import javax.annotation.Resource;
import javax.transaction.Transactional;

public class GroupedWorksExtendedRetriever implements Retriever<OrcidString, WorksExtended> {

    @Resource(name = "workManagerReadOnlyV3")
    private WorkManagerReadOnly workManagerReadOnly;

    @Override
    @Transactional
    public WorksExtended retrieve(OrcidString key) {
        return workManagerReadOnly.getWorksExtendedAsGroups(key.getOrcid());
    }

}
