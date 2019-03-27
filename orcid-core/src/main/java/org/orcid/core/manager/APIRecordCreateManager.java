package org.orcid.core.manager;

import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;

@Deprecated
public interface APIRecordCreateManager {
    OrcidProfile createProfile(OrcidMessage orcidMessage);
}
