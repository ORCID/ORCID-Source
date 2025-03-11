package org.orcid.frontend.web.controllers;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.ehcache.UserManagedCache;
import org.ehcache.config.builders.UserManagedCacheBuilder;
import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.PreferenceManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceClientId;
import org.orcid.jaxb.model.v3.release.notification.Notification;
import org.orcid.jaxb.model.v3.release.notification.NotificationType;
import org.orcid.jaxb.model.v3.release.notification.amended.NotificationAmended;
import org.orcid.jaxb.model.v3.release.notification.custom.NotificationCustom;
import org.orcid.jaxb.model.v3.release.notification.permission.NotificationPermission;
import org.orcid.model.v3.release.notification.institutional_sign_in.NotificationInstitutionalConnection;
import org.orcid.persistence.constants.SendEmailFrequency;
import org.orcid.persistence.jpa.entities.ActionableNotificationEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping({ "/inbox", "/notifications" })
@SessionAttributes("primaryEmail")
public class NotificationController extends BaseController {

    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;

    @Resource
    private EncryptionManager encryptionManager;
    
    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;
    
    @Resource
    private PreferenceManager preferenceManager;
    
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Resource(name = "emailManagerReadOnlyV3")
    private EmailManagerReadOnly emailManagerReadOnly;
    
    @Resource
    private EmailFrequencyManager emailFrequencyManager;
    
    @RequestMapping
    public ModelAndView getNotifications() {
        return new ModelAndView("notifications");
    }

    UserManagedCache<String, Boolean> isObsoleteNotificationAlertsCheckDone =
            UserManagedCacheBuilder.newUserManagedCacheBuilder(String.class, Boolean.class).build(true);

    public void shutdown() {
        isObsoleteNotificationAlertsCheckDone.close();
    }

    @RequestMapping("/notifications.json")
    public @ResponseBody List<Notification> getNotificationsJson(@RequestParam(value = "firstResult", defaultValue = "0") int firstResult,
            @RequestParam(value = "maxResults", defaultValue = "10") int maxResults,
            @RequestParam(value = "includeArchived", defaultValue = "false") boolean includeArchived) {
        String currentOrcid = getCurrentUserOrcid();
        List<Notification> notifications = notificationManager.findByOrcid(currentOrcid, includeArchived, firstResult, maxResults);
        notifications = archiveObsoleteNotifications(currentOrcid, notifications);
        addSubjectToNotifications(notifications);
        setOverwrittenSourceName(notifications);
        addSourceDescription(notifications);
        return notifications;
    }

    private List<Notification> archiveObsoleteNotifications(String currentOrcid, List<Notification> notifications) {
        if (!isObsoleteNotificationAlertsCheckDone.containsKey(currentOrcid)) {
            notifications = notificationManager.filterActionedNotificationAlerts(notifications, currentOrcid);
            isObsoleteNotificationAlertsCheckDone.putIfAbsent(currentOrcid, Boolean.TRUE);
        }
        return notifications;
    }

    private void setOverwrittenSourceName(List<Notification> notifications) {
    	for(Notification notification : notifications) {
    		if(notification instanceof NotificationCustom) {
    			NotificationCustom nc = (NotificationCustom) notification;
    			if(getMessage("email.subject.auto_deprecate").equals(nc.getSubject())) {
    				nc.setOverwrittenSourceName("ORCID");    				
    			}
    		}
    	}
    }
    
    private void addSubjectToNotifications(List<Notification> notifications) {
        for (Notification notification : notifications) {
            if (notification instanceof NotificationPermission) {
                NotificationPermission naa = (NotificationPermission) notification;
                String customSubject = naa.getNotificationSubject();
                if (StringUtils.isNotBlank(customSubject)) {
                    naa.setSubject(customSubject);
                } else {
                    naa.setSubject(getMessage(buildInternationalizationKey(NotificationType.class, naa.getNotificationType().value())));
                }
            } else if (notification instanceof NotificationAmended) {
                NotificationAmended na = (NotificationAmended) notification;
                na.setSubject(getMessage(buildInternationalizationKey(NotificationType.class, na.getNotificationType().value())));
            } else if (notification instanceof NotificationInstitutionalConnection) {
                NotificationInstitutionalConnection nic = (NotificationInstitutionalConnection) notification;
                nic.setSubject(getMessage(buildInternationalizationKey(NotificationType.class, nic.getNotificationType().value())));
            }
        }
    }
    
    @RequestMapping("/unreadCount.json")
    public @ResponseBody int getUnreadCountJson() {
        String currentOrcid = getCurrentUserOrcid();
        return notificationManager.getUnreadCount(currentOrcid);
    }

    @RequestMapping("/totalCount.json")
    public @ResponseBody ResponseEntity<?> getTotalCountJson() {
        String currentOrcid = getCurrentUserOrcid();
        int allNotifications = notificationManager.getTotalCount(currentOrcid, true);
        int notArchivedNotifications = notificationManager.getTotalCount(currentOrcid, false);
        return ResponseEntity.ok("{\"all\":" + allNotifications + ",\"nonArchived\":" + notArchivedNotifications + "}");
    }

