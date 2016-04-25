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
package org.orcid.api.common.util;

import static org.orcid.core.api.OrcidApiConstants.EXTERNAL_IDENTIFIERS;
import static org.orcid.core.api.OrcidApiConstants.PERSONAL_DETAILS;
import static org.orcid.core.api.OrcidApiConstants.RESEARCHER_URLS;
import static org.orcid.core.api.OrcidApiConstants.BIOGRAPHY;
import static org.orcid.core.api.OrcidApiConstants.OTHER_NAMES;
import static org.orcid.core.api.OrcidApiConstants.KEYWORDS;
import static org.orcid.core.api.OrcidApiConstants.ADDRESS;
import static org.orcid.core.api.OrcidApiConstants.PERSON;
import static org.orcid.core.api.OrcidApiConstants.EMAIL;

import org.orcid.jaxb.model.record_rc2.Address;
import org.orcid.jaxb.model.record_rc2.Addresses;
import org.orcid.jaxb.model.record_rc2.Biography;
import org.orcid.jaxb.model.record_rc2.Emails;
import org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_rc2.Keyword;
import org.orcid.jaxb.model.record_rc2.Keywords;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.OtherNames;
import org.orcid.jaxb.model.record_rc2.Person;
import org.orcid.jaxb.model.record_rc2.PersonalDetails;
import org.orcid.jaxb.model.record_rc2.ResearcherUrl;
import org.orcid.jaxb.model.record_rc2.ResearcherUrls;

public class ElementUtils {

    public static void setPathToResearcherUrls(ResearcherUrls researcherUrls, String orcid) {
        if(researcherUrls != null) {
            researcherUrls.setPath(RESEARCHER_URLS.replace("{orcid}", orcid));
        }        
    }

    public static void setPathToResearcherUrl(ResearcherUrl researcherUrl, String orcid) {
        if(researcherUrl != null) {
            researcherUrl.setPath(RESEARCHER_URLS.replace("{orcid}", orcid) + '/' + researcherUrl.getPutCode());
        }        
    }

    public static void setPathToPersonalDetails(PersonalDetails personalDetails, String orcid) {
        if(personalDetails != null) {
            personalDetails.setPath(PERSONAL_DETAILS.replace("{orcid}", orcid));
        }        
    }

    public static void setPathToExternalIdentifiers(PersonExternalIdentifiers extIds, String orcid) {
        if(extIds != null) {
            extIds.setPath(EXTERNAL_IDENTIFIERS.replace("{orcid}", orcid));
        }        
    }

    public static void setPathToExternalIdentifier(PersonExternalIdentifier extId, String orcid) {
        if(extId != null) {
            extId.setPath(EXTERNAL_IDENTIFIERS.replace("{orcid}", orcid) + '/' + extId.getPutCode());
        }        
    }

    public static void setPathToBiography(Biography bio, String orcid) {
        if(bio != null) {
            bio.setPath(BIOGRAPHY.replace("{orcid}", orcid));
        }        
    }

    public static void setPathToOtherNames(OtherNames otherNames, String orcid) {
        if(otherNames != null) {
            otherNames.setPath(OTHER_NAMES.replace("{orcid}", orcid));
        }        
    }

    public static void setPathToOtherName(OtherName otherName, String orcid) {
        if(otherName != null) {
            otherName.setPath(OTHER_NAMES.replace("{orcid}", orcid) + '/' + otherName.getPutCode());
        }        
    }

    public static void setPathToKeywords(Keywords keywords, String orcid) {
        if(keywords != null) {
            keywords.setPath(KEYWORDS.replace("{orcid}", orcid));
        }        
    }

    public static void setPathToKeyword(Keyword keyword, String orcid) {
        if(keyword != null) {
            keyword.setPath(KEYWORDS.replace("{orcid}", orcid) + '/' + keyword.getPutCode());
        }        
    }
    
    public static void setPathToAddresses(Addresses addresses, String orcid) {
        if(addresses != null) {
            addresses.setPath(ADDRESS.replace("{orcid}", orcid));
        }        
    }
    
    public static void setPathToAddress(Address address, String orcid) {
        if(address != null) {
            address.setPath(ADDRESS.replace("{orcid}", orcid) + '/' + address.getPutCode());
        }        
    }
    
    public static void setPathToPerson(Person person, String orcid) {
        if(person != null) {
            person.setPath(PERSON.replace("{orcid}", orcid) );
        }
    }
    
    public static void setPathToEmail(Emails emails, String orcid) {
        if(emails != null) {
            emails.setPath(EMAIL.replace("{orcid}", orcid) );
        }        
    }
}
