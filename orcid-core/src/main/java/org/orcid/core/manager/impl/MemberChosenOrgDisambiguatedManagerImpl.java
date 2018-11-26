package org.orcid.core.manager.impl;

import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
            MemberChosenOrgDisambiguatedEntity e = new MemberChosenOrgDisambiguatedEntity();
            e.setDateCreated(new Date());
            e.setLastModified(new Date());
            e.setSourceId(id.getOrgIdValue());
            e.setSourceType(id.getOrgIdType());
            
            if (forRemoval.contains(e)) {
                forRemoval.remove(e);
            } else {
                e = memberChosenOrgDisambiguatedDao.merge(e);
                markForReindexing(e.getOrgDisambiguatedEntity());
            }
        });
        
        forRemoval.stream().forEach(e -> {
            memberChosenOrgDisambiguatedDao.remove(e);
            markForReindexing(e.getOrgDisambiguatedEntity());
        });
    }

    private void markForReindexing(OrgDisambiguatedEntity orgDisambiguatedEntity) {
        orgDisambiguatedEntity.setIndexingStatus(IndexingStatus.PENDING);
        orgDisambiguatedDao.merge(orgDisambiguatedEntity);
    }

    
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        MemberChosenOrgDisambiguatedManager manager = (MemberChosenOrgDisambiguatedManager) context.getBean("memberChosenOrgDisambiguatedManager");
        manager.refreshMemberChosenOrgs();
    }
}
