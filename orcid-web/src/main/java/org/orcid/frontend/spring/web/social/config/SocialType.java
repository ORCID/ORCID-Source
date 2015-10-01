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
package org.orcid.frontend.spring.web.social.config;

/**
 * @author Shobhit Tyagi
 */
public enum SocialType {

	FACEBOOK("facebook"), GOOGLE("google");
	
	private String value;

	private SocialType(String value) {
		this.value = value;
	}
	
	public String value() {
        return value;
    }
}