    @RequestMapping(value = "{id}/read.json")
    public @ResponseBody Notification flagAsRead(HttpServletResponse response, @PathVariable("id") String id) {
        String currentUserOrcid = getCurrentUserOrcid();
        notificationManager.flagAsRead(currentUserOrcid, Long.valueOf(id));
        response.addHeader("X-Robots-Tag", "noindex");
        return notificationManager.findByOrcidAndId(currentUserOrcid, Long.valueOf(id));
    }

    @RequestMapping(value = "{id}/archive.json")
    public @ResponseBody Notification flagAsArchived(HttpServletResponse response, @PathVariable("id") String id) {
        String currentUserOrcid = getCurrentUserOrcid();
        notificationManager.flagAsArchived(currentUserOrcid, Long.valueOf(id), false);
        response.addHeader("X-Robots-Tag", "noindex");
        return notificationManager.findByOrcidAndId(currentUserOrcid, Long.valueOf(id));
    }

    @RequestMapping(value = "{id}/action", method = RequestMethod.GET)
    public ModelAndView executeAction(@PathVariable("id") String id, @RequestParam(value = "target") String redirectUri) {
        notificationManager.setActionedAndReadDate(getCurrentUserOrcid(), Long.valueOf(id));
        return new ModelAndView("redirect:" + redirectUri);
    }

    @RequestMapping(value = "/encrypted/{encryptedId}/action", method = RequestMethod.GET)
    public ModelAndView executeAction(@PathVariable("encryptedId") String encryptedId) {
        String idString;
        try {
            idString = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedId), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Problem decoding " + encryptedId, e);
        }
        Long id = Long.valueOf(idString);
        ActionableNotificationEntity notification = (ActionableNotificationEntity) notificationManager.findActionableNotificationEntity(id);
        String redirectUrl = notification.getAuthorizationUrl();
        String notificationOrcid = notification.getOrcid();
        UserDetails user = getCurrentUser();
        if (user != null) {
            // The user is logged in
            if (!user.getUsername().equals(notificationOrcid)) {
                return new ModelAndView("wrong_user");
            }
        } else {
            redirectUrl += "&orcid=" + notificationOrcid;
        }
        notificationManager.setActionedAndReadDate(notificationOrcid, id);
        return new ModelAndView("redirect:" + redirectUrl);
    }

    @RequestMapping(value = "/frequencies/view", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> getNotificationFrequencies() {
        return emailFrequencyManager.getEmailFrequency(getCurrentUserOrcid());        
    }
    
    @RequestMapping(value = "/frequencies/update/amendUpdates", method = RequestMethod.POST)    
    public ResponseEntity<?> updateSendChangeNotifications(@RequestBody String newFrequency) {
        String orcid = getCurrentUserOrcid();
        try {
            SendEmailFrequency value = SendEmailFrequency.fromValue(newFrequency);
            emailFrequencyManager.updateSendChangeNotifications(orcid, value);
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid value " + newFrequency);
        }        
        return ResponseEntity.ok("{\"status\":" + newFrequency + "}");
    }
    
    @RequestMapping(value = "/frequencies/update/adminUpdates", method = RequestMethod.POST)   
    public ResponseEntity<?> updateSendAdministrativeChangeNotifications(@RequestBody String newFrequency) {
        String orcid = getCurrentUserOrcid();
        try {
            SendEmailFrequency value = SendEmailFrequency.fromValue(newFrequency);
            emailFrequencyManager.updateSendAdministrativeChangeNotifications(orcid, value);
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid value " + newFrequency);
        }        
        return ResponseEntity.ok("{\"status\":" + newFrequency + "}");
    }
    
    @RequestMapping(value = "/frequencies/update/memberUpdates", method = RequestMethod.POST)
    public ResponseEntity<?> updateSendMemberUpdateRequests(@RequestBody String newFrequency) {
        String orcid = getCurrentUserOrcid();
        try {
            SendEmailFrequency value = SendEmailFrequency.fromValue(newFrequency);
            emailFrequencyManager.updateSendMemberUpdateRequests(orcid, value);
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid value " + newFrequency);
        }        
        return ResponseEntity.ok("{\"status\":" + newFrequency + "}");
    }
    
    @RequestMapping(value = "/frequencies/update/tipsUpdates", method = RequestMethod.POST)    
    public ResponseEntity<?> updateSendQuarterlyTips(@RequestBody Boolean enabled) {
        if(enabled == null){
            throw new IllegalArgumentException("Invalid value " + enabled);
        }        
        
        String orcid = getCurrentUserOrcid();
        emailFrequencyManager.updateSendQuarterlyTips(orcid, enabled);
        return ResponseEntity.ok("{\"status\":" + String.valueOf(enabled) + "}");
    }
    
    private void addSourceDescription(List<Notification> notifications) {
        for (Notification notification : notifications) {
            Source source = notification.getSource();
            if (source != null) {
                SourceClientId clientId = source.getSourceClientId();
                if (clientId != null) {
                    String sourcePath = source.retrieveSourcePath();
                    if (sourcePath != null) {
                        ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(sourcePath);
                        if (clientDetails != null) {
                            notification.setSourceDescription(clientDetails.getClientDescription());
                        }

                    }
                }
  
            }        
        }
    }
}
