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

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the userconnection database table.
 * 
 */
@Entity
@Table(name="Userconnection")
public class UserconnectionEntity extends BaseEntity<UserconnectionPK> implements Comparable<GroupIdRecordEntity> {
	private static final long serialVersionUID = 1L;

	private UserconnectionPK id;

	private String accesstoken;

	private String displayname;

	private String email;

	private Long expiretime;

	private String imageurl;

	private Timestamp lastLogin;

	private String orcid;

	private String profileurl;

	private Integer rank;

	private String refreshtoken;

	private String secret;
	
	private boolean isLinked;

	public UserconnectionEntity() {
	}

	@EmbeddedId
	public UserconnectionPK getId() {
		return this.id;
	}

	public void setId(UserconnectionPK id) {
		this.id = id;
	}

	public String getAccesstoken() {
		return this.accesstoken;
	}

	public void setAccesstoken(String accesstoken) {
		this.accesstoken = accesstoken;
	}

	public String getDisplayname() {
		return this.displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getExpiretime() {
		return this.expiretime;
	}

	public void setExpiretime(Long expiretime) {
		this.expiretime = expiretime;
	}

	public String getImageurl() {
		return this.imageurl;
	}

	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}

	@Column(name="last_login")
	public Timestamp getLastLogin() {
		return this.lastLogin;
	}

	public void setLastLogin(Timestamp lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getOrcid() {
		return this.orcid;
	}

	public void setOrcid(String orcid) {
		this.orcid = orcid;
	}

	public String getProfileurl() {
		return this.profileurl;
	}

	public void setProfileurl(String profileurl) {
		this.profileurl = profileurl;
	}

	public Integer getRank() {
		return this.rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public String getRefreshtoken() {
		return this.refreshtoken;
	}

	public void setRefreshtoken(String refreshtoken) {
		this.refreshtoken = refreshtoken;
	}

	public String getSecret() {
		return this.secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	@Override
	public int compareTo(GroupIdRecordEntity o) {
		return 0;
	}

	@Column(name="is_linked")
	public boolean isLinked() {
		return isLinked;
	}

	public void setLinked(boolean isLinked) {
		this.isLinked = isLinked;
	}
}