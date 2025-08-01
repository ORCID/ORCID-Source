package org.orcid.core.cache.impl;

import org.orcid.core.cache.OrcidString;
import org.orcid.core.cache.Retriever;
import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.pojo.WorkSummaryExtended;

import javax.annotation.Resource;
import java.util.List;

public class FeaturedGroupedWorksExtendedRetriever implements Retriever<OrcidString, List<WorkSummaryExtended>> {

    @Resource(name = "workManagerReadOnlyV3")
    private WorkManagerReadOnly workManagerReadOnly;

    @Override
    public List<WorkSummaryExtended> retrieve(OrcidString key) {
        return workManagerReadOnly.getFeaturedWorksSummaryExtended(key.getOrcid());
    }
}
