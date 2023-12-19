package org.orcid.core.oauth.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.endpoint.DefaultRedirectResolver;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class OrcidOauthRedirectResolver extends DefaultRedirectResolver {

    private Collection<String> redirectGrantTypes = Arrays.asList("implicit", "authorization_code");

    public OrcidOauthRedirectResolver() {
        
    }
    
    @Override
    public void setRedirectGrantTypes(Collection<String> redirectGrantTypes) {
        this.redirectGrantTypes = new HashSet<String>(redirectGrantTypes);
    }

    @Override
    public String resolveRedirect(String requestedRedirect, ClientDetails client) throws OAuth2Exception {
        Set<String> authorizedGrantTypes = client.getAuthorizedGrantTypes();
        if (authorizedGrantTypes.isEmpty()) {
            throw new InvalidGrantException("A client must have at least one authorized grant type.");
        }
        if (!containsRedirectGrantType(authorizedGrantTypes)) {
            throw new InvalidGrantException("A redirect_uri can only be used by implicit or authorization_code grant types.");
        }

        Set<String> registeredRedirectUris = client.getRegisteredRedirectUri();
        if (registeredRedirectUris == null || registeredRedirectUris.isEmpty()) {
            throw new InvalidRequestException("At least one redirect_uri must be registered with the client.");
        }
        
        // There must be at least one redirect uri that is the root of the requested redirect uri
        for(String registeredRedirectUri : registeredRedirectUris) {
            if(requestedRedirect != null && redirectMatches(requestedRedirect, registeredRedirectUri.trim())) {
                return requestedRedirect;
            }
        }
        throw new RedirectMismatchException("Unable to find a matching redirect_uri for the client.");
    }

    /**
     * @param grantTypes
     *            some grant types
     * @return true if the supplied grant types includes one or more of the
     *         redirect types
     */
    private boolean containsRedirectGrantType(Set<String> grantTypes) {
        for (String type : grantTypes) {
            if (redirectGrantTypes.contains(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean redirectMatches(String requestedRedirect, String redirectUri) {
        UriComponents requestedRedirectUri = UriComponentsBuilder.fromUriString(requestedRedirect).build();
        UriComponents registeredRedirectUri = UriComponentsBuilder.fromUriString(redirectUri).build();

        String requestedRedirectUriPath = (requestedRedirectUri.getPath() != null ? requestedRedirectUri.getPath() : "");
        String registeredRedirectUriPath = (registeredRedirectUri.getPath() != null ? registeredRedirectUri.getPath() : "");

        boolean portMatch = registeredRedirectUri.getPort() == requestedRedirectUri.getPort();
        boolean hostMatch = hostMatches(registeredRedirectUri.getHost(), requestedRedirectUri.getHost());
        boolean schemeMatch = isEqual(registeredRedirectUri.getScheme(), requestedRedirectUri.getScheme());
        boolean userInfoMatch = isEqual(registeredRedirectUri.getUserInfo(), requestedRedirectUri.getUserInfo());
        boolean pathMatch = StringUtils.cleanPath(requestedRedirectUriPath).startsWith(StringUtils.cleanPath(registeredRedirectUriPath));

        return schemeMatch && userInfoMatch && hostMatch && portMatch && pathMatch;
    }

    private boolean isEqual(String str1, String str2) {
        return Objects.equals(str1, str2);
    }
    
    @Override
    protected boolean hostMatches(String registered, String requested) {
        return isEqual(registered, requested);
    }
}
