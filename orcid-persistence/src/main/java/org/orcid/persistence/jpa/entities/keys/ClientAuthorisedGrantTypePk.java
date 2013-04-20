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
public class ClientAuthorisedGrantTypePk implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String clientId;
    private String grantType;

    public ClientAuthorisedGrantTypePk() {
    }

    public ClientAuthorisedGrantTypePk(String clientId, String authorisedGrantType) {
        this.clientId = clientId;
        this.grantType = authorisedGrantType;
    }

    public String getClientDetailsEntity() {
        return clientId;
    }

    public void setClientDetailsEntity(String clientId) {
        this.clientId = clientId;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String authorisedGrantType) {
        this.grantType = authorisedGrantType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ClientAuthorisedGrantTypePk that = (ClientAuthorisedGrantTypePk) o;

        if (!grantType.equals(that.grantType))
            return false;
        if (!clientId.equals(that.clientId))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = clientId.hashCode();
        result = 31 * result + grantType.hashCode();
        return result;
    }
}
