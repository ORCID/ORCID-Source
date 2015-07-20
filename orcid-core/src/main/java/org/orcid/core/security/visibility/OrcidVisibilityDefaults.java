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
package org.orcid.core.security.visibility;

import org.orcid.jaxb.model.message.Visibility;

/**
 * @author Declan Newman (declan) Date: 13/03/2012
 */
public enum OrcidVisibilityDefaults {

    AFFILIATE_DETAIL_DEFAULT(Visibility.PUBLIC), AFFILIATE_NAME_DEFAULT(Visibility.PUBLIC), FUNDING_DEFAULT(Visibility.PUBLIC), ALTERNATIVE_EMAIL_DEFAULT(
            Visibility.LIMITED), CREDIT_NAME_DEFAULT(Visibility.PUBLIC), COUNTRY_DEFAULT(Visibility.PUBLIC), DELEGATION_DEFAULT(Visibility.PRIVATE), PRIMARY_EMAIL_DEFAULT(
            Visibility.LIMITED), EXTERNAL_IDENTIFIER_DEFAULT(Visibility.PUBLIC), KEYWORD_DEFAULT(Visibility.PUBLIC), OTHER_NAMES_DEFAULT(Visibility.PUBLIC), PAST_INSTITUTION_DEFAULT(
            Visibility.PUBLIC), PERSONAL_DETAILS_DEFAULT(Visibility.LIMITED), PRIMARY_INSTITUTION_NAME_DEFAULT(Visibility.PUBLIC), PRIMARY_INSTITUTION_DETAIL_DEFAULT(
            Visibility.PUBLIC), RESEARCHER_URLS_DEFAULT(Visibility.PUBLIC), SHORT_DESCRIPTION_DEFAULT(Visibility.PUBLIC), SUBJECTS_DEFAULT(Visibility.PUBLIC), WORKS_DEFAULT(
            Visibility.PUBLIC), WORKS_COUNTRY_DEFAULT(Visibility.PUBLIC), BIOGRAPHY_DEFAULT(Visibility.PUBLIC), CREATED_BY_MEMBER_DEFAULT(Visibility.PRIVATE), ACTIVITIES_DEFAULT(
            Visibility.PUBLIC), WORKS_CONTRIBUTOR_VISIBILITY_DEFAULT(Visibility.PRIVATE);

    private Visibility visibility;

    OrcidVisibilityDefaults(Visibility visibility) {
        this.visibility = visibility;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    @Override
    public String toString() {
        return visibility.toString();
    }
}
