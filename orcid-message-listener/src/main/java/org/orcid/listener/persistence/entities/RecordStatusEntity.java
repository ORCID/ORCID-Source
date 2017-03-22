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
package org.orcid.listener.persistence.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "record_status")
public class RecordStatusEntity {
	private Date dateCreated;
	private Date lastModified;
	private String orcid;
	private Integer dumpStatus12Api = 0;
	private Integer dumpStatus20Api = 0;
	private Integer solrStatus20Api = 0;

	@Id
	@Column(name = "orcid", length = 19)
	public String getId() {
		return orcid;
	}

	public void setId(String orcid) {
		this.orcid = orcid;
	}

	@Column(name = "date_created")
	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Column(name = "last_modified")
	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	@Column(name = "api_1_2_dump_status")
	public Integer getDumpStatus12Api() {
		return dumpStatus12Api;
	}

	public void setDumpStatus12Api(Integer dumpStatus12Api) {
		this.dumpStatus12Api = dumpStatus12Api;
	}

	@Column(name = "api_2_0_dump_status")
	public Integer getDumpStatus20Api() {
		return dumpStatus20Api;
	}

	public void setDumpStatus20Api(Integer dumpStatus20Api) {
		this.dumpStatus20Api = dumpStatus20Api;
	}
	
        @Column(name = "api_2_0_solr_status")
        public Integer getSolrStatus20Api() {
            return solrStatus20Api;
        }
    
        public void setSolrStatus20Api(Integer solrStatus20Api) {
            this.solrStatus20Api = solrStatus20Api;
        }
}
