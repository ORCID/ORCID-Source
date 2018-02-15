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
