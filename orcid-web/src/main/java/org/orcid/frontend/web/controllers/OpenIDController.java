package org.orcid.frontend.web.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.codehaus.jettison.json.JSONException;
import org.orcid.core.manager.v3.read_only.PersonDetailsManagerReadOnly;
import org.orcid.core.oauth.authorizationServer.AuthorizationServerUtil;
import org.orcid.core.oauth.openid.OpenIDConnectUserInfo;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.record.Person;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.codehaus.jettison.json.JSONObject;

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
    public @ResponseBody ResponseEntity<OpenIDConnectUserInfo> getUserInfo(HttpServletRequest request) throws IOException, JSONException, URISyntaxException, InterruptedException {
        if (request.getHeader("Authorization") != null) {//look in header
            String tokenValue = request.getHeader("Authorization").replaceAll("Bearer|bearer", "").trim();
            OpenIDConnectUserInfo info = getInfoFromToken(tokenValue);
            if (info != null)
                return ResponseEntity.ok(info);
        }            
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new OpenIDConnectUserInfoAccessDenied());
    }
    
    @RequestMapping(value = "/oauth/userinfo", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody ResponseEntity<OpenIDConnectUserInfo> getUserInfoPOST(HttpServletRequest request) throws IOException, JSONException, URISyntaxException, InterruptedException {
        if (request.getParameter("access_token") != null) {
            OpenIDConnectUserInfo info = getInfoFromToken(request.getParameter("access_token"));
            if (info != null)
                return ResponseEntity.ok(info);                
        }
        return getUserInfo(request);
    }
    
    //lookup token, check it's valid, check scope.
    //deal with incorrect bearer case in request (I'm looking at you spring security!)
    private OpenIDConnectUserInfo getInfoFromToken(String tokenValue) throws JSONException, IOException, URISyntaxException, InterruptedException {
        //TODO: Refactor this to get the information from the database
        // This is unexpected and should be done with token introspection form the oauth server
        JSONObject tokenInfo = authorizationServerUtil.tokenIntrospection(tokenValue);
        if(tokenInfo == null) {
            return null;
        }

        boolean isTokenActive = tokenInfo.getBoolean("active");

        if(isTokenActive) {
            // If the token is user revoked it might be used for DELETE requests
            Set<String> scopes = Arrays.stream(tokenInfo.getString("scope").split("[\\s,]+"))
                    .collect(Collectors.toSet());
            OpenIDConnectUserInfo info = null;
            for(String scope : scopes) {
                ScopePathType scopePathType = ScopePathType.fromValue(scope);
                if(scopePathType.hasScope(ScopePathType.AUTHENTICATE)) {
                    String orcid = tokenInfo.getString("username");
                    Person person = personDetailsManagerReadOnly.getPublicPersonDetails(orcid);
                    return new OpenIDConnectUserInfo(orcid,person,path);
                }
            }
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OpenIDConnectUserInfoAccessDenied extends OpenIDConnectUserInfo{
        @JsonProperty("error")
        String error = "access_denied";
        @JsonProperty("error-description")
        String errorDescription="access_token is invalid";
        OpenIDConnectUserInfoAccessDenied(){

        }
    }
}
