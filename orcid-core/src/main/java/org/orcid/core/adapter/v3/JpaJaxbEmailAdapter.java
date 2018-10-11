package org.orcid.core.adapter.v3;

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.v3.rc2.record.Email;
import org.orcid.persistence.jpa.entities.EmailEntity;

public interface JpaJaxbEmailAdapter {
    EmailEntity toEmailEntity(Email email);

    Email toEmail(EmailEntity entity);
    
    List<Email> toEmailList(Collection<EmailEntity> entities);
    
    EmailEntity toEmailEntity(Email email, EmailEntity existing);
}
