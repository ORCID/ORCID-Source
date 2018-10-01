package org.orcid.frontend.web.controllers;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.orcid.core.manager.v3.read_only.PersonDetailsManagerReadOnly;
import org.orcid.core.oauth.openid.OpenIDConnectDiscoveryService;
import org.orcid.core.oauth.openid.OpenIDConnectKeyService;
import org.orcid.core.oauth.openid.OpenIDConnectUserInfo;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.rc1.record.Person;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import net.minidev.json.JSONObject;

@Controller
public class OpenIDController {

    @Resource
    private OpenIDConnectKeyService openIDConnectKeyService;
    
    @Resource(name = "personDetailsManagerReadOnlyV3")
    private PersonDetailsManagerReadOnly personDetailsManagerReadOnly;
    
    @Resource(name="orcidTokenStore")
    private TokenStore tokenStore;
    
    @Resource OpenIDConnectDiscoveryService openIDConnectDiscoveryService;
    
    @Value("${org.orcid.core.baseUri}")
    private String path;
    
    //match access token in POST body
    Pattern p = Pattern.compile("(?<=access_token=).*?(?=&|$)");
    
    /** Expose the public key as JSON
     * 
     * @param request
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/oauth/jwks", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody JSONObject getJWKS(HttpServletRequest request) {
        return openIDConnectKeyService.getPublicJWK().toJSONObject();     
    }
    
    /** Manually checks bearer token in header, looks up user or throws 403.
     * 
     * @return
     * @throws IOException 
     */
    @CrossOrigin
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
                return new OpenIDConnectUserInfo(orcid,person,path);
            }
        }  
        return null;
    }

    
    /** Expose the openid discovery information
     * 
     * @param request
     * @return
     * @throws JsonProcessingException 
     */
    @CrossOrigin
    @RequestMapping(value = "/.well-known/openid-configuration", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody String getOpenIDDiscovery(HttpServletRequest request) throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(openIDConnectDiscoveryService.getConfig());
        return json;     
    }
    

    @JsonInclude(Include.NON_NULL) 
    public static class OpenIDConnectUserInfoAccessDenied extends OpenIDConnectUserInfo{
        String error = "access_denied";
        @JsonProperty("error-description")
        String errorDescription="access_token is invalid";
        OpenIDConnectUserInfoAccessDenied(){
            
        }
    }
}
