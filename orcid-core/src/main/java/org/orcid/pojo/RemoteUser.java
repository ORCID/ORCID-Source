/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.pojo;

/**
 * 
 * @author Will Simpson
 *
 */
public class RemoteUser {

    private String userId;
    private String idType;

    public RemoteUser(String userId, String idType) {
        this.userId = userId;
        this.idType = idType;
    }

    public String getUserId() {
        return userId;
    }

    public String getIdType() {
        return idType;
    }

}
