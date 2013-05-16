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

import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.pojo.UserStatus;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
public class HomeController extends BaseController {

    @RequestMapping(value = "/")
    public ModelAndView homeHandler(HttpServletRequest request) {
        StringBuilder newUri = new StringBuilder(request.getRequestURL());
        newUri.insert(newUri.indexOf("://") + 3, "about.");
        newUri.insert(0, "redirect:");
        return new ModelAndView(newUri.toString());
    }

    @RequestMapping(value = "/home")
    public ModelAndView appHomeHandler() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("home");
        return mav;
    }

    @RequestMapping(value = "/robots.txt")
    public String dynamicRobots(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
        String requestedDomain = request.getServerName();
        if (domainsAllowingRobots.contains(requestedDomain)) {
            throw new NoSuchRequestHandlingMethodException(request);
        }
        return "robots";
    }

    @RequestMapping(value = "/lang.json")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody
    org.orcid.pojo.Local langJson(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {

        Locale locale = RequestContextUtils.getLocale(request);
        org.orcid.pojo.Local lPojo = new org.orcid.pojo.Local();
        lPojo.setLocale(locale.toString());

        ResourceBundle resources = ResourceBundle.getBundle("i18n/messages", locale);
        lPojo.setMessages(OrcidStringUtils.resourceBundleToMap(resources));

        return lPojo;

    }

    @RequestMapping(value = "/userStatus.json")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody
    Object getUserStatusJson(HttpServletRequest request, @RequestParam(value = "logUserOut", required = false) Boolean logUserOut) throws NoSuchRequestHandlingMethodException {
        
        if (logUserOut != null && logUserOut.booleanValue()) {
            SecurityContextHolder.clearContext();
            request.getSession().invalidate();
        }
        
        OrcidProfileUserDetails opd = getCurrentUser();
        UserStatus us = new UserStatus();
        us.setLoggedIn((opd != null));
        return us;
    }

}
