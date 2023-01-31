package org.orcid.listener.persistence.managers;

import java.util.List;

import org.orcid.listener.persistence.dao.SearchEngineRecordStatusDao;
import org.orcid.listener.persistence.entities.SearchEngineRecordStatusEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SearchEngineRecordStatusManager {
    @Autowired
    private SearchEngineRecordStatusDao dao;

    public void setSolrFail(String orcid) {
        if (dao.exists(orcid)) {
            dao.setSolrFail(orcid);
        } else {
            dao.create(orcid, false);
        }
    }

    public void setSolrOk(String orcid) {
        if (dao.exists(orcid)) {
            dao.setSolrOk(orcid);
        } else {
            dao.create(orcid, true);
        }
    }

    public List<SearchEngineRecordStatusEntity> getFailedElements(int batchSize) {
        return dao.getFailedElements(batchSize);
    }
}
