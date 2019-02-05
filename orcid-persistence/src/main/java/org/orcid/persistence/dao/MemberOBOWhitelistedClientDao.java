package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.MemberOBOWhitelistedClientEntity;

public interface MemberOBOWhitelistedClientDao {

    List<MemberOBOWhitelistedClientEntity> getWhitelistForClient(String clientDetailsId);

}