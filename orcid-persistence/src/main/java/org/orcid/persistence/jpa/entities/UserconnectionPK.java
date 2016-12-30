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

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the userconnection database table.
 * 
 */
@Embeddable
public class UserconnectionPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String userid;

	private String providerid;

	private String provideruserid;

	public UserconnectionPK() {
	}
	
	public UserconnectionPK(String userid, String providerid, String provideruserid) {
		this.userid = userid;
		this.providerid = providerid;
		this.provideruserid = provideruserid;
	}
	public String getUserid() {
		return this.userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getProviderid() {
		return this.providerid;
	}
	public void setProviderid(String providerid) {
		this.providerid = providerid;
	}
	public String getProvideruserid() {
		return this.provideruserid;
	}
	public void setProvideruserid(String provideruserid) {
		this.provideruserid = provideruserid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof UserconnectionPK)) {
			return false;
		}
		UserconnectionPK castOther = (UserconnectionPK)other;
		return 
			this.userid.equals(castOther.userid)
			&& this.providerid.equals(castOther.providerid)
			&& this.provideruserid.equals(castOther.provideruserid);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.userid.hashCode();
		hash = hash * prime + this.providerid.hashCode();
		hash = hash * prime + this.provideruserid.hashCode();
		
		return hash;
	}

    @Override
    public String toString() {
        return "UserconnectionPK [userid=" + userid + ", providerid=" + providerid + ", provideruserid=" + provideruserid + "]";
    }
}