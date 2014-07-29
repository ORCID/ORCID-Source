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

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.NotificationManager;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.notification.Notification;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/notifications")
public class NotificationController extends BaseController {

    @Resource
    private NotificationManager notificationManager;

    @RequestMapping
    public ModelAndView getNotifications() {
        ModelAndView mav = new ModelAndView("notifications");
        OrcidProfile profile = orcidProfileManager.retrieveOrcidProfile(getCurrentUserOrcid(), LoadOptions.BIO_ONLY);
        mav.addObject("profile", profile);
        return mav;
    }

    @RequestMapping("/notifications.json")
    public @ResponseBody
    List<Notification> getNotificationsJson() {
        String currentOrcid = getCurrentUserOrcid();
        return notificationManager.findByOrcid(currentOrcid, 0, 10);
    }

    @RequestMapping(value = "/{id}/notification.html", produces = MediaType.TEXT_HTML_VALUE)
    public @ResponseBody
    String getNotificationHtml(@PathVariable("id") String id) {
        Notification notification = notificationManager.findByOrcidAndId(getCurrentUserOrcid(), Long.valueOf(id));
        return notification.getBodyHtml();
    }

}
