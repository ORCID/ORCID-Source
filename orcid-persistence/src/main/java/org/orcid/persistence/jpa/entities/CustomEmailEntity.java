package org.orcid.persistence.jpa.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.orcid.persistence.jpa.entities.keys.CustomEmailPk;

@Entity
@Table(name = "custom_email")
public class CustomEmailEntity extends BaseEntity<CustomEmailPk>{
    
    private static final long serialVersionUID = 1L;
    private ClientDetailsEntity clientDetailsEntity;
    private EmailType emailType;
    private String sender;
    private String subject;
    private String content; 
    
    @Override
    public CustomEmailPk getId() {        
        return new CustomEmailPk(clientDetailsEntity.getClientId(), emailType);
    }    
    
    @Id
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinColumn(name = "client_details_id")
    public ClientDetailsEntity getClientDetailsEntity() {
        return clientDetailsEntity;
    }

    public void setClientDetailsEntity(ClientDetailsEntity clientDetails) {
        this.clientDetailsEntity = clientDetails;
    }
    
    @Id
    @Column(name = "email_type")
    @Enumerated(EnumType.STRING)
    public EmailType getEmailType() {
        return emailType;
    }

    public void setEmailType(EmailType emailType) {
        this.emailType = emailType;
    }

    @Column(name = "content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(name = "sender")
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
    
    @Column(name = "subject")
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }          
}
