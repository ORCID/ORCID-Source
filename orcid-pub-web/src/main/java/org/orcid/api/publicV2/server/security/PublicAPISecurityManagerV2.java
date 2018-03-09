package org.orcid.api.publicV2.server.security;

import org.orcid.jaxb.model.common_v2.VisibilityType;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record_v2.ActivitiesContainer;
import org.orcid.jaxb.model.record_v2.Addresses;
import org.orcid.jaxb.model.record_v2.Biography;
import org.orcid.jaxb.model.record_v2.Emails;
import org.orcid.jaxb.model.record_v2.GroupsContainer;
import org.orcid.jaxb.model.record_v2.Keywords;
import org.orcid.jaxb.model.record_v2.OtherNames;
import org.orcid.jaxb.model.record_v2.Person;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_v2.PersonalDetails;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.record_v2.ResearcherUrls;
import org.orcid.jaxb.model.record_v2.WorkBulk;

public interface PublicAPISecurityManagerV2 {
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
}
