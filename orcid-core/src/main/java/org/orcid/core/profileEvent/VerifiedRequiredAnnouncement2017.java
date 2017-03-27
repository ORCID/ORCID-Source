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
package org.orcid.core.profileEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.orcid.core.manager.NotificationManager;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.jpa.entities.ProfileEventType;

public class VerifiedRequiredAnnouncement2017 implements ProfileEvent {

    private static Logger LOG = LoggerFactory.getLogger(VerifiedRequiredAnnouncement2017.class);

    @Resource
    private NotificationManager notificationManager;

    @Resource
    private MessageSource messages;

    private OrcidProfile orcidProfile;

    /*
     * export MAVEN_OPTS=
     * "-Xmx1024m -XX:MaxPermSize=256m -Dorg.orcid.config.file=file:///.../ORCID-Source/orcid-persistence/src/main/resources/staging-persistence.properties"
     * ; mvn install; mvn exec:java
     * -Dexec.mainClass="org.orcid.core.profileEvent.ProfileEventManager"
     * -Dexec.args="-bean verifiedRequiredAnnouncement2017 -callOnAll true";
     * 
     */

    private List<ProfileEventType> pes = Collections.unmodifiableList(Arrays.asList(ProfileEventType.VERIFIED_REQUIRED_SKIPPED_2017,
            ProfileEventType.VERIFIED_REQUIRED_HAS_VALIDATED_2017, ProfileEventType.VERIFIED_REQUIRED_SENT_2017, ProfileEventType.VERIFIED_REQUIRED_FAIL_2017));

    VerifiedRequiredAnnouncement2017(OrcidProfile op) {
        this.orcidProfile = op;
    }

    @Override
    public OrcidProfile getOrcidProfile() {
        return orcidProfile;
    }

    @Override
    public ProfileEventResult call() throws Exception {
        String orcidId = getOrcidProfile().getOrcidIdentifier().getPath();

        if (orcidProfile.getOrcidBio() == null || orcidProfile.getOrcidBio().getContactDetails() == null
                || orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail() == null
                || orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue() == null) {
            return new ProfileEventResult(orcidId, ProfileEventType.VERIFIED_REQUIRED_SKIPPED_2017);
        }

        if (orcidProfile.isLocked() || orcidProfile.isDeactivated() || orcidProfile.getOrcidDeprecated() != null
                || !orcidProfile.getOrcidHistory().getClaimed().isValue()) {
            return new ProfileEventResult(orcidId, ProfileEventType.VERIFIED_REQUIRED_SKIPPED_2017);
        }

        Email primaryEmail = orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail();
        if (primaryEmail.isVerified()) {
            return new ProfileEventResult(orcidId, ProfileEventType.VERIFIED_REQUIRED_HAS_VALIDATED_2017);
        } else {
            try {
                boolean sent = notificationManager.sendVerifiedRequiredAnnouncement2017(orcidProfile);
                if (sent) {
                    return new ProfileEventResult(orcidId, ProfileEventType.VERIFIED_REQUIRED_SENT_2017);
                } else {
                    return new ProfileEventResult(orcidId, ProfileEventType.VERIFIED_REQUIRED_FAIL_2017);
                }
            } catch (Exception e) {
                LOG.error("ProfileEventType exception trying to send email to: " + orcidProfile.retrieveOrcidUriAsString(), e);
                return new ProfileEventResult(orcidId, ProfileEventType.VERIFIED_REQUIRED_FAIL_2017);
            }
        }
    }

    @Override
    public List<ProfileEventType> outcomes() {
        return pes;
    }

}
