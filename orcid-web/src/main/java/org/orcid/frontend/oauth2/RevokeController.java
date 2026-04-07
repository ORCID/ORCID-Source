package org.orcid.frontend.oauth2;

import javax.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.orcid.api.common.T2OrcidApiService;
import org.orcid.core.oauth.authorizationServer.AuthorizationServerUtil;
import org.orcid.core.togglz.Features;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.net.URISyntaxException;

@Controller
@RequestMapping(value = { T2OrcidApiService.OAUTH_REVOKE }, consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.APPLICATION_JSON)
public class RevokeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RevokeController.class);

    @Resource
    private AuthorizationServerUtil authorizationServerUtil;

    @RequestMapping
    public ResponseEntity<?> revoke(HttpServletRequest request) throws IOException, URISyntaxException, InterruptedException {
        // Forward the request to the authorization server
        String tokenToRevoke = request.getParameter("token");
        if (PojoUtil.isEmpty(tokenToRevoke)) {
            throw new IllegalArgumentException("Please provide the token to be param");
        }
        Response r = null;
        if(StringUtils.isNotBlank(request.getHeader("Authorization"))) {
            String authorization = request.getHeader("Authorization");
            r = authorizationServerUtil.forwardTokenRevocationRequest(authorization, tokenToRevoke);
        } else {
            String clientId = SecurityContextHolder.getContext().getAuthentication().getName();
            String clientSecret = request.getParameter("client_secret");
            if (PojoUtil.isEmpty(tokenToRevoke)) {
                throw new IllegalArgumentException("Please provide the token to be param");
            }
            r = authorizationServerUtil.forwardTokenRevocationRequest(clientId, clientSecret, tokenToRevoke);
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(Features.OAUTH_TOKEN_VALIDATION.name(),
                "ON");
        return ResponseEntity.status(r.getStatus()).headers(responseHeaders).body(r.getEntity());
    }

}
