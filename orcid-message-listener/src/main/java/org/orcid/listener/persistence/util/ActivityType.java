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
package org.orcid.listener.persistence.util;

public enum ActivityType {
	DISTINCTIONS("/distinctions/"),
	EDUCATIONS("/educations/"),
	EMPLOYMENTS("/employments/"),
	FUNDINGS("/fundings/"),
	INVITED_POSITIONS("/invited-positions/"),
	MEMBERSHIPS("/memberships/"),
	QUALIFICATIONS("/qualifications/"),
	SERVICES("/services/"),
	PEER_REVIEWS("/peer-reviews/"),
	WORKS("/works/");
	
	private final String pathDiscriminator;
	
	ActivityType(String pathDiscriminator) {
		this.pathDiscriminator = pathDiscriminator;
	}
	
	public String getPathDiscriminator() {
        return pathDiscriminator;
    }
}
