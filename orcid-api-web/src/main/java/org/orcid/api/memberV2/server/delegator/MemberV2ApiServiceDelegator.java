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
package org.orcid.api.memberV2.server.delegator;

import javax.ws.rs.core.Response;

import org.orcid.jaxb.model.record.Education;
import org.orcid.jaxb.model.record.Employment;
import org.orcid.jaxb.model.record.Funding;
import org.orcid.jaxb.model.record.Work;

/**
 * 
 * @author Will Simpson
 *
 */
public interface MemberV2ApiServiceDelegator {

    Response viewStatusText();

    Response viewActivities(String orcid);
    
    Response viewWork(String orcid, String putCode);
    
    Response viewWorkSummary(String orcid, String putCode);
    
    Response createWork(String orcid, Work work);
    
    Response updateWork(String orcid, String putCode, Work work);
    
    Response deleteWork(String orcid, String putCode);

    Response viewFunding(String orcid, String putCode);
    
    Response viewFundingSummary(String orcid, String putCode);
    
    Response createFunding(String orcid, Funding funding);
    
    Response updateFunding(String orcid, String putCode, Funding funding);
    
    Response deleteFunding(String orcid, String putCode);
    
    Response viewEducation(String orcid, String putCode);
    
    Response viewEducationSummary(String orcid, String putCode);
    
    Response createEducation(String orcid, Education education);
    
    Response updateEducation(String orcid, String putCode, Education education);
    
    Response viewEmployment(String orcid, String putCode);
    
    Response viewEmploymentSummary(String orcid, String putCode);
    
    Response createEmployment(String orcid, Employment employment);
    
    Response updateEmployment(String orcid, String putCode, Employment employment);
    
    Response deleteAffiliation(String orcid, String putCode);
}
