package org.orcid.core.manager;

import java.util.List;

import org.orcid.core.salesforce.model.OrgId;

public interface MemberChosenOrgDisambiguatedManager {
    
    void refreshMemberChosenOrgs(List<OrgId> orgIds);

}
