/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.orcid.persistence.jpa.entities.ClientDetailsEntity;

@XmlRootElement
public class IdentifierType implements Serializable{

    private static final long serialVersionUID = 1L;
    public static final String PRIMARY_USE_WORK = "work";
    public static final String PRIMARY_USE_FUNDING = "funding";
    
    
    private String name;
    private Long id;
    private String validationRegex;
    private String resolutionPrefix;
    private String description;
    private Date dateCreated;
    private Date lastModified;
    private Boolean caseSensitive;
    private String primaryUse;
    private Boolean deprecated = Boolean.FALSE;    
    
    private ClientDetailsEntity sourceClient;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getPutCode() {
        return id;
    }
    public void setPutCode(Long id) {
        this.id = id;
    }
    public String getValidationRegex() {
        return validationRegex;
    }
    @XmlTransient
    public void setValidationRegex(String validationRegex) {
        this.validationRegex = validationRegex;
    }
    public String getResolutionPrefix() {
        return resolutionPrefix;
    }
    public void setResolutionPrefix(String resolutionPrefix) {
        this.resolutionPrefix = resolutionPrefix;
    }
    public Boolean getDeprecated() {
        return deprecated;
    }
    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }
    public Date getDateCreated() {
        return dateCreated;
    }
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
    public Date getLastModified() {
        return lastModified;
    }
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
    @XmlTransient
    public ClientDetailsEntity getSourceClient() {
        return sourceClient;
    }
    public void setSourceClient(ClientDetailsEntity sourceClient) {
        this.sourceClient = sourceClient;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Boolean getCaseSensitive() {
        return caseSensitive;
    }
    public void setCaseSensitive(Boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }
    public String getPrimaryUse() {        
        return primaryUse;
    }
    public void setPrimaryUse(String primaryUse) {
        this.primaryUse = primaryUse;
    }
    @Override
    public String toString() {
        return "IdentifierType [name=" + name + ", id=" + id + ", validationRegex=" + validationRegex + ", resolutionPrefix=" + resolutionPrefix + ", description="
                + description + ", dateCreated=" + dateCreated + ", lastModified=" + lastModified + ", caseSensitive=" + caseSensitive + ", primaryUse=" + primaryUse
                + ", deprecated=" + deprecated + ", sourceClient=" + sourceClient + "]";
    }

}
