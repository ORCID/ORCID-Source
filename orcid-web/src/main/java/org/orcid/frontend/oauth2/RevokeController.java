package org.orcid.frontend.oauth2;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = { "/oauth/revoke" }, consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.APPLICATION_JSON)
public class RevokeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RevokeController.class);

    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;

    public void setOrcidOauth2TokenDetailService(OrcidOauth2TokenDetailService orcidOauth2TokenDetailService) {
        this.orcidOauth2TokenDetailService = orcidOauth2TokenDetailService;
    }

    @RequestMapping
    public ResponseEntity<?> revoke(HttpServletRequest request) {
        String clientId = SecurityContextHolder.getContext().getAuthentication().getName();        
        if (PojoUtil.isEmpty(clientId)) {
            throw new IllegalArgumentException("Unable to validate client credentials");
        }

        String tokenToRevoke = request.getParameter("token");
        if (PojoUtil.isEmpty(tokenToRevoke)) {
            throw new IllegalArgumentException("Please provide the token to be param");
        }

        OrcidOauth2TokenDetail token = orcidOauth2TokenDetailService.findNonDisabledByTokenValue(tokenToRevoke);
        if (token == null) {
            // Try to find it by refresh token
            token = orcidOauth2TokenDetailService.findByRefreshTokenValue(tokenToRevoke);
        }

        if (token != null && (token.getTokenDisabled() == null || !token.getTokenDisabled())) {
            String tokenOwner = token.getClientDetailsId();
            if (clientId.equals(tokenOwner)) {
                orcidOauth2TokenDetailService.revokeAccessToken(token.getTokenValue());
            } else {
                LOGGER.warn("Client {} is trying to revoke token that belongs to client {}", clientId, tokenOwner);
            }
        }

        return ResponseEntity.ok().build();
    }

}
