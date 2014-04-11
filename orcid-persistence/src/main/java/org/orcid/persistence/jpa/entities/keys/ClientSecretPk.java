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

/**
 * 
 * @author Will Simpson
 */
public class ClientSecretPk implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String clientDetailsEntity;
    private String clientSecret;

    public ClientSecretPk() {
    }

    public ClientSecretPk(String clientDetailsEntity, String scopeType) {
        this.clientDetailsEntity = clientDetailsEntity;
        this.clientSecret = scopeType;
    }

    public String getClientDetailsEntity() {
        return clientDetailsEntity;
    }

    public void setClientDetailsEntity(String clientDetailsEntity) {
        this.clientDetailsEntity = clientDetailsEntity;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String scopeType) {
        this.clientSecret = scopeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ClientSecretPk that = (ClientSecretPk) o;

        if (!clientDetailsEntity.equals(that.clientDetailsEntity))
            return false;
        if (!clientSecret.equals(that.clientSecret))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = clientDetailsEntity.hashCode();
        result = 31 * result + clientSecret.hashCode();
        return result;
    }
}
