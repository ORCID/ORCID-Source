package org.orcid.frontend.oauth2;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.orcid.api.common.T2OrcidApiService;
import org.orcid.core.oauth.authorizationServer.AuthorizationServerUtil;
import org.orcid.core.togglz.Features;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.net.URISyntaxException;

@Controller
@RequestMapping(value = { T2OrcidApiService.OAUTH_REVOKE }, consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.APPLICATION_JSON)
public class RevokeController {

    @Resource
    private AuthorizationServerUtil authorizationServerUtil;

    @RequestMapping
    public ResponseEntity<?> revoke(HttpServletRequest request) throws IOException, URISyntaxException, InterruptedException {
        String tokenToRevoke = request.getParameter("token");
        String authorization = request.getHeader("Authorization");
        Response r = null;

        if (PojoUtil.isEmpty(tokenToRevoke)) {
            throw new IllegalArgumentException("Please provide the token to be param");
        }
        
        if(Features.OAUTH_TOKEN_VALIDATION.isActive()) {
            // Forward the request to the authorization server
            if(StringUtils.isNotBlank(authorization)) {
                r = authorizationServerUtil.forwardTokenRevocationRequest(authorization, tokenToRevoke);
            } else {
                String clientId = resolveClientId(request);
                String clientSecret = request.getParameter("client_secret");
                r = authorizationServerUtil.forwardTokenRevocationRequest(clientId, clientSecret, tokenToRevoke);
            }
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(Features.OAUTH_TOKEN_VALIDATION.name(),
                    "ON");
            return ResponseEntity.status(r.getStatus()).headers(responseHeaders).body(r.getEntity());
        } else {
            if (StringUtils.isNotBlank(authorization)) {
                r = authorizationServerUtil.forwardTokenRevocationRequest(authorization, tokenToRevoke);
            } else {
                String clientId = resolveClientId(request);
                String clientSecret = request.getParameter("client_secret");
                r = authorizationServerUtil.forwardTokenRevocationRequest(clientId, clientSecret, tokenToRevoke);
            }
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(Features.OAUTH_TOKEN_VALIDATION.name(),
                "ON");
        return ResponseEntity.status(r.getStatus()).headers(responseHeaders).body(r.getEntity());
    }

    private String resolveClientId(HttpServletRequest request) {
        String clientId = request.getParameter("client_id");
        if (StringUtils.isNotBlank(clientId)) {
            return clientId;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && StringUtils.isNotBlank(authentication.getName())) {
            return authentication.getName();
        }

        throw new IllegalArgumentException("Please provide client_id or Authorization header");
    }

}
