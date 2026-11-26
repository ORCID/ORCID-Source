package org.orcid.core.manager.v3.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.LocaleUtils;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.TemplateManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.jaxb.model.common.AvailableLocales;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.HtmlUtils;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.jpa.entities.DelegateInvitationEntity;
import org.orcid.persistence.jpa.entities.NotificationDelegateEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

public class DelegateInvitationMessaging {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelegateInvitationMessaging.class);

    @Resource
    private NotificationDao notificationDao;


    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private TemplateManager templateManager;

    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Resource(name = "recordNameManagerReadOnlyV3")
    private RecordNameManagerReadOnly recordNameManagerReadOnly;

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
        String otherOrcid = owner.equals(invitation.getSenderOrcid()) ? invitation.getRecipientOrcid() : invitation.getSenderOrcid();
        boolean invitedByEmail = key.endsWith(".sender.email");
        String identity = invitedByEmail ? invitation.getEnteredEmail() : otherOrcid;
        Map<String, Object> params = new HashMap<>();
        String subject;
        String body;
        if (key.startsWith("created.sender.")) {
            String display = identity;
            String link = "";
            if (!invitedByEmail) {
                String name = publicName(otherOrcid);
                if (StringUtils.isNotBlank(name)) {
                    display = name;
                }
                link = orcidUrlManager.getBaseUrl().replaceAll("/+$", "") + "/" + otherOrcid;
            }
            Object[] args = new Object[]{display, link};
            subject = messages.getMessage("delegate.invitation." + key + ".subject", args, locale);
            body = messages.getMessage("delegate.invitation." + key + ".body", args, locale);
            String safeLink = HtmlUtils.htmlEscape(link);
            Object[] htmlArgs = new Object[]{"<strong>" + HtmlUtils.htmlEscape(display) + "</strong>",
                    "(<a href=\"" + safeLink + "\">" + safeLink + "</a>)"};
            String html = messages.getMessage("delegate.invitation." + key + ".bodyhtml", htmlArgs, locale);
            // brackets already come from the message pattern, avoid doubling them
            params.put("bodyHtml", html.replace("((<a", "(<a").replace("</a>))", "</a>)"));
        } else if (actionable) {
            String name = publicName(otherOrcid);
            String display = StringUtils.isNotBlank(name) ? name : otherOrcid;
            String recordUrl = orcidUrlManager.getBaseUrl().replaceAll("/+$", "") + "/" + otherOrcid;
            Object[] args = new Object[]{display, recordUrl};
            subject = messages.getMessage("delegate.invitation.created.recipient.subject", args, locale);
            body = messages.getMessage("delegate.invitation.created.recipient.body", args, locale);
            params.put("senderName", display);
            params.put("senderUrl", recordUrl);
            params.put("recipientIntro", messages.getMessage("delegate.invitation.recipient.intro", null, locale));
            params.put("recipientInfo", messages.getMessage("delegate.invitation.recipient.info", null, locale));
            params.put("recipientDeadline", messages.getMessage("delegate.invitation.recipient.deadline", null, locale));
            params.put("recipientExpired", messages.getMessage("delegate.invitation.recipient.expired", null, locale));
            params.put("acceptHeading", messages.getMessage("delegate.invitation.recipient.acceptHeading", null, locale));
            params.put("acceptDescription", messages.getMessage("delegate.invitation.recipient.acceptDescription", new Object[]{display}, locale));
            params.put("declineHeading", messages.getMessage("delegate.invitation.recipient.declineHeading", null, locale));
            params.put("declineDescription", messages.getMessage("delegate.invitation.recipient.declineDescription", new Object[]{display}, locale));
        } else {
            subject = messages.getMessage("delegate.invitation." + key + ".subject", null, locale);
            body = messages.getMessage("delegate.invitation." + key + ".body", new Object[]{identity}, locale);
        }
        params.put("subject", subject);
        params.put("body", body);
        params.put("actionable", actionable);
        if (actionable) {
            String base = orcidUrlManager.getBaseUrl().replaceAll("/+$", "");
            String link = base + "/delegate-invitation?token=" + invitationToken + "&decision=";
            params.put("acceptUrl", link + "ACCEPT");
            params.put("declineUrl", link + "DECLINE");
            LOGGER.debug("Delegate invitation {} for recipient {}: accept link {} decline link {}", invitation.getId(), owner,
                    link + "ACCEPT", link + "DECLINE");
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

    private String publicName(String orcid) {
        return recordNameManagerReadOnly.fetchDisplayablePublicName(orcid);
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