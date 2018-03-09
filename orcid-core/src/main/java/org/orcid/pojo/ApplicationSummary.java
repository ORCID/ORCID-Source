package org.orcid.pojo;

import java.util.Date;
import java.util.Map;

import org.orcid.jaxb.model.message.ScopePathType;

public class ApplicationSummary {

    private String orcidUri;

    private String orcidPath;

    private String orcidHost;

    private String name;

    private String groupOrcidUri;

    private String groupOrcidPath;

    private String groupOrcidHost;

    private String groupName;

    private String websiteValue;

    private Date approvalDate;

    private Map<ScopePathType, String> scopePaths;
    
    private String tokenId;

    public String getOrcidUri() {
        return orcidUri;
    }

    public void setOrcidUri(String orcidUri) {
        this.orcidUri = orcidUri;
    }

    public String getOrcidPath() {
        return orcidPath;
    }

    public void setOrcidPath(String orcidPath) {
        this.orcidPath = orcidPath;
    }

    public String getOrcidHost() {
        return orcidHost;
    }

    public void setOrcidHost(String orcidHost) {
        this.orcidHost = orcidHost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupOrcidUri() {
        return groupOrcidUri;
    }

    public void setGroupOrcidUri(String groupOrcidUri) {
        this.groupOrcidUri = groupOrcidUri;
    }

    public String getGroupOrcidPath() {
        return groupOrcidPath;
    }

    public void setGroupOrcidPath(String groupOrcidPath) {
        this.groupOrcidPath = groupOrcidPath;
    }

    public String getGroupOrcidHost() {
        return groupOrcidHost;
    }

    public void setGroupOrcidHost(String groupOrcidHost) {
        this.groupOrcidHost = groupOrcidHost;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

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

    public Map<ScopePathType, String> getScopePaths() {
        return scopePaths;
    }

    public void setScopePaths(Map<ScopePathType, String> scopePaths) {
        this.scopePaths = scopePaths;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }        
}
