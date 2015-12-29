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
package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Angel Montenegro
 * */

@Entity
@Table(name = "internal_sso")
public class InternalSSOEntity extends BaseEntity<String> {

    private static final long serialVersionUID = 1L;
    private String orcid;
    private String token;

    @Id
    @Override
    @Column(name = "orcid", length = 19)
    public String getId() {
        return orcid;
    }
    
    public void setId(String orcid) {
        this.orcid = orcid;
    }    

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
