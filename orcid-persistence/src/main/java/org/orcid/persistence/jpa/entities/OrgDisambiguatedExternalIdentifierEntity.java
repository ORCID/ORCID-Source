package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 
 * @author Will Simpson
 * 
 */

@Entity
@Table(name = "org_disambiguated_external_identifier")
public class OrgDisambiguatedExternalIdentifierEntity extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String identifier;
    private String identifierType;
    private Boolean preferred;
    private OrgDisambiguatedEntity orgDisambiguated;

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "org_disambiguated_external_identifier_seq")
    @SequenceGenerator(name = "org_disambiguated_external_identifier_seq", sequenceName = "org_disambiguated_external_identifier_seq")
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Column(name = "identifier_type")
    public String getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

    @ManyToOne
    @JoinColumn(name = "org_disambiguated_id")
    public OrgDisambiguatedEntity getOrgDisambiguated() {
        return orgDisambiguated;
    }

    public void setOrgDisambiguated(OrgDisambiguatedEntity orgDisambiguated) {
        this.orgDisambiguated = orgDisambiguated;
    }
    
    @Column(name = "preferred")
    public Boolean getPreferred() {
        return preferred;
    }

    public void setPreferred(Boolean preferred) {
        this.preferred = preferred;
    }

}
