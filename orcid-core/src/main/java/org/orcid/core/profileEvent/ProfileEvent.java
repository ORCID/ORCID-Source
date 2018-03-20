package org.orcid.core.profileEvent;

import java.util.List;
import org.orcid.jaxb.model.message.OrcidProfile;
import java.util.concurrent.Callable;

import org.orcid.persistence.jpa.entities.ProfileEventType;

public interface ProfileEvent extends Callable<ProfileEventResult> {

	public List<ProfileEventType> outcomes();

	public OrcidProfile getOrcidProfile();

}
