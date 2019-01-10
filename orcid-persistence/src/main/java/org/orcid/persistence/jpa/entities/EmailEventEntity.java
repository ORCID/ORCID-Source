package org.orcid.persistence.jpa.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Entity
@Table(name = "email_event")
public class EmailEventEntity extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String email;    
    private EmailEventType type;

    public EmailEventEntity() {
    }

    public EmailEventEntity(String email, EmailEventType type) {
        this.email = email;
        this.type = type;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "email_event_seq")
    @SequenceGenerator(name = "email_event_seq", sequenceName = "email_event_seq")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String orcid) {
        this.email = orcid;
    }
    
    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "email_event_type")
    public EmailEventType getType() {
        return type;
    }

    public void setType(EmailEventType type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EmailEventEntity other = (EmailEventEntity) obj;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (type != other.type)
            return false;
        return true;
    }    
}
