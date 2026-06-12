package org.orcid.persistence.jpa.entities;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * 
 * @author Will Simpson
 * 
 */
@Table(name = "email_domain_to_org_id")
@Entity
public class EmailDomainToOrgIdEntity extends BaseEntity<Long> {
    
    private static final long serialVersionUID = 7138838021634315502L;

    public enum DomainCategory {PERSONAL, PROFESSIONAL, UNDEFINED}
    
    private Long id;
    private EmailDomainEntity emailDomian;
    private OrgDisambiguatedEntity orgDisambiguated;
    

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "email_domain_to_org_id_seq")
    @SequenceGenerator(name = "email_domain_to_org_id_seq", sequenceName = "email_domain_to_org_id_seq", allocationSize = 1)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "email_domian_id")
    public EmailDomainEntity getEmailDomian() {
        return emailDomian;
    }

    public void setEmailDomian(EmailDomainEntity emailDomian) {
        this.emailDomian = emailDomian;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "org_disambiguated_id")
    public OrgDisambiguatedEntity getOrgDisambiguated() {
        return orgDisambiguated;
    }

    public void setOrgDisambiguated(OrgDisambiguatedEntity orgDisambiguated) {
        this.orgDisambiguated = orgDisambiguated;
    }
}
