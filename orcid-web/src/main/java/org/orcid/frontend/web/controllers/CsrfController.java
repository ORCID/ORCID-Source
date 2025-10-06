package org.orcid.frontend.web.controllers;

import java.util.Collections;
import java.util.Map;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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

    @RequestMapping(value = "/csrf.json", method = RequestMethod.GET)
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody Map<String, String> getCsrf() {
        // No work needed here. The Spring Security CSRF filter ensures the
        // token is generated and the cookie is written on the response.
        return Collections.emptyMap();
    }
}


