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
package org.orcid.core.manager.validator;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.exception.ActivityIdentifierValidationException;
import org.orcid.core.exception.ActivityTitleValidationException;
import org.orcid.core.exception.InvalidPutCodeException;
import org.orcid.jaxb.model.record.Education;
import org.orcid.jaxb.model.record.Employment;
import org.orcid.jaxb.model.record.Funding;
import org.orcid.jaxb.model.record.FundingTitle;
import org.orcid.jaxb.model.record.Work;
import org.orcid.jaxb.model.record.WorkTitle;

public class ActivityValidator {

    public static void validateWork(Work work) {
        WorkTitle title = work.getWorkTitle();
        if (title == null || title.getTitle() == null || StringUtils.isEmpty(title.getTitle().getContent())) {
            throw new ActivityTitleValidationException();
        }

        if (work.getWorkExternalIdentifiers() == null || work.getWorkExternalIdentifiers().getWorkExternalIdentifier() == null
                || work.getWorkExternalIdentifiers().getWorkExternalIdentifier().isEmpty()) {
                throw new ActivityIdentifierValidationException();
        }
        
        if (work.getPutCode() != null) {
                throw new InvalidPutCodeException();
        }
    }
    
    public static void validateFunding(Funding funding) {
    	FundingTitle title = funding.getTitle();
        if (title == null || title.getTitle() == null || StringUtils.isEmpty(title.getTitle().getContent())) {
            throw new ActivityTitleValidationException();
        }

        if (funding.getExternalIdentifiers() == null || funding.getExternalIdentifiers().getExternalIdentifier() == null
                || funding.getExternalIdentifiers().getExternalIdentifier().isEmpty()) {
                throw new ActivityIdentifierValidationException();
        }
        
        if (funding.getPutCode() != null) {
                throw new InvalidPutCodeException();
        }
    }
    
    public static void validateEmployment(Employment employment) {
        if (employment.getPutCode() != null) {
                throw new InvalidPutCodeException();
        }
    }
    
    public static void validateEducation(Education education) {
        if (education.getPutCode() != null) {
                throw new InvalidPutCodeException();
        }
    }
}
