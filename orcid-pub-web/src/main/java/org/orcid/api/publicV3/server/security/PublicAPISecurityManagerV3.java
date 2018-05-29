package org.orcid.api.publicV3.server.security;

import org.orcid.jaxb.model.v3.rc1.common.VisibilityType;
import org.orcid.jaxb.model.v3.rc1.record.ActivitiesContainer;
import org.orcid.jaxb.model.v3.rc1.record.Addresses;
import org.orcid.jaxb.model.v3.rc1.record.Biography;
import org.orcid.jaxb.model.v3.rc1.record.Emails;
import org.orcid.jaxb.model.v3.rc1.record.GroupsContainer;
import org.orcid.jaxb.model.v3.rc1.record.Keywords;
import org.orcid.jaxb.model.v3.rc1.record.OtherNames;
import org.orcid.jaxb.model.v3.rc1.record.Person;
import org.orcid.jaxb.model.v3.rc1.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.rc1.record.PersonalDetails;
import org.orcid.jaxb.model.v3.rc1.record.Record;
import org.orcid.jaxb.model.v3.rc1.record.ResearcherUrls;
import org.orcid.jaxb.model.v3.rc1.record.WorkBulk;
import org.orcid.jaxb.model.v3.rc1.record.summary.ActivitiesSummary;

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
}
