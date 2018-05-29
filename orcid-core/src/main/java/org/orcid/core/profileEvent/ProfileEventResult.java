package org.orcid.core.profileEvent;

import org.orcid.persistence.jpa.entities.ProfileEventType;

public class ProfileEventResult {

    private ProfileEventType outcome;

    private String orcidId;

    public ProfileEventResult(String orcidId, ProfileEventType outcome) {
        this.setOrcidId(orcidId);
        this.setOutcome(outcome);
    }

    public ProfileEventType getOutcome() {
        return outcome;
    }

    public void setOutcome(ProfileEventType outcome) {
        this.outcome = outcome;
    }

    public String getOrcidId() {
        return orcidId;
    }

    public void setOrcidId(String orcidId) {
        this.orcidId = orcidId;
    }


}
