/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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

import java.util.LinkedHashMap;
import java.util.Map;

import org.orcid.frontend.web.forms.LoginForm;
import org.orcid.frontend.web.forms.RegistrationForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller("loginController")
public class LoginController extends BaseController {

    @ModelAttribute("yesNo")
    public Map<String, String> retrieveYesNoMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("true", "Yes");
        map.put("false", "No");
        return map;
    }

    @RequestMapping(value = { "/signin", "/login" }, method = RequestMethod.GET)
    public ModelAndView loginGetHandler(ModelAndView mav) {
        // in case have come via a link that requires them to be signed out
        logoutCurrentUser();
        mav.setViewName("login");
        return mav;
    }

    @RequestMapping(value = { "/oauth/signin", "/oauth/login" }, method = RequestMethod.GET)
    public ModelAndView loginGetHandler2(ModelAndView mav) {
        mav.setViewName("oauth_login");
        mav.addObject("hideUserVoiceScript", true);
        return mav;
    }

    @RequestMapping("wrong-user")
    public String wrongUserHandler() {
        return "wrong_user";
    }

    @RequestMapping("/session-expired")
    public String sessionExpiredHandler() {
        return "session_expired";
    }

}
