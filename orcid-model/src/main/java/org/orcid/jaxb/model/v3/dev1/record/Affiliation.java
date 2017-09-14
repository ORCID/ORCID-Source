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
package org.orcid.jaxb.model.v3.dev1.record;

import org.orcid.jaxb.model.v3.dev1.common.CreatedDate;
import org.orcid.jaxb.model.v3.dev1.common.FuzzyDate;
import org.orcid.jaxb.model.v3.dev1.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.dev1.common.Organization;
import org.orcid.jaxb.model.v3.dev1.common.Source;
import org.orcid.jaxb.model.v3.dev1.common.Url;
import org.orcid.jaxb.model.v3.dev1.common.Visibility;

public interface Affiliation {
    String getDepartmentName();

    void setDepartmentName(String value);

    String getRoleTitle();

    void setRoleTitle(String value);

    FuzzyDate getStartDate();

    void setStartDate(FuzzyDate value);

    FuzzyDate getEndDate();

    void setEndDate(FuzzyDate value);

    Organization getOrganization();

    void setOrganization(Organization value);

    Source getSource();

    void setSource(Source value);

    Visibility getVisibility();

    void setVisibility(Visibility value);

    Long getPutCode();

    void setPutCode(Long value);

    CreatedDate getCreatedDate();

    void setCreatedDate(CreatedDate value);

    LastModifiedDate getLastModifiedDate();

    void setLastModifiedDate(LastModifiedDate value);
    
    Url getUrl();
    
    void setUrl(Url url);
}