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
package org.orcid.api.publicV2.server.security;

import org.orcid.jaxb.model.common_rc4.VisibilityType;
import org.orcid.jaxb.model.record.summary_rc4.ActivitiesSummary;
import org.orcid.jaxb.model.record_rc4.ActivitiesContainer;
import org.orcid.jaxb.model.record_rc4.Addresses;
import org.orcid.jaxb.model.record_rc4.Biography;
import org.orcid.jaxb.model.record_rc4.Emails;
import org.orcid.jaxb.model.record_rc4.GroupsContainer;
import org.orcid.jaxb.model.record_rc4.Keywords;
import org.orcid.jaxb.model.record_rc4.OtherNames;
import org.orcid.jaxb.model.record_rc4.Person;
import org.orcid.jaxb.model.record_rc4.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_rc4.PersonalDetails;
import org.orcid.jaxb.model.record_rc4.Record;
import org.orcid.jaxb.model.record_rc4.ResearcherUrls;

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
}
