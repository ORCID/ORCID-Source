package org.orcid.core.adapter.mapstruct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.orcid.core.contributors.roles.ContributorRoleConverter;
import org.orcid.core.contributors.roles.InvalidContributorRoleException;
import org.orcid.core.contributors.roles.credit.CreditRole;
import org.orcid.core.contributors.roles.fundings.LegacyFundingContributorRole;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.v3.release.record.FundingContributors;

/**
 * Replaces the old Orika-only {@code org.orcid.core.adapter.v3.converter.FundingContributorsConverter}.
 * Plain, framework-free conversion logic - no Orika dependency. Not Spring-managed
 * (constructed per mapper-facade build with a runtime role-converter dependency).
 */
public class FundingContributorsMapperV3 {

    private ContributorRoleConverter roleConverter;

    public FundingContributorsMapperV3(ContributorRoleConverter roleConverter) {
        this.roleConverter = roleConverter;
    }

    public String convertTo(FundingContributors source) {
        // convert role to db format
        source.getContributor().forEach(c -> {
            if (c.getContributorAttributes() != null && c.getContributorAttributes().getContributorRole() != null) {
                String providedRoleValue = c.getContributorAttributes().getContributorRole();
                String resolvedRoleValue = roleConverter.toDBRole(providedRoleValue);
                if (resolvedRoleValue == null) {
                    Map<String, String> exceptionParams = new HashMap<>();
                    exceptionParams.put("role", providedRoleValue);

                    List<String> legalValues = new ArrayList<>();
                    for (LegacyFundingContributorRole role : LegacyFundingContributorRole.values()) {
                        legalValues.add(role.value());
                    }
                    for (CreditRole role : CreditRole.values()) {
                        legalValues.add(role.value());
                    }
                    exceptionParams.put("validRoles", legalValues.toString());

                    throw new InvalidContributorRoleException(exceptionParams);
                }
                c.getContributorAttributes().setContributorRole(resolvedRoleValue);
            }
        });
        return JsonUtils.convertToJsonString(source);
    }

    public FundingContributors convertFrom(String source) {
        FundingContributors fundingContributors = JsonUtils.readObjectFromJsonString(source, FundingContributors.class);
        fundingContributors.getContributor().forEach(c -> c.setCreditName("".equals(c.getCreditName()) ? null : c.getCreditName()));

        // convert role to API format
        fundingContributors.getContributor().forEach(c -> {
            if (c.getContributorAttributes() != null && c.getContributorAttributes().getContributorRole() != null) {
                c.getContributorAttributes().setContributorRole(roleConverter.toRoleValue(c.getContributorAttributes().getContributorRole()));
            }
        });
        return fundingContributors;
    }
}
