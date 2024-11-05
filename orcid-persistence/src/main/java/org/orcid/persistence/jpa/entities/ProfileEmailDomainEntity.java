package org.orcid.persistence.jpa.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author Andrej Romanov
 *
 */
@Entity
@Table(name = "profile_email_domain")
public class ProfileEmailDomainEntity extends BaseEntity<Long>  {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String orcid;
    private String emailDomain;
    private String visibility;
    private Date dateCreated;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "profile_email_domain_seq")
    @SequenceGenerator(name = "profile_email_domain_seq", sequenceName = "profile_email_domain_seq", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "orcid")
    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    @Column(name = "email_domain")
    public String getEmailDomain() {
        return emailDomain;
    }

    public void setEmailDomain(String emailDomain) {
        this.emailDomain = emailDomain;
    }

    @Column(name = "visibility")
    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    @Column(name = "date_created")
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProfileEmailDomainEntity other = (ProfileEmailDomainEntity) obj;
        return Objects.equals(emailDomain, other.emailDomain) && Objects.equals(orcid, other.orcid)
                && Objects.equals(id, other.id) && Objects.equals(visibility, other.visibility);
    }

}
