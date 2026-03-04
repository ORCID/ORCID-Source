package org.orcid.frontend.web.controllers;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.orcid.core.oauth.authorizationServer.AuthorizationServerUtil;
import org.orcid.core.oauth.openid.OpenIDConnectUserInfo;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;

import net.minidev.json.JSONObject;

@Controller
public class OpenIDController {
    
    @Resource(name = "personDetailsManagerReadOnlyV3")
    private PersonDetailsManagerReadOnly personDetailsManagerReadOnly;
    
    @Resource
    private AuthorizationServerUtil authorizationServerUtil;

    @Value("${org.orcid.core.baseUri}")
    private String path;
    
    //match access token in POST body
    Pattern p = Pattern.compile("(?<=access_token=).*?(?=&|$)");
    
    /** Expose the public key as JSON
     * 
     * @param request
     * @return
     */
    @RequestMapping(value = "/oauth/jwks", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody JSONObject getJWKS(HttpServletRequest request) {
        //TODO: This should be a FW proxy to the auth server, maybe from nginx
        throw new UnsupportedOperationException("Should be requested from the auth server");
    }
    
    /** Manually checks bearer token in header, looks up user or throws 403.
     * 
     * @return
     * @throws IOException 
     */
    @RequestMapping(value = "/oauth/userinfo", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody ResponseEntity<OpenIDConnectUserInfo> getUserInfo(HttpServletRequest request) throws IOException{
        if (request.getHeader("Authorization") != null) {//look in header
            String tokenValue = request.getHeader("Authorization").replaceAll("Bearer|bearer", "").trim();
            OpenIDConnectUserInfo info = getInfoFromToken(tokenValue);
            if (info != null)
                return ResponseEntity.ok(info);
        }            
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new OpenIDConnectUserInfoAccessDenied());
    }
    
    @RequestMapping(value = "/oauth/userinfo", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody ResponseEntity<OpenIDConnectUserInfo> getUserInfoPOST(HttpServletRequest request) throws IOException{
        if (request.getParameter("access_token") != null) {
            OpenIDConnectUserInfo info = getInfoFromToken(request.getParameter("access_token"));
            if (info != null)
                return ResponseEntity.ok(info);                
        }
        return getUserInfo(request);
    }
    
    //lookup token, check it's valid, check scope.
    //deal with incorrect bearer case in request (I'm looking at you spring security!)
    private OpenIDConnectUserInfo getInfoFromToken(String tokenValue) {
        //TODO: Refactor this to get the information from the database
        // This is unexpected and should be done with token introspection form the oauth server
        JSONObject tokenInfo = authorizationServerUtil.tokenIntrospection(tokenValue);
        if(tokenInfo == null) {
            return null;
        }

        boolean isTokenActive = tokenInfo.getBoolean("active");

        if(isTokenActive) {
            // If the token is user revoked it might be used for DELETE requests
            Set<String> scopes = OAuth2Utils.parseParameterList(tokenInfo.getString("scope"));
            OpenIDConnectUserInfo info = scopes.stream().forEach(s ->
            {
                ScopePathType scope = ScopePathType.fromValue(s);
                if(scope.hasScope(ScopePathType.AUTHENTICATE)) {
                    OpenIDConnectUserInfo t = new OpenIDConnectUserInfo();
                    //TODO set data to the token
                    //TODO the name doesn't come in the token introspection data, so, we should get it from the names cache
                    return t;
                }
            });
            return info;
        }
        return null;
    }

    
    /** Expose the openid discovery information
     * 
     * @param request
     * @return
     * @throws JsonProcessingException 
     */
    @RequestMapping(value = "/.well-known/openid-configuration", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody String getOpenIDDiscovery(HttpServletRequest request) throws JsonProcessingException {
        //TODO: This should be a FW proxy to the auth server, maybe from nginx
        throw new UnsupportedOperationException("Should be requested from the auth server");
    }

}
