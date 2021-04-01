package org.orcid.core.contributors.roles.works;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.orcid.core.contributors.roles.ContributorRoleConverterImpl;
import org.orcid.core.contributors.roles.LegacyContributorRole;
import org.orcid.core.contributors.roles.credit.CreditRole;

public class WorkContributorRoleConverter extends ContributorRoleConverterImpl {

    protected Map<CreditRole, LegacyContributorRole> getMappings() {
        Map<CreditRole, LegacyContributorRole> mappings = new HashMap<>();
        mappings.put(CreditRole.WRITING_ORIGINAL_DRAFT, LegacyWorkContributorRole.AUTHOR);
        mappings.put(CreditRole.WRITING_REVIEW_EDITING, LegacyWorkContributorRole.EDITOR);
        mappings.put(CreditRole.INVESTIGATION, LegacyWorkContributorRole.CO_INVESTIGATOR);
        mappings.put(CreditRole.SUPERVISION, LegacyWorkContributorRole.PRINCIPAL_INVESTIGATOR);
        return mappings;
    }
    
    @Override
    public List<LegacyContributorRole> getLegacyRoles() {
        return Arrays.asList(LegacyWorkContributorRole.values());
    }

}
