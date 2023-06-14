package org.orcid.pojo;

import java.util.Date;
import java.util.Map;

public class ApplicationSummary {

    private String name;

    private String clientId;

    private String websiteValue;

    private Date approvalDate;

    private Map<String, String> scopePaths;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClientId() { return clientId; }

    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getWebsiteValue() {
        return websiteValue;
    }

    public void setWebsiteValue(String websiteValue) {
        this.websiteValue = websiteValue;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public Map<String, String> getScopePaths() {
        return scopePaths;
    }

    public void setScopePaths(Map<String, String> scopePaths) {
        this.scopePaths = scopePaths;
    }

}
