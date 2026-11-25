package org.orcid.core.manager.v3.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.LocaleUtils;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.TemplateManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.jaxb.model.common.AvailableLocales;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.jpa.entities.DelegateInvitationEntity;
import org.orcid.persistence.jpa.entities.NotificationDelegateEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.context.MessageSource;

public class DelegateInvitationMessaging {

    @Resource
    private NotificationDao notificationDao;


    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private TemplateManager templateManager;

    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Resource(name = "delegateInvitationMessages")
    private MessageSource messages;

    public void created(DelegateInvitationEntity invitation, String invitationToken) {
        create(invitation, invitation.getSenderOrcid(), senderLookupKey("created", invitation), false, null);
        NotificationDelegateEntity actionable = create(invitation, invitation.getRecipientOrcid(), "created.recipient", true, invitationToken);
        invitation.setActionableNotificationId(actionable.getId());
    }

    public void responded(DelegateInvitationEntity invitation) {
        String event = invitation.getStatus() == DelegateInvitationEntity.Status.ACCEPTED ? "accepted" : "declined";
        String senderKey = invitation.getStatus() == DelegateInvitationEntity.Status.DECLINED ? senderLookupKey(event, invitation) : event + ".sender";
        create(invitation, invitation.getSenderOrcid(), senderKey, false, null);
        create(invitation, invitation.getRecipientOrcid(), event + ".recipient", false, null);
    }

    private String senderLookupKey(String event, DelegateInvitationEntity invitation) {
        return event + ".sender." + (invitation.getLookupType() == DelegateInvitationEntity.LookupType.EMAIL ? "email" : "orcid");
    }

    private NotificationDelegateEntity create(DelegateInvitationEntity invitation, String owner, String key, boolean actionable, String invitationToken) {
        Locale locale = localeFor(owner);
        String subject = messages.getMessage("delegate.invitation." + key + ".subject", null, locale);
        String otherOrcid = owner.equals(invitation.getSenderOrcid()) ? invitation.getRecipientOrcid() : invitation.getSenderOrcid();
        String identity = key.endsWith(".sender.email") ? invitation.getEnteredEmail() : otherOrcid;
        String body = messages.getMessage("delegate.invitation." + key + ".body", new Object[]{identity}, locale);
        Map<String, Object> params = new HashMap<>();
        params.put("subject", subject);
        params.put("body", body);
        params.put("actionable", actionable);
        if (actionable) {
            String base = orcidUrlManager.getBaseUrl().replaceAll("/+$", "");
            String link = base + "/delegate-invitation?token=" + invitationToken + "&decision=";
            params.put("acceptUrl", link + "ACCEPT");
            params.put("declineUrl", link + "DECLINE");
            params.put("acceptLabel", messages.getMessage("delegate.invitation.accept", null, locale));
            params.put("declineLabel", messages.getMessage("delegate.invitation.decline", null, locale));
        }

        NotificationDelegateEntity notification = new NotificationDelegateEntity();
        notification.setOrcid(owner);
        notification.setSourceId(owner);
        notification.setSubject(subject);
        notification.setNotificationSubject(subject);
        notification.setBodyText(templateManager.processTemplate("delegate_invitation.ftl", params, locale));
        notification.setBodyHtml(templateManager.processTemplate("delegate_invitation_html.ftl", params, locale));
        notification.setLang(locale.toLanguageTag());
        notification.setSendable(true);
        notificationDao.persist(notification);
        return notification;
    }

    private Locale localeFor(String orcid) {
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        if (profile != null && profile.getLocale() != null) {
            try {
                return LocaleUtils.toLocale(AvailableLocales.valueOf(profile.getLocale()).value());
            } catch (IllegalArgumentException e) {
                return Locale.ENGLISH;
            }
        }
        return Locale.ENGLISH;
    }
}