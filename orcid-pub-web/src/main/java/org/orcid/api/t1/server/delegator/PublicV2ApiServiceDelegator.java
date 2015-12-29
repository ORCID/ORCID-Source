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
    
    Response viewWork(String orcid, Long putCode);
    
    Response viewWorkSummary(String orcid, Long putCode);
    
    Response viewFunding(String orcid, Long putCode);
    
    Response viewFundingSummary(String orcid, Long putCode);
    
    Response viewEducation(String orcid, Long putCode);
    
    Response viewEducationSummary(String orcid, Long putCode);
    
    Response viewEmployment(String orcid, Long putCode);
    
    Response viewEmploymentSummary(String orcid, Long putCode);    
    
    Response viewPeerReview(String orcid, Long putCode);
    
    Response viewPeerReviewSummary(String orcid, Long putCode);

    Response viewWorkCitation(String orcid, Long putCode);  
    
    Response viewResearcherUrl(String orcid, Long putCode);
    
    Response viewResearcherUrls(String orcid);
    
    Response viewEmails(String orcid);
            
    Response viewPersonalDetails(String orcid);
    
    Response viewOtherNames(String orcid);
    
    Response viewOtherName(String orcid, Long putCode);
    
    Response viewExternalIdentifiers(String orcid);

    Response viewExternalIdentifier(String orcid, Long putCode);
}
