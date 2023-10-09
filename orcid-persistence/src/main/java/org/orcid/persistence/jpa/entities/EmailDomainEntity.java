package org.orcid.persistence.jpa.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 
 * @author Will Simpson
 * 
 */
@Table(name = "email_domain")
@Entity
public class EmailDomainEntity extends BaseEntity<Long> {
    
    private static final long serialVersionUID = 7138838021634315502L;

    public enum DomainCategory {PERSONAL, PROFESSIONAL, UNDEFINED}
    
    private Long id;
    private String emailDomain; 
    private DomainCategory category;
    

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "email_domain_seq")
    @SequenceGenerator(name = "email_domain_seq", sequenceName = "email_domain_seq", allocationSize = 1)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "email_domain")
    public String getEmailDomain() {
        return emailDomain;
    }

    public void setCategory(DomainCategory category) {
        this.category = category;
    }

    @Column(name = "category")
    public DomainCategory getCategory() {
        return category;
    }
    
    public void setEmailDomain(String emailDomain) {
        this.emailDomain = emailDomain;
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, emailDomain, id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EmailDomainEntity other = (EmailDomainEntity) obj;
        return category == other.category && Objects.equals(emailDomain, other.emailDomain) && Objects.equals(id, other.id);
    }    
}
