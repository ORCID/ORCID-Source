package org.orcid.pojo.summary;

import org.orcid.core.utils.v3.SourceUtils;
import org.orcid.jaxb.model.v3.release.common.FuzzyDate;
import org.orcid.pojo.ajaxForm.PojoUtil;

import java.util.ArrayList;
import java.util.List;

public class AffiliationSummary {
    public String organizationName;
    public String url;
    public String startDate;
    public String endDate;
    public String role;
    public String type;
    public boolean validatedOrSelfAsserted;

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isValidatedOrSelfAsserted() {
        return validatedOrSelfAsserted;
    }

    public void setValidatedOrSelfAsserted(boolean validatedOrSelfAsserted) {
        this.validatedOrSelfAsserted = validatedOrSelfAsserted;
    }

    public static List<AffiliationSummary> valueOf(List<org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary> affiliationGroupForms, String orcid, String type) {
        List<AffiliationSummary> affiliationSummaries = new ArrayList<>();

        affiliationGroupForms.forEach(affiliationGroupForm -> {
            affiliationSummaries.add(AffiliationSummary.valueOf(affiliationGroupForm, orcid, type));
        });

        return affiliationSummaries;
    }

    public static AffiliationSummary valueOf(org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary as, String orcid, String type) {
        AffiliationSummary form = new AffiliationSummary();

        if (as != null) {
            if (as.getOrganization() != null && as.getOrganization().getName() != null && as.getOrganization().getName().trim().length() != 0) {
                form.setOrganizationName(as.getOrganization().getName());
            }

            if (!PojoUtil.isEmpty(as.getUrl())) {
                form.setUrl(as.getUrl().getValue());
            }

            if (!PojoUtil.isEmpty(as.getStartDate())) {
                form.setStartDate(getDate(as.getStartDate()));
            }

            if (!PojoUtil.isEmpty(as.getEndDate())) {
                form.setEndDate(getDate(as.getEndDate()));
            }

            if (!PojoUtil.isEmpty(as.getRoleTitle())) {
                form.setRole(as.getRoleTitle());
            }

            form.setType(type);

            if (as.getSource() != null) {
                form.setValidatedOrSelfAsserted(SourceUtils.isSelfAsserted(as.getSource(), orcid));
            }
        }
        return form;
    }

    private static String getDate(FuzzyDate date) {
        return date != null ? date.toString() : null;
    }
}
