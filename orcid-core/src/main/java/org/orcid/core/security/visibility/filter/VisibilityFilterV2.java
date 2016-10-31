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
package org.orcid.core.security.visibility.filter;

import java.util.Collection;

import org.orcid.jaxb.model.common_rc3.Filterable;
import org.orcid.jaxb.model.record.summary_rc3.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc3.Fundings;
import org.orcid.jaxb.model.record.summary_rc3.PeerReviews;
import org.orcid.jaxb.model.record.summary_rc3.Works;
import org.orcid.jaxb.model.record_rc3.Group;
import org.orcid.jaxb.model.record_rc3.Person;
import org.orcid.jaxb.model.record_rc3.PersonalDetails;
import org.orcid.jaxb.model.record_rc3.Record;

/**
 * @author Will Simpson
 */
public interface VisibilityFilterV2 {

    ActivitiesSummary filter(ActivitiesSummary activitiesSummary, String orcid);
    
    Works filter(Works works, String orcid);
    
    Fundings filter(Fundings fundings, String orcid);
    
    PeerReviews filter(PeerReviews peerReviews, String orcid);
    
    Collection<? extends Filterable> filter(Collection<? extends Filterable> filterables, String orcid);
        
    Collection<? extends Group> filterGroups(Collection<? extends Group> groups, String orcid);
    
    PersonalDetails filter(PersonalDetails personalDetails, String orcid);
    
    Person filter(Person person, String orcid);

    Record filter(Record record, String orcid);
}
