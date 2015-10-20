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
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.jpa.entities.ProfileEventType;

public class ServiceAnnouncement_1_For_2015 implements ProfileEvent {

    private static Logger LOG = LoggerFactory.getLogger(ServiceAnnouncement_1_For_2015.class);

    @Resource
    private NotificationManager notificationManager;

    @Resource
    private MessageSource messages;

    private OrcidProfile orcidProfile;

    /*
     * export MAVEN_OPTS=
     * "-Xmx1024m -XX:MaxPermSize=256m -Dorg.orcid.config.file=file:///Users/rcpeters/git/ORCID-Source/orcid-persistence/src/main/resources/staging-persistence.properties"
     * ; mvn install; mvn exec:java
     * -Dexec.mainClass="org.orcid.core.profileEvent.ProfileEventManager"
     * -Dexec.args="-bean serviceAnnouncement_1_For_2015 -callOnAll true";
     * 
     * Following https://github.com/ORCID/ORCID-Source/blob/
     * eeae0d0933c68aacc4ef0fbf0846fb99ae9a1257/orcid-core/src/main/java/org/
     * orcid/core/profileEvent/CrossRefEmail.java
     * 
     */

    private List<ProfileEventType> pes = Collections
            .unmodifiableList(Arrays.asList(ProfileEventType.SERVICE_ANNOUNCEMENT_SENT_1_FOR_2015,
                    ProfileEventType.SERVICE_ANNOUNCEMENT_FAIL_1_FOR_2015,
                    ProfileEventType.SERVICE_ANNOUNCEMENT_SKIPPED_1_FOR_2015));

    ServiceAnnouncement_1_For_2015(OrcidProfile op) {
        this.orcidProfile = op;
    }

    @Override
    public OrcidProfile getOrcidProfile() {
        return orcidProfile;
    }

    @Override
    public ProfileEventResult call() throws Exception {
        String orcidId = getOrcidProfile().getOrcidIdentifier().getPath();
        // Doesn't have email check
        if (orcidProfile.getOrcidBio() == null || orcidProfile.getOrcidBio().getContactDetails() == null
                || orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail() == null
                || orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue() == null)
            return new ProfileEventResult(orcidId, ProfileEventType.SERVICE_ANNOUNCEMENT_SKIPPED_1_FOR_2015);

        // Is locked
        if (orcidProfile.isLocked())
            return new ProfileEventResult(orcidId, ProfileEventType.SERVICE_ANNOUNCEMENT_SKIPPED_1_FOR_2015);

        if (orcidProfile.getOrcidHistory() != null) {
            // id deprecated
            if (orcidProfile.getOrcidDeprecated() != null)
                return new ProfileEventResult(orcidId, ProfileEventType.SERVICE_ANNOUNCEMENT_SKIPPED_1_FOR_2015);

            // Isn't claimed
            if (orcidProfile.getOrcidHistory().getClaimed() != null
                    && orcidProfile.getOrcidHistory().getClaimed().isValue() == false)
                return new ProfileEventResult(orcidId, ProfileEventType.SERVICE_ANNOUNCEMENT_SKIPPED_1_FOR_2015);
        }

        // Is deactivated
        if (orcidProfile.isDeactivated())
            return new ProfileEventResult(orcidId, ProfileEventType.SERVICE_ANNOUNCEMENT_SKIPPED_1_FOR_2015);

        ProfileEventResult pes = new ProfileEventResult(orcidId, ProfileEventType.SERVICE_ANNOUNCEMENT_SENT_1_FOR_2015);
        try {
            boolean sent = notificationManager.sendServiceAnnouncement_1_For_2015(orcidProfile);
            if (!sent)
                pes = new ProfileEventResult(orcidId, ProfileEventType.SERVICE_ANNOUNCEMENT_FAIL_1_FOR_2015);
        } catch (Exception e) {
            LOG.error("ProfileEventType exception trying to send email to: " + orcidProfile.retrieveOrcidUriAsString(),
                    e);
            pes = new ProfileEventResult(orcidId, ProfileEventType.SERVICE_ANNOUNCEMENT_FAIL_1_FOR_2015);
        }
        return pes;
    }

    @Override
    public List<ProfileEventType> outcomes() {
        return pes;
	}

}
