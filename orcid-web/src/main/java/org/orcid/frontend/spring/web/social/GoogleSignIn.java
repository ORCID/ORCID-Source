package org.orcid.frontend.spring.web.social;

import org.springframework.social.google.api.Google;

public interface GoogleSignIn extends Google {

    void getJWTInfo();
}
