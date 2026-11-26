package org.orcid.persistence.jpa.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DELEGATE")
public class NotificationDelegateEntity extends NotificationAdministrativeEntity {

    private static final long serialVersionUID = 1L;
}
