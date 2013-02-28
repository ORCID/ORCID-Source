/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.api.common.validation;

import java.util.List;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.orcid.api.common.exception.OrcidBadRequestException;
import org.orcid.jaxb.model.message.Citation;
import org.orcid.jaxb.model.message.CitationType;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.utils.BibtexException;
import org.orcid.utils.BibtexUtils;
import org.springframework.stereotype.Component;

/**
 * 2011-2012 - Semantico Ltd.
 * 
 * @author Declan Newman (declan) Date: 08/10/2012
 */

@Aspect
@Component
public class OrcidMessageValidator {

    // This should have its own validator at some point, but this is trivial at
    // the moment
    @Before("@annotation(validOrcidMessage) && (args(..,orcidMessage))")
    public void validateIncomingOrcidMessage(ValidOrcidMessage validOrcidMessage, OrcidMessage orcidMessage) {
        validate(orcidMessage);
    }

    private void validate(OrcidMessage orcidMessage) {
        OrcidProfile orcidProfile = orcidMessage != null ? orcidMessage.getOrcidProfile() : null;
        if (orcidProfile == null) {
            throw new OrcidBadRequestException("There must be an orcid-profile element");
        }
        checkBio(orcidProfile.getOrcidBio());
        checkActivities(orcidProfile.getOrcidActivities());
    }

    private void checkBio(OrcidBio orcidBio) {
        if (orcidBio != null) {
            checkContactDetails(orcidBio.getContactDetails());
        }
    }

    private void checkContactDetails(ContactDetails contactDetails) {
        if (contactDetails != null) {
            List<Email> emailList = contactDetails.getEmail();
            int primaryCount = 0;
            for (Email email : emailList) {
                if (email.isPrimary()) {
                    primaryCount++;
                }
            }
            if (primaryCount > 1) {
                throw new OrcidBadRequestException("There must not be more than one primary email");
            }
        }
    }

    private void checkActivities(OrcidActivities orcidActivities) {
        if (orcidActivities != null) {
            OrcidWorks works = orcidActivities.getOrcidWorks();
            if (works != null && works.getOrcidWork() != null && !works.getOrcidWork().isEmpty()) {
                checkWorks(works.getOrcidWork());
            }
        }
    }

    private void checkWorks(List<OrcidWork> orcidWork) {
        for (OrcidWork work : orcidWork) {
            checkWork(work);
        }
    }

    public void checkWork(OrcidWork orcidWork) {
        if (orcidWork.getWorkCitation() != null) {
            Citation workCitation = orcidWork.getWorkCitation();
            if (CitationType.BIBTEX.equals(workCitation.getWorkCitationType())) {
                try {
                    BibtexUtils.validate(workCitation.getCitation());
                } catch (BibtexException e) {
                    throw new OrcidBadRequestException("Invalid BibTeX citation: " + workCitation.getCitation() + "\n", e);
                }
            }
        }
    }

}
