package org.orcid.core.manager.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.MemberChosenOrgDisambiguatedManager;
import org.orcid.core.salesforce.model.OrgId;
import org.orcid.persistence.dao.MemberChosenOrgDisambiguatedDao;
import org.orcid.persistence.jpa.entities.MemberChosenOrgDisambiguatedEntity;

public class MemberChosenOrgDisambiguatedManagerImpl implements MemberChosenOrgDisambiguatedManager {

    @Resource
    private MemberChosenOrgDisambiguatedDao memberChosenOrgDisambiguatedDao;

    @Override
    public void refreshMemberChosenOrgs(List<OrgId> orgIds) {
        memberChosenOrgDisambiguatedDao.removeAll();
        orgIds.stream().forEach(id -> {
            MemberChosenOrgDisambiguatedEntity e = new MemberChosenOrgDisambiguatedEntity();
            e.setDateCreated(new Date());
            e.setLastModified(new Date());
            e.setSourceId(id.getOrgIdValue());
            e.setSourceType(id.getOrgIdType());
            memberChosenOrgDisambiguatedDao.persist(e);
        });
    }

}
