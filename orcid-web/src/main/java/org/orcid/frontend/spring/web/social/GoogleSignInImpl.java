package org.orcid.frontend.spring.web.social;

import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.google.api.oauth2.OAuth2Operations;
import org.springframework.social.google.api.oauth2.UserInfo;

public class GoogleSignInImpl extends GoogleTemplate implements GoogleSignIn {

    public GoogleSignInImpl() {
        super();
    }

    public GoogleSignInImpl(String accessToken) {
        super(accessToken);
    }

    @Override
    public UserInfo getUserInfo() {
        OAuth2Operations op = oauth2Operations();
        return op.getUserinfo();
    }

}
