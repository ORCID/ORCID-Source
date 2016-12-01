/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
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

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.TemplateManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.jaxb.model.common_rc3.Source;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.Preferences;
import org.orcid.jaxb.model.notification.amended_rc3.NotificationAmended;
import org.orcid.jaxb.model.notification.custom_rc3.NotificationCustom;
import org.orcid.jaxb.model.notification.permission_rc3.NotificationPermission;
import org.orcid.jaxb.model.notification_rc3.Notification;
import org.orcid.jaxb.model.notification_rc3.NotificationType;
import org.orcid.model.notification.institutional_sign_in_rc3.NotificationInstitutionalConnection;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.jpa.entities.ActionableNotificationEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
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

    @Resource
    private NotificationManager notificationManager;

    @Resource
    private NotificationDao notificationDao;

    @Resource
    private TemplateManager templateManager;

    @Resource
    private EncryptionManager encryptionManager;
    
    @Resource
    private OrcidUrlManager orcidUrlManager;
    
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;
    
    @RequestMapping
    public ModelAndView getNotifications() {
        ModelAndView mav = new ModelAndView("notifications");
        OrcidProfile profile = orcidProfileManager.retrieveOrcidProfile(getCurrentUserOrcid(), LoadOptions.BIO_AND_INTERNAL_ONLY);
        mav.addObject("profile", profile);
        return mav;
    }

    @RequestMapping("/notifications.json")
    public @ResponseBody List<Notification> getNotificationsJson(@RequestParam(value = "firstResult", defaultValue = "0") int firstResult,
            @RequestParam(value = "maxResults", defaultValue = "10") int maxResults,
            @RequestParam(value = "includeArchived", defaultValue = "false") boolean includeArchived) {
        String currentOrcid = getCurrentUserOrcid();
        List<Notification> notifications = notificationManager.findByOrcid(currentOrcid, includeArchived, firstResult, maxResults);
        addSubjectToNotifications(notifications);
        setOverwrittenSourceName(notifications);
        return notifications;
    }

    @RequestMapping("/notification-alerts.json")
    public @ResponseBody List<Notification> getNotificationAlertJson() {
        String currentOrcid = getCurrentUserOrcid();
        List<Notification> notifications = notificationManager.findNotificationAlertsByOrcid(currentOrcid);
        addSubjectToNotifications(notifications);
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
        return notificationDao.getUnreadCount(currentOrcid);
    }

    @RequestMapping(value = "/CUSTOM/{id}/notification.html", produces = OrcidApiConstants.HTML_UTF)
    public @ResponseBody String getCustomNotificationHtml(@PathVariable("id") String id) {
        Notification notification = notificationManager.findByOrcidAndId(getCurrentUserOrcid(), Long.valueOf(id));
        if (notification instanceof NotificationCustom) {
            return ((NotificationCustom) notification).getBodyHtml();
        } else {
            return "Notification is of wrong type";
        }
    }

    @RequestMapping(value = "/PERMISSION/{id}/notification.html", produces = OrcidApiConstants.HTML_UTF)
    public ModelAndView getPermissionNotificationHtml(@PathVariable("id") String id) {
        ModelAndView mav = new ModelAndView();
        Notification notification = notificationManager.findByOrcidAndId(getCurrentUserOrcid(), Long.valueOf(id));
        addSourceDescription(notification);
        mav.addObject("notification", notification);
        mav.setViewName("notification/add_activities_notification");
        return mav;
    }

    @RequestMapping(value = "/AMENDED/{id}/notification.html", produces = OrcidApiConstants.HTML_UTF)
    public ModelAndView getAmendedNotificationHtml(@PathVariable("id") String id) {
        ModelAndView mav = new ModelAndView();
        Notification notification = notificationManager.findByOrcidAndId(getCurrentUserOrcid(), Long.valueOf(id));
        addSourceDescription(notification);
        mav.addObject("notification", notification);
        mav.addObject("emailName", notificationManager.deriveEmailFriendlyName(getEffectiveProfile()));
        mav.setViewName("notification/amended_notification");
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
        return mav;
    }
    
    @RequestMapping(value = "{id}/read.json")
    public @ResponseBody Notification flagAsRead(@PathVariable("id") String id) {
        String currentUserOrcid = getCurrentUserOrcid();
        notificationDao.flagAsRead(currentUserOrcid, Long.valueOf(id));
        return notificationManager.findByOrcidAndId(currentUserOrcid, Long.valueOf(id));
    }

    @RequestMapping(value = "{id}/archive.json")
    public @ResponseBody Notification flagAsArchived(@PathVariable("id") String id) {
        String currentUserOrcid = getCurrentUserOrcid();
        notificationDao.flagAsArchived(currentUserOrcid, Long.valueOf(id));
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
        ActionableNotificationEntity notification = (ActionableNotificationEntity) notificationDao.find(id);
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
    
    @RequestMapping(value = "/frequencies/{encryptedEmail}/email-frequencies.json", method = RequestMethod.GET)
    public @ResponseBody Preferences getDefaultPreference(HttpServletRequest request, @PathVariable("encryptedEmail") String encryptedEmail) throws UnsupportedEncodingException {
    	String decryptedEmail = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedEmail), "UTF-8"));
    	OrcidProfile profile = orcidProfileManager.retrieveOrcidProfileByEmail(decryptedEmail);
    	Preferences pref = profile.getOrcidInternal().getPreferences();
        return pref != null ? pref : new Preferences();
    }
    
    @RequestMapping(value = "/frequencies/{encryptedEmail}/email-frequencies.json", method = RequestMethod.POST)
    public @ResponseBody Preferences setPreference(HttpServletRequest request, @RequestBody Preferences preferences, @PathVariable("encryptedEmail") String encryptedEmail) throws UnsupportedEncodingException {
    	String decryptedEmail = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedEmail), "UTF-8"));
    	OrcidProfile profile = orcidProfileManager.retrieveOrcidProfileByEmail(decryptedEmail);
    	orcidProfileManager.updatePreferences(profile.getOrcidIdentifier().getPath(), preferences);
        return preferences;
    }
    
    @RequestMapping(value = "/frequencies/{encryptedEmail}", method = RequestMethod.GET)
    public ModelAndView getNotificationFrequenciesWindow(HttpServletRequest request, 
    		@PathVariable("encryptedEmail") String encryptedEmail) throws Exception {
        ModelAndView result = null;
        String decryptedEmail = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedEmail), "UTF-8"));
        OrcidProfile profile = orcidProfileManager.retrieveOrcidProfileByEmail(decryptedEmail);

        String primaryEmail = profile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue();

        if (decryptedEmail.equals(primaryEmail)) {
        	result = new ModelAndView("email_frequency");
        	result.addObject("primaryEmail", primaryEmail);
        }

        return result;
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
