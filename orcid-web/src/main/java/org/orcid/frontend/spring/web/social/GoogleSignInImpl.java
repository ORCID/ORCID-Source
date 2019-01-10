package org.orcid.frontend.spring.web.social;

import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.web.client.RestTemplate;

public class GoogleSignInImpl extends GoogleTemplate implements GoogleSignIn {

    public GoogleSignInImpl() {
        super();
    }
    
    public GoogleSignInImpl(String accessToken) {
        super(accessToken);
    }
    
    public void getJWTInfo() {
        RestTemplate restTemplate = getRestTemplate();
        Object o = restTemplate.getForObject("https://www.googleapis.com/oauth2/v4/token", Object.class);
        System.out.println(o);
    }

}
