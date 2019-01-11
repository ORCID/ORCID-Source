package org.orcid.frontend.spring.web.social;

import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.oauth2.UserInfo;

public interface GoogleSignIn extends Google {

    UserInfo getUserInfo();
}
