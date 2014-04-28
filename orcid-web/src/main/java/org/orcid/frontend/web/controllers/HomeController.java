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

import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.orcid.core.locale.LocaleManager;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.pojo.UserStatus;
import org.orcid.utils.OrcidStringUtils;
import org.orcid.utils.UTF8Control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
public class HomeController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @Resource
    private LocaleManager localeManager;

// @formatter:off
//    @RequestMapping(value = "/")
//    public ModelAndView homeHandler(HttpServletRequest request) {
//        StringBuilder newUri = new StringBuilder(request.getRequestURL());
//        newUri.insert(newUri.indexOf("://") + 3, "about.");
//        newUri.insert(0, "redirect:");
//        return new ModelAndView(newUri.toString());
//    }
// @formatter:on

    // freindly link to allow language switching
    @RequestMapping(value = "/home")
    public ModelAndView homeRedirect(HttpServletRequest request) {
        return new ModelAndView("redirect:/");

    }

    @RequestMapping(value = "/tomcatUp.json")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody
    String tomcatUp(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
        request.setAttribute("isMonitoring", true);
        return "{tomcatUp:true}";
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
    org.orcid.pojo.Local langJson(HttpServletRequest request, @RequestParam(value = "lang", required = false) String lang) throws NoSuchRequestHandlingMethodException {
        if (lang != null) {
            String orcid = getRealUserOrcid();
            if (orcid != null) {
                OrcidProfile existingProfile = orcidProfileManager.retrieveOrcidProfile(orcid);
                org.orcid.jaxb.model.message.Locale locale = existingProfile.getOrcidPreferences().getLocale();
                if (!locale.value().equals(lang)) {
                    try {
                        existingProfile.getOrcidPreferences().setLocale(org.orcid.jaxb.model.message.Locale.fromValue(lang));
                        orcidProfileManager.updateOrcidPreferences(existingProfile);
                    } catch (Exception e) {
                        LOGGER.error("langJson exception", e);
                    } catch (Throwable t) {
                        LOGGER.error("langJson Throwable", t);
                    }
                }
            }
        }

        Locale locale = RequestContextUtils.getLocale(request);
        return localeManager.getJavascriptMessages(locale);

    }

    @RequestMapping(value = "/userStatus.json")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody
    Object getUserStatusJson(HttpServletRequest request, @RequestParam(value = "logUserOut", required = false) Boolean logUserOut)
            throws NoSuchRequestHandlingMethodException {

        if (logUserOut != null && logUserOut.booleanValue()) {
            SecurityContextHolder.clearContext();
            request.getSession().invalidate();
        }

        String orcid = getCurrentUserOrcid();
        UserStatus us = new UserStatus();
        us.setLoggedIn((orcid != null));
        return us;
    }

}
