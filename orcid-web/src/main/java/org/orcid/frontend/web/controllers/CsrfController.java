package org.orcid.frontend.web.controllers;

import java.util.Collections;
import java.util.Map;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Lightweight CSRF initialization endpoint.
 *
 * This endpoint is intended to be called by the frontend only on Firefox, as
 * Firefox may ignore Set-Cookie for the XSRF token when it is sent on 3xx
 * responses (e.g., during calls like /account/nameForm.json or
 * /inbox/unreadCount.json). Hitting this endpoint first ensures a 200 OK
 * response path where the CookieCsrfTokenRepository can write the XSRF-TOKEN
 * cookie.
 */
@Controller
public class CsrfController {

    @Resource(name = "csrfTokenRepo")
    private CsrfTokenRepository csrfTokenRepository;

    @RequestMapping(value = "/csrf.json", method = RequestMethod.GET)
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody Map<String, String> getCsrf(HttpServletRequest request, HttpServletResponse response) {
        // Reuse existing token when available; only generate/save when missing.
        CsrfToken csrfToken = csrfTokenRepository.loadToken(request);
        if (csrfToken == null) {
            csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        }
        if (csrfToken == null) {
            csrfToken = (CsrfToken) request.getAttribute("_csrf");
        }
        if (csrfToken == null) {
            csrfToken = csrfTokenRepository.generateToken(request);
            csrfTokenRepository.saveToken(csrfToken, request, response);
        }
        if (csrfToken != null) {
            csrfToken.getToken();
        }
        return Collections.emptyMap();
    }
}


