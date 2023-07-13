package org.orcid.pojo.summary;

import org.orcid.jaxb.model.v3.release.common.FuzzyDate;
import org.orcid.pojo.ajaxForm.PojoUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AffiliationSummary {
    public String organizationName;
    public String url;
    public Date startDate;
    public Date endDate;
    public String role;
    public String title;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
            if (as.getOrganization().getName() == null || as.getOrganization().getName().trim().length() == 0) {
                form.setOrganizationName(as.getOrganization().getName());
            }

            if (!PojoUtil.isEmpty(as.getUrl())) {
                form.setUrl(as.getUrl().getValue());
            }

            if (!PojoUtil.isEmpty(as.getStartDate())) {
                FuzzyDate date = as.getStartDate();
//                form.setStartDate(new Date(date.getYear().getValue(), date.getMonth(), date.getDay()));
            }

            if (!PojoUtil.isEmpty(as.getEndDate())) {
//                form.setStartDate(affiliationForm.getEndDate().toJavaDate());
            }

            if (!PojoUtil.isEmpty(as.getRoleTitle())) {
                form.setRole(as.getRoleTitle());
            }

            if (!PojoUtil.isEmpty(as.getDepartmentName())) {
                form.setOrganizationName(as.getDepartmentName());
            }

            if (!PojoUtil.isEmpty(as.getRoleTitle())) {
                form.setRole(as.getRoleTitle());
            }

            form.setType(type);

            if (as.getSource() != null) {
                form.setValidatedOrSelfAsserted(as.getSource().getSourceName().getContent().equals(orcid));
            }
        }
        return form;
    }
}
