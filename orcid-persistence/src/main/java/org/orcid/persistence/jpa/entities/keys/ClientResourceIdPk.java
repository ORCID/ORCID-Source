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
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 13/02/2012
 */
public class ClientResourceIdPk implements Serializable {

    private String clientDetailsEntity;
    private String resourceId;

    public ClientResourceIdPk() {
    }

    public ClientResourceIdPk(String clientDetailsEntity, String resourceId) {
        this.clientDetailsEntity = clientDetailsEntity;
        this.resourceId = resourceId;
    }

    public String getClientDetailsEntity() {
        return clientDetailsEntity;
    }

    public void setClientDetailsEntity(String clientDetailsEntity) {
        this.clientDetailsEntity = clientDetailsEntity;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ClientResourceIdPk that = (ClientResourceIdPk) o;

        if (!clientDetailsEntity.equals(that.clientDetailsEntity))
            return false;
        if (!resourceId.equals(that.resourceId))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = clientDetailsEntity.hashCode();
        result = 31 * result + resourceId.hashCode();
        return result;
    }
}
