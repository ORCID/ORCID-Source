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

import org.orcid.persistence.jpa.entities.ClientDetailsEntity;

public class IdentifierType implements Serializable{

    private static final long serialVersionUID = 1L;
    private String name;
    private Long id;
    private String validationRegex;
    private String resolutionPrefix;
    private String description;
    
    private Date dateCreated;
    private Date lastModified;
    
    @Override
    public String toString() {
        return "IdentifierType [name=" + name + ", id=" + id + ", validationRegex=" + validationRegex + ", resolutionPrefix=" + resolutionPrefix + ", dateCreated="
                + dateCreated + ", lastModified=" + lastModified + ", sourceClient=" + sourceClient + ", deprecated=" + deprecated + ", description=" + description +"]";
    }
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
    private Boolean deprecated = Boolean.FALSE;
    
    //source?
}
