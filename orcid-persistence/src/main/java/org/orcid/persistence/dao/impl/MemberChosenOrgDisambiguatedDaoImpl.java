package org.orcid.persistence.dao.impl;

import org.orcid.persistence.dao.MemberChosenOrgDisambiguatedDao;
import org.orcid.persistence.jpa.entities.MemberChosenOrgDisambiguatedEntity;

public class MemberChosenOrgDisambiguatedDaoImpl extends GenericDaoImpl<MemberChosenOrgDisambiguatedEntity, Long> implements MemberChosenOrgDisambiguatedDao {

    public MemberChosenOrgDisambiguatedDaoImpl() {
        super(MemberChosenOrgDisambiguatedEntity.class);
    }
    
}