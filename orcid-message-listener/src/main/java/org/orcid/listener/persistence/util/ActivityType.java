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

import org.orcid.jaxb.model.record_v2.Activity;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.Work;

public enum ActivityType {
	EDUCATIONS("/educations/", "educations"),
	EMPLOYMENTS("/employments/", "employments"),
	FUNDINGS("/fundings/", "fundings"),
	PEER_REVIEWS("/peer-reviews/", "peer-reviews"),
	WORKS("/works/", "works");
	
	private final String pathDiscriminator;
	private final String name;
	
	ActivityType(String pathDiscriminator, String name) {
		this.pathDiscriminator = pathDiscriminator;
		this.name = name;
	}
	
	public String getPathDiscriminator() {
        return pathDiscriminator;
    }
	
	public String getName() {
		return name;
	}
	
	public static ActivityType inferFromActivity(Activity a) {
		if(a.getClass().isAssignableFrom(Education.class)) {
			return EDUCATIONS;
		} else if(a.getClass().isAssignableFrom(Employment.class)) {
			return EMPLOYMENTS;
		} else if(a.getClass().isAssignableFrom(Funding.class)) {
			return FUNDINGS;
		} else if(a.getClass().isAssignableFrom(PeerReview.class)) {
			return PEER_REVIEWS;
		} else if(a.getClass().isAssignableFrom(Work.class)) {
			return WORKS;
		} 
		
		throw new IllegalArgumentException("Unable to find activity of type " + a.getClass().toGenericString());
	}
}
