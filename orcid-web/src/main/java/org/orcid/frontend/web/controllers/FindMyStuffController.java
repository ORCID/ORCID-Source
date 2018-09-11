package org.orcid.frontend.web.controllers;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.orcid.core.manager.v3.FindMyStuffManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.jaxb.model.v3.rc1.notification.Notification;
import org.orcid.jaxb.model.v3.rc1.notification.NotificationType;
import org.orcid.model.v3.rc1.notification.internal.NotificationFindMyStuff;
import org.orcid.persistence.jpa.entities.FindMyStuffHistoryEntity;
import org.orcid.pojo.FindMyStuffResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({ "/find-my-stuff" })
public class FindMyStuffController extends BaseController{

    @Resource 
    NotificationController notificationsController;
    
    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenService;

    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;
    
    @Resource
    FindMyStuffManager findMyStuff;
    
    /** 
     * Generates results if:
     * 
     * 1. user does not have existing permissions with SP
     * 2. user has not opted out of find my stuff
     * 3. we've not attempted to find in the last week for that user 
     * 
     * @return
     */
    @RequestMapping("/find-my-stuff.json")
    public @ResponseBody List<FindMyStuffResult> getFoundStuff() {
        String orcid = getCurrentUserOrcid();
        List<FindMyStuffResult> results = findMyStuff.findIfAppropriate(orcid);
        return results;
    }
    
}
