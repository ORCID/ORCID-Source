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

import org.orcid.core.manager.NotificationManager;
import org.orcid.jaxb.model.notification.Notification;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/notifications")
public class NotificationController extends BaseController {

    @Resource
    private NotificationManager notificationManager;

    @RequestMapping
    public String getNotifications() {
        return "notifications";
    }

    @RequestMapping("/notifications.json")
    public @ResponseBody
    List<Notification> getNotificationsJson() {
        String currentOrcid = getCurrentUserOrcid();
        return notificationManager.findByOrcid(currentOrcid, 0, 10);
    }

}
