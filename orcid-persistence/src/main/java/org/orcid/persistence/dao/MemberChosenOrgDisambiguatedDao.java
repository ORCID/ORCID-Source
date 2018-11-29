package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.MemberChosenOrgDisambiguatedEntity;

public interface MemberChosenOrgDisambiguatedDao {

    List<MemberChosenOrgDisambiguatedEntity> getAll();

    MemberChosenOrgDisambiguatedEntity merge(MemberChosenOrgDisambiguatedEntity entity);

    void remove(MemberChosenOrgDisambiguatedEntity entity);

}