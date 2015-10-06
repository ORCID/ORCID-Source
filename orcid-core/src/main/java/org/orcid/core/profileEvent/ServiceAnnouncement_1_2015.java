package org.orcid.core.profileEvent;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.TemplateManager;
import org.orcid.core.manager.impl.MailGunManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.persistence.jpa.entities.ProfileEventType;

public class ServiceAnnouncement_1_2015 implements ProfileEvent {

	private static Logger LOG = LoggerFactory.getLogger(ServiceAnnouncement_1_2015.class);

	@Resource
	private MailGunManager mailGunManager;

	@Resource
	private TemplateManager templateManager;

	@Resource
	private OrcidUrlManager orcidUrlManager;

	@Resource
	private NotificationManager notificationManager;
	
    @Resource
    private MessageSource messages;


	private OrcidProfile orcidProfile;

	/*
	 * export MAVEN_OPTS=
	 * "-Xmx1024m -XX:MaxPermSize=256m -Dorg.orcid.config.file=file:///Users/rcpeters/git/ORCID-Source/orcid-persistence/src/main/resources/staging-persistence.properties"
	 * ;
	 * mvn install 
	 * mvn exec:java
	 * -Dexec.mainClass="org.orcid.core.profileEvent.ProfileEventManager"
	 * -Dexec.args="-bean serviceAnnouncement_1_2015 -callOnAll true"
	 * 
	 * Following https://github.com/ORCID/ORCID-Source/blob/
	 * eeae0d0933c68aacc4ef0fbf0846fb99ae9a1257/orcid-core/src/main/java/org/
	 * orcid/core/profileEvent/CrossRefEmail.java
	 * 
	 */

	private List<ProfileEventType> pes = Collections.unmodifiableList(Arrays.asList(
			ProfileEventType.SERVICE_ANNOUNCEMENT_SENT_1_2015, ProfileEventType.SERVICE_ANNOUNCEMENT_FAIL_1_2015,
			ProfileEventType.SERVICE_ANNOUNCEMENT_SKIPPED_1_2015));

	ServiceAnnouncement_1_2015(OrcidProfile op) {
		this.orcidProfile = op;
	}

	@Override
	public ProfileEventType call() throws Exception {
		if (orcidProfile.getOrcidBio() == null || orcidProfile.getOrcidBio().getContactDetails() == null
				|| orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail() == null
				|| orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue() == null)
			return ProfileEventType.SERVICE_ANNOUNCEMENT_SKIPPED_1_2015;

		boolean needsVerification = !orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().isVerified()
				&& orcidProfile.getType().equals(OrcidType.USER) && !orcidProfile.isDeactivated();

		ProfileEventType pet = ProfileEventType.SERVICE_ANNOUNCEMENT_SENT_1_2015;
		try {
			String email = orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue();
			String emailFriendlyName = notificationManager.deriveEmailFriendlyName(orcidProfile);
			Map<String, Object> templateParams = new HashMap<String, Object>();
			templateParams.put("emailName", emailFriendlyName);
			String verificationUrl = null;
			verificationUrl = notificationManager.createVerificationUrl(email, orcidUrlManager.getBaseUrl());
			if (needsVerification) {
				templateParams.put("verificationUrl", verificationUrl);
			}
			templateParams.put("orcid", orcidProfile.getOrcidIdentifier().getPath());
			templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
			notificationManager.addMessageParams(templateParams, orcidProfile);
			String text = templateManager.processTemplate("service_announcement_1_2015.ftl", templateParams);
			String html = templateManager.processTemplate("service_announcement_1_2015_html.ftl", templateParams);
			if (!mailGunManager.sendEmail("support@notify.orcid.org", email, "Service Announcment", text, html))
				pet = ProfileEventType.SERVICE_ANNOUNCEMENT_FAIL_1_2015;
		} catch (Exception e) {
			LOG.error("ProfileEventType exception trying to send email to: " + orcidProfile.retrieveOrcidUriAsString(),
					e);
			pet = ProfileEventType.SERVICE_ANNOUNCEMENT_FAIL_1_2015;
		}
		return pet;

	}

	@Override
	public List<ProfileEventType> outcomes() {
		return pes;
	}

}
