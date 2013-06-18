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
package org.orcid.core.profileEvent;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.TemplateManager;
import org.orcid.core.manager.impl.MailGunManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrossRefEmail implements ProfileEvent {

    @Resource
    private MailGunManager mailGunManager;

    @Resource
    private TemplateManager templateManager;

    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Resource
    private NotificationManager notificationManager;

    private OrcidProfile orcidProfile;

    private static Logger LOG = LoggerFactory.getLogger(ProfileEventManager.class);

    private List<ProfileEventType> pes = Collections.unmodifiableList(Arrays.asList(ProfileEventType.EMAIL_VERIFY_CROSSREF_MARKETING_FAIL,
            ProfileEventType.EMAIL_VERIFY_CROSSREF_MARKETING_SENT, ProfileEventType.EMAIL_VERIFY_CROSSREF_MARKETING_SKIPPED));

    public List<ProfileEventType> outcomes() {
        return pes;
    }

    public CrossRefEmail(OrcidProfile orcidProfile) {
        this.orcidProfile = orcidProfile;
    }

    public ProfileEventType call() throws Exception {
        boolean primaryNotNull = orcidProfile.getOrcidBio() != null && orcidProfile.getOrcidBio().getContactDetails() != null
                && orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail() != null
                && orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue() != null;

        boolean needsVerification = primaryNotNull && !orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().isVerified()
                && orcidProfile.getType().equals(OrcidType.USER) && !orcidProfile.isDeactivated();

        if (needsVerification) {
            ProfileEventType pet = ProfileEventType.EMAIL_VERIFY_CROSSREF_MARKETING_SENT;
            try {
                String email = orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue();
                String emailFriendlyName = notificationManager.deriveEmailFriendlyName(orcidProfile);
                Map<String, Object> templateParams = new HashMap<String, Object>();
                templateParams.put("emailName", emailFriendlyName);
                String verificationUrl = null;
                try {
                    verificationUrl = notificationManager.createVerificationUrl(email, new URI(orcidUrlManager.getBaseUrl()));
                } catch (URISyntaxException e) {
                    LOG.debug("SendEventEmail exception", e);
                }
                templateParams.put("verificationUrl", verificationUrl);
                templateParams.put("orcid", orcidProfile.getOrcid().getValue());
                templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
                String text = templateManager.processTemplate("verification_email_w_crossref.ftl", templateParams);
                String html = templateManager.processTemplate("verification_email_w_crossref_html.ftl", templateParams);
                if (!mailGunManager.sendEmail("support@verify.orcid.org", email, "Please verify your email", text, html))
                    pet = ProfileEventType.EMAIL_VERIFY_CROSSREF_MARKETING_FAIL;
            } catch (Exception e) {
                LOG.error("ProfileEventType exception trying to send email to: " + orcidProfile.getOrcidId(), e);
                pet = ProfileEventType.EMAIL_VERIFY_CROSSREF_MARKETING_FAIL;
            }
            return pet;
        }
        return ProfileEventType.EMAIL_VERIFY_CROSSREF_MARKETING_SKIPPED;
    }

}
