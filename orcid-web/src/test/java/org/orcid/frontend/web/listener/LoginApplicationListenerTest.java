/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.frontend.web.listener;

import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * Test class existing only to assert that a successful login always increments
 * a metric counter.
 * 
 * @author jamesb
 * 
 */
public class LoginApplicationListenerTest {

    @Test
    public void testOnApplicationEvent() {
        Long counter = LoginApplicationListener.LOGIN_COUNTER.count();
        LoginApplicationListener loginApplicationListener = new LoginApplicationListener();
        AuthenticationSuccessEvent authenticationSuccessEvent = new AuthenticationSuccessEvent(fakeAuth());
        // call synchronously - not the normal usage of an event model, but this
        // is only to check the counter
        loginApplicationListener.onApplicationEvent(authenticationSuccessEvent);
        Long incCounter = counter + 1;
        assertTrue(LoginApplicationListener.LOGIN_COUNTER.count() == incCounter);

        incCounter++;
        loginApplicationListener.onApplicationEvent(authenticationSuccessEvent);
        assertTrue(LoginApplicationListener.LOGIN_COUNTER.count() == incCounter);
    }

    private Authentication fakeAuth() {
        return new Authentication() {

            /**
             * 
             */
            private static final long serialVersionUID = -5445977611474157507L;

            @Override
            public String getName() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean isAuthenticated() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public Object getPrincipal() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Object getDetails() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Object getCredentials() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }

}
