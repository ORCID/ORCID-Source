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
public class ClientRedirectUriPk implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3948069038449324678L;
    private String clientDetailsEntity;
    private String redirectUri;

    public ClientRedirectUriPk() {
    }

    public ClientRedirectUriPk(String clientDetailsEntity, String redirectUri) {
        this.clientDetailsEntity = clientDetailsEntity;
        this.redirectUri = redirectUri;
    }

    public String getClientDetailsEntity() {
        return clientDetailsEntity;
    }

    public void setClientDetailsEntity(String clientDetailsEntity) {
        this.clientDetailsEntity = clientDetailsEntity;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ClientRedirectUriPk that = (ClientRedirectUriPk) o;

        if (!clientDetailsEntity.equals(that.clientDetailsEntity))
            return false;
        if (!redirectUri.equals(that.redirectUri))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = clientDetailsEntity.hashCode();
        result = 31 * result + redirectUri.hashCode();
        return result;
    }
}
