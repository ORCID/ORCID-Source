package org.orcid.core.contributors.roles.fundings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.orcid.core.contributors.roles.ContributorRoleConverterImpl;
import org.orcid.core.contributors.roles.LegacyContributorRole;
import org.orcid.core.contributors.roles.credit.CreditRole;

public class FundingContributorRoleConverter extends ContributorRoleConverterImpl {

    @Override
    public Map<CreditRole, LegacyContributorRole> getMappings() {
        Map<CreditRole, LegacyContributorRole> mappings = new HashMap<>();
        mappings.put(CreditRole.SUPERVISION, LegacyFundingContributorRole.LEAD);
        return mappings;
    }
    
    @Override
    public List<LegacyContributorRole> getLegacyRoles() {
        return Arrays.asList(LegacyFundingContributorRole.values());
    }

}
