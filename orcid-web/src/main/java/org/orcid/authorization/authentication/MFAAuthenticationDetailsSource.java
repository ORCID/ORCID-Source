package org.orcid.authorization.authentication;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class MFAAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, MFAWebAuthenticationDetails> {

    @Override
    public MFAWebAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new MFAWebAuthenticationDetails(context);
    }

}
