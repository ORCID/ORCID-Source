package org.orcid.core.manager.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.MemberChosenOrgDisambiguatedManager;
import org.orcid.core.manager.SalesForceManager;
import org.orcid.core.salesforce.model.OrgId;
import org.orcid.persistence.dao.MemberChosenOrgDisambiguatedDao;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.MemberChosenOrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;

public class MemberChosenOrgDisambiguatedManagerImpl implements MemberChosenOrgDisambiguatedManager {

    @Resource
    private MemberChosenOrgDisambiguatedDao memberChosenOrgDisambiguatedDao;
    
    @Resource
    private OrgDisambiguatedDao orgDisambiguatedDao;
    
    @Resource
    private SalesForceManager salesForceManager; 

    @Override
    public synchronized void refreshMemberChosenOrgs() {
        List<OrgId> orgIds = salesForceManager.retrieveAllOrgIds();
        List<MemberChosenOrgDisambiguatedEntity> forRemoval = new ArrayList<>(memberChosenOrgDisambiguatedDao.getAll());
        orgIds.stream().forEach(id -> {
            OrgDisambiguatedEntity orgDisambiguated = orgDisambiguatedDao.findBySourceIdAndSourceType(id.getOrgIdValue(), id.getOrgIdType());
            if (orgDisambiguated != null) {
                MemberChosenOrgDisambiguatedEntity e = new MemberChosenOrgDisambiguatedEntity();
                e.setOrgDisambiguatedId(orgDisambiguated.getId());
                
                if (forRemoval.contains(e)) {
                    forRemoval.remove(e);
                } else {
                    memberChosenOrgDisambiguatedDao.merge(e);
                    markForReindexing(orgDisambiguated);
                }
            }
        });
        
        forRemoval.stream().forEach(e -> {
            memberChosenOrgDisambiguatedDao.remove(e);
            OrgDisambiguatedEntity org = orgDisambiguatedDao.find(e.getOrgDisambiguatedId());
            markForReindexing(org);
        });
    }

    private void markForReindexing(OrgDisambiguatedEntity orgDisambiguatedEntity) {
        orgDisambiguatedEntity.setIndexingStatus(IndexingStatus.PENDING);
        orgDisambiguatedDao.merge(orgDisambiguatedEntity);
    }

    
}
