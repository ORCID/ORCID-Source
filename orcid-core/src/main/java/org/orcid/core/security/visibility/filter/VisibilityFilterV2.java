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

import org.orcid.jaxb.model.common.Filterable;
import org.orcid.jaxb.model.record.summary_rc1.ActivitiesSummary;
import org.orcid.jaxb.model.record_rc1.Group;
import org.orcid.jaxb.model.record_rc2.Person;
import org.orcid.jaxb.model.record_rc2.PersonalDetails;

/**
 * @author Will Simpson
 */
public interface VisibilityFilterV2 {

    ActivitiesSummary filter(ActivitiesSummary activitiesSummary);
    
    Collection<? extends Filterable> filter(Collection<? extends Filterable> filterables);
        
    Collection<? extends Group> filterGroups(Collection<? extends Group> groups);
    
    PersonalDetails filter(PersonalDetails personalDetails);
    
    Person filter(Person person);

}
