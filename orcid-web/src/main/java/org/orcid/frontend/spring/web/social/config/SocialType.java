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
