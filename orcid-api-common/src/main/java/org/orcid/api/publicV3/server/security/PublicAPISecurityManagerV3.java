package org.orcid.api.publicV3.server.security;

import org.orcid.jaxb.model.v3.release.common.VisibilityType;
import org.orcid.jaxb.model.v3.release.record.ActivitiesContainer;
import org.orcid.jaxb.model.v3.release.record.Addresses;
import org.orcid.jaxb.model.v3.release.record.Biography;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.jaxb.model.v3.release.record.GroupsContainer;
import org.orcid.jaxb.model.v3.release.record.Keywords;
import org.orcid.jaxb.model.v3.release.record.OtherNames;
import org.orcid.jaxb.model.v3.release.record.Person;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.release.record.PersonalDetails;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;
import org.orcid.jaxb.model.v3.release.record.WorkBulk;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviews;

public interface PublicAPISecurityManagerV3 {
    void checkIsPublic(VisibilityType visibilityType);

    void checkIsPublic(Biography biography);

    void filter(ActivitiesSummary activitiesSummary);

    void filter(ActivitiesContainer container);

    void filter(GroupsContainer container);

    void filter(Addresses addresses);

    void filter(Emails emails);

    void filter(Keywords keywords);

    void filter(OtherNames otherNames);

    void filter(PersonExternalIdentifiers extIds);

    void filter(ResearcherUrls researcherUrls);

    void filter(PersonalDetails personalDetails);

    void filter(Person person);

    void filter(Record record);

    void filter(WorkBulk workBulk);

    void filter(PeerReviews peerReviews);
}
