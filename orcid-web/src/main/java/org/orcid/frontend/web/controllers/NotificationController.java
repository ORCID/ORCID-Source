package org.orcid.frontend.web.controllers;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.PreferenceManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.frontend.web.controllers.helper.UserSession;
import org.orcid.frontend.web.forms.PreferencesForm;
import org.orcid.jaxb.model.v3.dev1.common.Source;
import org.orcid.jaxb.model.v3.dev1.notification.Notification;
import org.orcid.jaxb.model.v3.dev1.notification.NotificationType;
import org.orcid.jaxb.model.v3.dev1.notification.amended.NotificationAmended;
import org.orcid.jaxb.model.v3.dev1.notification.custom.NotificationAdministrative;
import org.orcid.jaxb.model.v3.dev1.notification.custom.NotificationCustom;
import org.orcid.jaxb.model.v3.dev1.notification.custom.NotificationServiceAnnouncement;
import org.orcid.jaxb.model.v3.dev1.notification.custom.NotificationTip;
import org.orcid.jaxb.model.v3.dev1.notification.permission.NotificationPermission;
import org.orcid.model.v3.dev1.notification.institutional_sign_in.NotificationInstitutionalConnection;
import org.orcid.persistence.constants.SendEmailFrequency;
import org.orcid.persistence.jpa.entities.ActionableNotificationEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
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
    private UserSession userSession;
    
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

    @RequestMapping("/notifications.json")
    public @ResponseBody List<Notification> getNotificationsJson(@RequestParam(value = "firstResult", defaultValue = "0") int firstResult,
            @RequestParam(value = "maxResults", defaultValue = "10") int maxResults,
            @RequestParam(value = "includeArchived", defaultValue = "false") boolean includeArchived) {
        String currentOrcid = getCurrentUserOrcid();
        List<Notification> notifications = notificationManager.findByOrcid(currentOrcid, includeArchived, firstResult, maxResults);
        notifications = archiveObsoleteNotifications(currentOrcid, notifications);
        addSubjectToNotifications(notifications);
        setOverwrittenSourceName(notifications);
        return notifications;
    }

    @RequestMapping("/notification-alerts.json")
    public @ResponseBody List<Notification> getNotificationAlertJson() {
        String currentOrcid = getCurrentUserOrcid();
        List<Notification> notifications = notificationManager.findNotificationAlertsByOrcid(currentOrcid);
        notifications = archiveObsoleteNotifications(currentOrcid, notifications);
        notifications = notifications.stream().filter(n -> !userSession.getSuppressedNotificationAlertIds().contains(n.getPutCode())).collect(Collectors.toList());
        addSubjectToNotifications(notifications);
        return notifications;
    }

    private List<Notification> archiveObsoleteNotifications(String currentOrcid, List<Notification> notifications) {
        if (!userSession.isObsoleteNotificationAlertsCheckDone()) {
            notifications = notificationManager.filterActionedNotificationAlerts(notifications, currentOrcid);
            userSession.setObsoleteNotificationAlertsCheckDone(true);
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

    @RequestMapping(value = "/CUSTOM/{id}/notification.html", produces = OrcidApiConstants.HTML_UTF)
    public ModelAndView getCustomNotificationHtml(HttpServletResponse response, @PathVariable("id") String id) {
        ModelAndView mav = new ModelAndView();
        Notification notification = notificationManager.findByOrcidAndId(getCurrentUserOrcid(), Long.valueOf(id));
        if (notification instanceof NotificationCustom) {
            String html =  ((NotificationCustom) notification).getBodyHtml();
            int start = html.indexOf("<body>")+"<body>".length();
            int end = html.indexOf("</body>", start);
            String body = html.substring(start, end);
            mav.addObject("body", body);
        }
        mav.addObject("notification", notification);
        mav.setViewName("notification/custom_notification");
        mav.addObject("noIndex", true);
        return mav;
    }

    @RequestMapping(value = "/PERMISSION/{id}/notification.html", produces = OrcidApiConstants.HTML_UTF)
    public ModelAndView getPermissionNotificationHtml(@PathVariable("id") String id) {
        ModelAndView mav = new ModelAndView();
        Notification notification = notificationManager.findByOrcidAndId(getCurrentUserOrcid(), Long.valueOf(id));
        addSourceDescription(notification);
        mav.addObject("notification", notification);
        mav.setViewName("notification/add_activities_notification");
        mav.addObject("noIndex", true);
        return mav;
    }

    @RequestMapping(value = "/AMENDED/{id}/notification.html", produces = OrcidApiConstants.HTML_UTF)
    public ModelAndView getAmendedNotificationHtml(@PathVariable("id") String id) {
        String orcid = getEffectiveUserOrcid();
        ProfileEntity record = profileEntityCacheManager.retrieve(orcid);
        ModelAndView mav = new ModelAndView();
        Notification notification = notificationManager.findByOrcidAndId(getCurrentUserOrcid(), Long.valueOf(id));
        addSourceDescription(notification);
        mav.addObject("notification", notification);
        mav.addObject("emailName", notificationManager.deriveEmailFriendlyName(record));
        mav.setViewName("notification/amended_notification");
        mav.addObject("noIndex", true);
        return mav;
    }

    @RequestMapping(value = "/INSTITUTIONAL_CONNECTION/{id}/notification.html", produces = OrcidApiConstants.HTML_UTF)
    public ModelAndView getInstitutionalConnectionNotificationHtml(@PathVariable("id") String id) throws UnsupportedEncodingException {
        ModelAndView mav = new ModelAndView();        
        Notification notification = notificationManager.findByOrcidAndId(getCurrentUserOrcid(), Long.valueOf(id));
        String clientId = notification.getSource().retrieveSourcePath();
        ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(clientId);
        String authorizationUrl = notificationManager.buildAuthorizationUrlForInstitutionalSignIn(clientDetails);
        addSourceDescription(notification);
        mav.addObject("notification", notification);               
        mav.addObject("baseUri", getBaseUri());
        mav.addObject("clientId", clientId);
        mav.addObject("authorizationUrl", authorizationUrl);
        mav.setViewName("notification/institutional_connection_notification");
        mav.addObject("noIndex", true);
        return mav;
    }
    
    @RequestMapping(value = "/SERVICE_ANNOUNCEMENT/{id}/notification.html", produces = OrcidApiConstants.HTML_UTF)
    public ModelAndView getServiceAnnouncementNotificationHtml(HttpServletResponse response, @PathVariable("id") String id) throws UnsupportedEncodingException {
        ModelAndView mav = new ModelAndView();
        Notification notification = notificationManager.findByOrcidAndId(getCurrentUserOrcid(), Long.valueOf(id));
        if (notification instanceof NotificationServiceAnnouncement) {
            String html =  ((NotificationServiceAnnouncement) notification).getBodyHtml();
            int start = html.indexOf("<body>")+"<body>".length();
            int end = html.indexOf("</body>", start);
            String body = html.substring(start, end);
            mav.addObject("body", body);
        }
        mav.addObject("notification", notification);
        mav.setViewName("notification/custom_notification");
        mav.addObject("noIndex", true);
        return mav;
    }
    
    @RequestMapping(value = "/TIP/{id}/notification.html", produces = OrcidApiConstants.HTML_UTF)
    public ModelAndView getTipNotificationHtml(HttpServletResponse response, @PathVariable("id") String id) throws UnsupportedEncodingException {
        ModelAndView mav = new ModelAndView();
        Notification notification = notificationManager.findByOrcidAndId(getCurrentUserOrcid(), Long.valueOf(id));
        if (notification instanceof NotificationTip) {
            String html =  ((NotificationServiceAnnouncement) notification).getBodyHtml();
            int start = html.indexOf("<body>")+"<body>".length();
            int end = html.indexOf("</body>", start);
            String body = html.substring(start, end);
            mav.addObject("body", body);
        }
        mav.addObject("notification", notification);
        mav.setViewName("notification/custom_notification");
        mav.addObject("noIndex", true);
        return mav;
    }
    
    @RequestMapping(value = "/ADMINISTRATIVE/{id}/notification.html", produces = OrcidApiConstants.HTML_UTF)
    public ModelAndView getAdministrativeNotificationHtml(HttpServletResponse response, @PathVariable("id") String id) throws UnsupportedEncodingException {
        ModelAndView mav = new ModelAndView();
        Notification notification = notificationManager.findByOrcidAndId(getCurrentUserOrcid(), Long.valueOf(id));
        if (notification instanceof NotificationAdministrative) {
            String html =  ((NotificationServiceAnnouncement) notification).getBodyHtml();
            int start = html.indexOf("<body>")+"<body>".length();
            int end = html.indexOf("</body>", start);
            String body = html.substring(start, end);
            mav.addObject("body", body);
        }
        mav.addObject("notification", notification);
        mav.setViewName("notification/custom_notification");
        mav.addObject("noIndex", true);
        return mav;
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
        String notificationOrcid = notification.getProfile().getId();
        OrcidProfileUserDetails user = getCurrentUser();
        if (user != null) {
            // The user is logged in
            if (!user.getOrcid().equals(notificationOrcid)) {
                return new ModelAndView("wrong_user");
            }
        } else {
            redirectUrl += "&orcid=" + notificationOrcid;
        }
        notificationManager.setActionedAndReadDate(notificationOrcid, id);
        return new ModelAndView("redirect:" + redirectUrl);
    }
    
    @RequestMapping(value = "{id}/suppressAlert.json")
    public @ResponseBody void suppressAlert(HttpServletResponse response, @PathVariable("id") String id) {
        userSession.getSuppressedNotificationAlertIds().add(Long.valueOf(id));
        response.addHeader("X-Robots-Tag", "noindex");
    }
    
    @RequestMapping(value = "/frequencies/{encryptedEmail}/email-frequencies.json", method = RequestMethod.GET)
    public @ResponseBody PreferencesForm getDefaultPreference(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("encryptedEmail") String encryptedEmail) throws UnsupportedEncodingException {
        response.addHeader("X-Robots-Tag", "noindex");
        String decryptedEmail = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedEmail), "UTF-8"));
        PreferencesForm preferences = new PreferencesForm();
        String orcid = emailManagerReadOnly.findOrcidIdByEmail(decryptedEmail);

        if (PojoUtil.isEmpty(orcid)) {
            throw new IllegalArgumentException("Invalid email provided");
        }

        ProfileEntity entity = profileEntityCacheManager.retrieve(orcid);
        preferences.setOrcid(orcid);
        preferences.setSendEmailFrequencyDays(String.valueOf(entity.getSendEmailFrequencyDays()));
        return preferences;
    }
    
    @RequestMapping(value = "/frequencies/{encryptedEmail}/email-frequencies.json", method = RequestMethod.POST)
    public @ResponseBody PreferencesForm setPreference(HttpServletRequest request, HttpServletResponse response, @RequestBody String emailFrequencyDays,
            @PathVariable("encryptedEmail") String encryptedEmail) throws UnsupportedEncodingException {
        response.addHeader("X-Robots-Tag", "noindex");
        String decryptedEmail = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedEmail), "UTF-8"));
        String orcid = emailManagerReadOnly.findOrcidIdByEmail(decryptedEmail);
        SendEmailFrequency newFrequency = null;
        for (SendEmailFrequency f : SendEmailFrequency.values()) {
            if (f.value().equals(emailFrequencyDays)) {
                newFrequency = f;
                break;
            }
        }

        if (newFrequency == null) {
            throw new IllegalArgumentException("Invalid value: " + emailFrequencyDays);
        }
        PreferencesForm preferences = new PreferencesForm();
        preferences.setOrcid(orcid);
        if (!preferenceManager.updateEmailFrequencyDays(orcid, newFrequency)) {
            ProfileEntity entity = profileEntityCacheManager.retrieve(orcid);
            preferences.setSendEmailFrequencyDays(String.valueOf(entity.getSendEmailFrequencyDays()));
        } else {
            preferences.setSendEmailFrequencyDays(emailFrequencyDays);
        }

        return preferences;
    }
    
    @RequestMapping(value = "/frequencies/{encryptedEmail}", method = RequestMethod.GET)
    public ModelAndView getNotificationFrequenciesWindow(HttpServletRequest request, @PathVariable("encryptedEmail") String encryptedEmail) throws Exception {
        ModelAndView result = null;
        String decryptedEmail = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedEmail), "UTF-8"));

        if (emailManagerReadOnly.isPrimaryEmail(decryptedEmail)) {
            result = new ModelAndView("email_frequency");
            result.addObject("primaryEmail", decryptedEmail);
            result.addObject("noIndex", true);
        }

        return result;
    }

    @RequestMapping(value = "/frequencies/view", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> getNotificationFrequencies() {
        return emailFrequencyManager.getEmailFrequency(getCurrentUserOrcid());        
    }
    
    @RequestMapping(value = "/frequencies/update/amendUpdates", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void updateSendChangeNotifications(@RequestBody String newFrequency) {
        String orcid = getCurrentUserOrcid();
        try {
            SendEmailFrequency value = SendEmailFrequency.fromValue(newFrequency);
            emailFrequencyManager.updateSendChangeNotifications(orcid, value);
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid value " + newFrequency);
        }        
    }
    
    @RequestMapping(value = "/frequencies/update/adminUpdates", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void updateSendAdministrativeChangeNotifications(@RequestBody String newFrequency) {
        String orcid = getCurrentUserOrcid();
        try {
            SendEmailFrequency value = SendEmailFrequency.fromValue(newFrequency);
            emailFrequencyManager.updateSendAdministrativeChangeNotifications(orcid, value);
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid value " + newFrequency);
        }        
    }
    
    @RequestMapping(value = "/frequencies/update/memberUpdates", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void updateSendMemberUpdateRequests(@RequestBody String newFrequency) {
        String orcid = getCurrentUserOrcid();
        try {
            SendEmailFrequency value = SendEmailFrequency.fromValue(newFrequency);
            emailFrequencyManager.updateSendMemberUpdateRequests(orcid, value);
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid value " + newFrequency);
        }        
    }
    
    @RequestMapping(value = "/frequencies/update/tipsUpdates", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void updateSendQuarterlyTips(@RequestBody Boolean enabled) {
        if(enabled == null){
            throw new IllegalArgumentException("Invalid value " + enabled);
        }        
        
        String orcid = getCurrentUserOrcid();
        emailFrequencyManager.updateSendQuarterlyTips(orcid, enabled);
    }
    
    private void addSourceDescription(Notification notification) {
        Source source = notification.getSource();
        if (source != null) {
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
