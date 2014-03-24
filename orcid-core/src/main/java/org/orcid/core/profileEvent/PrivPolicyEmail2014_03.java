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
import org.springframework.context.MessageSource;

public class PrivPolicyEmail2014_03 implements ProfileEvent {

    @Resource
    private MessageSource messages;

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

    private List<ProfileEventType> pes = Collections.unmodifiableList(Arrays.asList(ProfileEventType.POLICY_UPDATE_2014_03_SENT, ProfileEventType.POLICY_UPDATE_2014_03_FAIL, ProfileEventType.POLICY_UPDATE_2014_03_SKIPPED));

    public List<ProfileEventType> outcomes() {
        return pes;
    }

    public PrivPolicyEmail2014_03(OrcidProfile orcidProfile) {
        this.orcidProfile = orcidProfile;
    }

    public ProfileEventType call() throws Exception {
        boolean primaryNotNull = orcidProfile.getOrcidBio() != null && orcidProfile.getOrcidBio().getContactDetails() != null
                && orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail() != null
                && orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue() != null;

        boolean isUser = primaryNotNull
                && orcidProfile.getType().equals(OrcidType.USER) && !orcidProfile.isDeactivated();

        if (isUser) {
            ProfileEventType pet = ProfileEventType.POLICY_UPDATE_2014_03_SENT;
            try {
                if (!notificationManager.sendPrivPolicyEmail2014_03(orcidProfile, new URI(orcidUrlManager.getBaseUrl())))
                    pet = ProfileEventType.POLICY_UPDATE_2014_03_FAIL;

            } catch (Exception e) {
                LOG.error("ProfileEventType exception trying to send email to: " + orcidProfile.retrieveOrcidUriAsString(), e);
                pet = ProfileEventType.POLICY_UPDATE_2014_03_FAIL;
            }
            return pet;
        }
        return ProfileEventType.POLICY_UPDATE_2014_03_FAIL;
    }

}
