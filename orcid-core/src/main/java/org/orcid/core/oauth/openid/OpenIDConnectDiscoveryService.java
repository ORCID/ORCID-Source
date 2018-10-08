package org.orcid.core.oauth.openid;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import com.google.common.collect.Lists;

public class OpenIDConnectDiscoveryService {

    private OpenIDConnectDiscoveryServiceConfig config;
    
    public static class OpenIDConnectDiscoveryServiceConfig{
        @Value("${org.orcid.core.baseUri}")
        private String path;
        
        private List<String> token_endpoint_auth_signing_alg_values_supported = Lists.newArrayList("RS256");
        private List<String> id_token_signing_alg_values_supported = Lists.newArrayList("RS256");
        private String userinfo_endpoint = "/oauth/userinfo";
        private String authorization_endpoint = "/oauth/authorize";
        private String token_endpoint = "/oauth/token";
        private String jwks_uri = "/oauth/jwks";
        private List<String> claims_supported = Lists.newArrayList("family_name","given_name","name","auth_time","iss","sub");
        private List<String> scopes_supported = Lists.newArrayList("openid");
        private List<String> subject_types_supported  = Lists.newArrayList("public");
        private List<String> response_types_supported = Lists.newArrayList("code","id_token","id_token token");
        private Boolean claims_parameter_supported = false;
        private List<String> token_endpoint_auth_methods_supported = Lists.newArrayList("client_secret_basic");
        private List<String> grant_types_supported = Lists.newArrayList("authorization_code","implicit","refresh_token");
        public String getIssuer() {
            return path;
        }
        public List<String> getToken_endpoint_auth_signing_alg_values_supported() {
            return token_endpoint_auth_signing_alg_values_supported;
        }
        public List<String> getId_token_signing_alg_values_supported() {
            return id_token_signing_alg_values_supported;
        }
        public String getUserinfo_endpoint() {
            return path+userinfo_endpoint;
        }
        public String getAuthorization_endpoint() {
            return path+authorization_endpoint;
        }
        public String getToken_endpoint() {
            return path+token_endpoint;
        }
        public String getJwks_uri() {
            return path+jwks_uri;
        }
        public List<String> getClaims_supported() {
            return claims_supported;
        }
        public List<String> getScopes_supported() {
            return scopes_supported;
        }
        public List<String> getSubject_types_supported() {
            return subject_types_supported;
        }
        public List<String> getResponse_types_supported() {
            return response_types_supported;
        }
        public Boolean getClaims_parameter_supported() {
            return claims_parameter_supported;
        }
        public List<String> getToken_endpoint_auth_methods_supported() {
            return token_endpoint_auth_methods_supported;
        }
        public List<String> getGrant_types_supported() {
            return grant_types_supported;
        }
        
        /* example: 
                 * "provider_info":{
        "token_endpoint_auth_signing_alg_values_supported":[
        "RS256"
        ],
        "userinfo_endpoint":"https://qa.orcid.org/oauth/userinfo",
        "authorization_endpoint":"https://qa.orcid.org/oauth/authorize",
        "claims_supported":[
        "family_name",
        "given_name",
        "name",
        "auth_time",
        "iss",
        "sub"
        ],
        "scopes_supported":[
        "openid"
        ],
        "grant_types_supported":[
        "authorization_code"
        ],
        "token_endpoint":"https://qa.orcid.org/oauth/token",
        "id_token_signing_alg_values_supported":[
        "RS256"
        ],
        "subject_types_supported":[
        "public"
        ],
        "response_types_supported":[
        "code"
        ],
        "jwks_uri":"https://qa.orcid.org/oauth/jwks",
        "claims_parameter_supported":"false",
        "token_endpoint_auth_methods_supported":[
        "client_secret_basic"
        ],
        "claim_types_supported":[
        "normal"
        ],
        "issuer":"https://orcid.org"
        }

         */
    }
    
    public OpenIDConnectDiscoveryService(OpenIDConnectDiscoveryServiceConfig config){
        this.config = config;
    }
    
    public OpenIDConnectDiscoveryServiceConfig getConfig(){        
        return config;
    }
}
