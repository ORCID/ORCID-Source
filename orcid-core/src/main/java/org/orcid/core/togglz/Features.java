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
package org.orcid.core.togglz;

import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum Features implements Feature {
    
    @Label("Affiliation search")
    AFFILIATION_SEARCH,
    
    @Label("Last modified")
    LAST_MOD,
    
    @Label("New footer")
    NEW_FOOTER,
    
    @Label("Split oauth in 2 screens")
    OAUTH_2SCREENS,
    
    @Label("Mutiple emails on register form")
    REG_MULTI_EMAIL,
    
    @Label("Revoke access token if authorization code is reused")
    REVOKE_TOKEN_ON_CODE_REUSE,

    @Label("Survey link")
    SURVEY,
    
    @Label("Two factor authentication")
    TWO_FACTOR_AUTHENTICATION,
    
    @Label("API analytics debug logging")
    API_ANALYTICS_DEBUG;
    
    public boolean isActive() {
        return FeatureContext.getFeatureManager().isActive(this);
    }
}
