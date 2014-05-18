/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.jpa.entities.keys;

import java.io.Serializable;

import org.orcid.persistence.jpa.entities.EmailType;

public class CustomEmailPk implements Serializable {
    private static final long serialVersionUID = 1L;
    private String clientDetailsEntity;
    private EmailType emailType;

    public CustomEmailPk() {

    }

    public CustomEmailPk(String clientDetailsEntity, EmailType emailType) {
        super();
        this.clientDetailsEntity = clientDetailsEntity;
        this.emailType = emailType;
    }

    public String getClientDetailsEntity() {
        return clientDetailsEntity;
    }

    public void setClientDetailsEntity(String clientDetailsEntity) {
        this.clientDetailsEntity = clientDetailsEntity;
    }

    public EmailType getEmailType() {
        return emailType;
    }

    public void setEmailType(EmailType emailType) {
        this.emailType = emailType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((clientDetailsEntity == null) ? 0 : clientDetailsEntity.hashCode());
        result = prime * result + ((emailType == null) ? 0 : emailType.hashCode());
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
        CustomEmailPk other = (CustomEmailPk) obj;
        if (clientDetailsEntity == null) {
            if (other.clientDetailsEntity != null)
                return false;
        } else if (!clientDetailsEntity.equals(other.clientDetailsEntity))
            return false;
        if (emailType != other.emailType)
            return false;
        return true;
    }

}
