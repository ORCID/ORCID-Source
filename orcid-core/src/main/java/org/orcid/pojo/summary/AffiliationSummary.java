package org.orcid.pojo.summary;

import org.orcid.core.utils.v3.SourceUtils;
import org.orcid.pojo.ajaxForm.AffiliationForm;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class AffiliationSummary {
    private String organizationName;
    private String url;
    private String startDate;
    private String endDate;
    private String role;
    private String type;
    private boolean validated;
    private long putCode;

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
    
    public long getPutCode() {
        return putCode;
    }

    public void setPutCode(long putCode) {
        this.putCode = putCode;
    }

    public static AffiliationSummary valueOf(AffiliationForm as, String orcid, String type) {
        AffiliationSummary form = new AffiliationSummary();

        if (as != null) {
            if(!PojoUtil.isEmpty(as.getPutCode())) {
                form.setPutCode(Long.valueOf(as.getPutCode().getValue()));
            }
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
                form.setValidated(!SourceUtils.isSelfAsserted(as, orcid));
            }
        }
        return form;
    }

    public static AffiliationSummary valueof(org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary as, String orcid, String type) {
        AffiliationSummary form = new AffiliationSummary();
        if(as != null) {
            form.setType(type);
            
            if(as.getOrganization() != null && as.getOrganization().getName() != null) {
                form.setOrganizationName(as.getOrganization().getName());
            }
            
            if(as.getUrl() != null && !PojoUtil.isEmpty(as.getUrl().getValue())) {
                form.setUrl(as.getUrl().getValue());
            }
            
            if(as.getStartDate() != null) {
                form.setStartDate(as.getStartDate().toString());
            }
            
            if(as.getEndDate() != null) {
                form.setEndDate(as.getEndDate().toString());
            }
            
            if(as.getRoleTitle() != null) {
                form.setRole(as.getRoleTitle());
            }
            
            if(as.getSource() != null) {
                form.setValidated(!SourceUtils.isSelfAsserted(as.getSource(), orcid));
            }
            
            if(as.getPutCode() != null) {
                form.setPutCode(as.getPutCode());
            }
        }
        return form;
    }
    
    private static String getDate(Date date) {
        return date != null ? date.toFuzzyDate().toString() : null;
    }
}
