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


import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.jpa.entities.ProfileEventType;

public class ProfileEventResult {

	private ProfileEventType outcome;

	private OrcidProfile orcidProfile;

	public ProfileEventResult(OrcidProfile orcidProfile, ProfileEventType outcome) {
		this.setOrcidProfile(orcidProfile);
		this.setOutcome(outcome);
	}

	public ProfileEventType getOutcome() {
		return outcome;
	}

	public void setOutcome(ProfileEventType outcome) {
		this.outcome = outcome;
	}

	public OrcidProfile getOrcidProfile() {
		return orcidProfile;
	}

	public void setOrcidProfile(OrcidProfile orcidProfile) {
		this.orcidProfile = orcidProfile;
	}


}
