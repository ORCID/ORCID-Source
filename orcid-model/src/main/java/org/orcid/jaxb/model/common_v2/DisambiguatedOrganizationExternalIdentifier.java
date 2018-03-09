package org.orcid.jaxb.model.common_v2;

import java.io.Serializable;

public class DisambiguatedOrganizationExternalIdentifier implements Serializable {
    private static final long serialVersionUID = 2551196916230501285L;
    private Long id;
    private Long orgDisambiguatedId;
    private String identifier;
    private String identifierType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrgDisambiguatedId() {
        return orgDisambiguatedId;
    }

    public void setOrgDisambiguatedId(Long orgDisambiguatedId) {
        this.orgDisambiguatedId = orgDisambiguatedId;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

}
