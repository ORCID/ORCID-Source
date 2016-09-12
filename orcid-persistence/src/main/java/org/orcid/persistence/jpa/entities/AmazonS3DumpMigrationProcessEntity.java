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

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "amazon_s3_dump_migration_process")
public class AmazonS3DumpMigrationProcessEntity extends BaseEntity<String> {
    
    /**
     * 
     */
    private static final long serialVersionUID = -7894070905806319722L;
    String orcid;
    Date date;
    
    @Id
    @Override
    public String getId() {
        return orcid;
    }

    public void setId(String id) {
        this.orcid = id;
    }
    
    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }        
}
