package org.orcid.core.cache.impl;

import org.orcid.core.cache.OrcidString;
import org.orcid.core.cache.Retriever;
import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.pojo.ActivityTitle;
import org.orcid.pojo.WorkSummaryExtended;

import javax.annotation.Resource;
import java.util.List;

public class WorksTitleRetriever implements Retriever<OrcidString, List<ActivityTitle>> {

    @Resource(name = "workManagerReadOnlyV3")
    private WorkManagerReadOnly workManagerReadOnly;

    @Override
    public List<ActivityTitle> retrieve(OrcidString key) {
        return workManagerReadOnly.getWorksTitle(key.getOrcid());
    }
}
