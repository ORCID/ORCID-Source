package org.orcid.pojo.summary;

import org.orcid.core.utils.v3.SourceUtils;
import org.orcid.pojo.ajaxForm.AffiliationForm;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class AffiliationSummary {
    public String organizationName;
    public String url;
    public String startDate;
    public String endDate;
    public String role;
    public String type;
    public boolean validated;

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

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public static AffiliationSummary valueOf(AffiliationForm as, String orcid, String type) {
        AffiliationSummary form = new AffiliationSummary();

        if (as != null) {
            if (!PojoUtil.isEmpty(as.getAffiliationName())) {
                form.setOrganizationName(as.getAffiliationName().getValue());
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
                form.setRole(as.getRoleTitle().getValue());
            }

            form.setType(type);

            if (as.getSource() != null) {
                form.setValidated(SourceUtils.isSelfAsserted(as.getSource(), orcid));
            }
        }
        return form;
    }

    private static String getDate(Date date) {
        return date != null ? date.toFuzzyDate().toString() : null;
    }
}
