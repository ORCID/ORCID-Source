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
package org.orcid.frontend.web.controllers;

import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.orcid.core.manager.read_only.PersonDetailsManagerReadOnly;
import org.orcid.core.oauth.openid.OpenIDConnectDiscoveryService;
import org.orcid.core.oauth.openid.OpenIDConnectKeyService;
import org.orcid.core.oauth.openid.OpenIDConnectUserInfo;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_v2.Person;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import net.minidev.json.JSONObject;

@Controller
public class OpenIDController {

    @Resource
    private OpenIDConnectKeyService openIDConnectKeyService;
    
    @Resource
    private PersonDetailsManagerReadOnly personDetailsManagerReadOnly;
    
    @Resource(name="orcidTokenStore")
    private TokenStore tokenStore;
    
    @Resource OpenIDConnectDiscoveryService openIDConnectDiscoveryService;
    
    @Value("${org.orcid.core.baseUri}")
    private String path;
    
    /** Expose the public key as JSON
     * 
     * @param request
     * @return
     */
    @RequestMapping(value = "/oauth/jwks", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody JSONObject getJWKS(HttpServletRequest request) {
        return openIDConnectKeyService.getPublicJWK().toJSONObject();     
    }
    
    /** Manually checks bearer token, looks up user or throws 403.
     * 
     * @return
     */
    @RequestMapping(value = "/oauth/userinfo", method = { RequestMethod.GET, RequestMethod.POST }, produces = "application/json")
    public @ResponseBody ResponseEntity<OpenIDConnectUserInfo> getUserInfo(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization"); //note we do not support form post per https://tools.ietf.org/html/rfc6750 because it's a MAY and pointless
        if (authHeader != null) {
            //lookup token, check it's valid, check scope.
            //deal with incorrect bearer case in request (I'm looking at you spring security!)
            String tokenValue = authHeader.replaceAll("Bearer|bearer", "").trim();
            OAuth2AccessToken tok = tokenStore.readAccessToken(tokenValue);
            if (tok != null && !tok.isExpired()){
                boolean hasScope = false;
                Set<ScopePathType> requestedScopes = ScopePathType.getScopesFromStrings(tok.getScope());
                for (ScopePathType scope : requestedScopes) {
                    if (scope.hasScope(ScopePathType.AUTHENTICATE)) {
                        hasScope = true;
                    }
                }
                if (hasScope){
                    String orcid = tok.getAdditionalInformation().get("orcid").toString();
                    Person person = personDetailsManagerReadOnly.getPublicPersonDetails(orcid);
                    return ResponseEntity.ok(new OpenIDConnectUserInfo(orcid,person,path));
                }
            }            
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    
    /** Expose the openid discovery information
     * 
     * @param request
     * @return
     * @throws JsonProcessingException 
     */
    @RequestMapping(value = "/.well-known/openid-configuration", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody String getOpenIDDiscovery(HttpServletRequest request) throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(openIDConnectDiscoveryService.getConfig());
        return json;     
    }
}
