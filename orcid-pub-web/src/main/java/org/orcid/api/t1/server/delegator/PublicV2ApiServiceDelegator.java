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
package org.orcid.api.t1.server.delegator;

import javax.ws.rs.core.Response;

public interface PublicV2ApiServiceDelegator {
    
    Response viewStatusText();
    
    Response viewActivities(String orcid);
    
    Response viewWork(String orcid, String putCode);
    
    Response viewWorkSummary(String orcid, String putCode);
    
    Response viewFunding(String orcid, String putCode);
    
    Response viewFundingSummary(String orcid, String putCode);
    
    Response viewEducation(String orcid, String putCode);
    
    Response viewEducationSummary(String orcid, String putCode);
    
    Response viewEmployment(String orcid, String putCode);
    
    Response viewEmploymentSummary(String orcid, String putCode);    
}
