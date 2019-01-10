package org.orcid.api.publicV3.server.security;

import org.orcid.jaxb.model.v3.rc2.common.VisibilityType;
import org.orcid.jaxb.model.v3.rc2.record.ActivitiesContainer;
import org.orcid.jaxb.model.v3.rc2.record.Addresses;
import org.orcid.jaxb.model.v3.rc2.record.Biography;
import org.orcid.jaxb.model.v3.rc2.record.Emails;
import org.orcid.jaxb.model.v3.rc2.record.GroupsContainer;
import org.orcid.jaxb.model.v3.rc2.record.Keywords;
import org.orcid.jaxb.model.v3.rc2.record.OtherNames;
import org.orcid.jaxb.model.v3.rc2.record.Person;
import org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.rc2.record.PersonalDetails;
import org.orcid.jaxb.model.v3.rc2.record.Record;
import org.orcid.jaxb.model.v3.rc2.record.ResearcherUrls;
import org.orcid.jaxb.model.v3.rc2.record.WorkBulk;
import org.orcid.jaxb.model.v3.rc2.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.PeerReviews;

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
