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

import static org.orcid.core.api.OrcidApiConstants.ADDRESS;
import static org.orcid.core.api.OrcidApiConstants.BIOGRAPHY;
import static org.orcid.core.api.OrcidApiConstants.EMAIL;
import static org.orcid.core.api.OrcidApiConstants.EXTERNAL_IDENTIFIERS;
import static org.orcid.core.api.OrcidApiConstants.KEYWORDS;
import static org.orcid.core.api.OrcidApiConstants.OTHER_NAMES;
import static org.orcid.core.api.OrcidApiConstants.PERSON;
import static org.orcid.core.api.OrcidApiConstants.PERSONAL_DETAILS;
import static org.orcid.core.api.OrcidApiConstants.RESEARCHER_URLS;

import org.orcid.jaxb.model.record_rc2.Address;
import org.orcid.jaxb.model.record_rc2.Addresses;
import org.orcid.jaxb.model.record_rc2.Biography;
import org.orcid.jaxb.model.record_rc2.Emails;
import org.orcid.jaxb.model.record_rc2.Keyword;
import org.orcid.jaxb.model.record_rc2.Keywords;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.OtherNames;
import org.orcid.jaxb.model.record_rc2.Person;
import org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_rc2.PersonalDetails;
import org.orcid.jaxb.model.record_rc2.ResearcherUrl;
import org.orcid.jaxb.model.record_rc2.ResearcherUrls;

public class ElementUtils {

    public static void setPathToResearcherUrls(ResearcherUrls researcherUrls, String orcid) {
        researcherUrls.setPath(RESEARCHER_URLS.replace("{orcid}", orcid));
        if(researcherUrls.getResearcherUrls() != null && !researcherUrls.getResearcherUrls().isEmpty()) {
            for(ResearcherUrl rUrl : researcherUrls.getResearcherUrls()) {
                setPathToResearcherUrl(rUrl, orcid);
            }
        }
    }

    public static void setPathToResearcherUrl(ResearcherUrl researcherUrl, String orcid) {
        researcherUrl.setPath(RESEARCHER_URLS.replace("{orcid}", orcid) + '/' + researcherUrl.getPutCode());
    }

    public static void setPathToExternalIdentifiers(PersonExternalIdentifiers extIds, String orcid) {
        extIds.setPath(EXTERNAL_IDENTIFIERS.replace("{orcid}", orcid));
        if(extIds.getExternalIdentifier() != null && !extIds.getExternalIdentifier().isEmpty()) {
            for(PersonExternalIdentifier extId : extIds.getExternalIdentifier()) {
                setPathToExternalIdentifier(extId, orcid);
            }
        }
    }

    public static void setPathToExternalIdentifier(PersonExternalIdentifier extId, String orcid) {
        extId.setPath(EXTERNAL_IDENTIFIERS.replace("{orcid}", orcid) + '/' + extId.getPutCode());
    }

    public static void setPathToBiography(Biography bio, String orcid) {
        bio.setPath(BIOGRAPHY.replace("{orcid}", orcid));
    }

    public static void setPathToOtherNames(OtherNames otherNames, String orcid) {
        otherNames.setPath(OTHER_NAMES.replace("{orcid}", orcid));
        if(otherNames.getOtherNames() != null && !otherNames.getOtherNames().isEmpty()) {
            for(OtherName otherName : otherNames.getOtherNames()) {
                setPathToOtherName(otherName, orcid);
            }
        }
    }

    public static void setPathToOtherName(OtherName otherName, String orcid) {
        otherName.setPath(OTHER_NAMES.replace("{orcid}", orcid) + '/' + otherName.getPutCode());
    }

    public static void setPathToKeywords(Keywords keywords, String orcid) {
        keywords.setPath(KEYWORDS.replace("{orcid}", orcid));
        if(keywords.getKeywords() != null && !keywords.getKeywords().isEmpty()) {
            for(Keyword keyword : keywords.getKeywords()) {
                setPathToKeyword(keyword, orcid);
            }
        }
    }

    public static void setPathToKeyword(Keyword keyword, String orcid) {
        keyword.setPath(KEYWORDS.replace("{orcid}", orcid) + '/' + keyword.getPutCode());
    }
    
    public static void setPathToAddresses(Addresses addresses, String orcid) {
        addresses.setPath(ADDRESS.replace("{orcid}", orcid));
        if(addresses.getAddress() != null && !addresses.getAddress().isEmpty()) {
            for(Address address : addresses.getAddress()) {
                setPathToAddress(address, orcid);
            }
        }
    }
    
    public static void setPathToAddress(Address address, String orcid) {
        address.setPath(ADDRESS.replace("{orcid}", orcid) + '/' + address.getPutCode() );
    }
    
    public static void setPathToEmail(Emails emails, String orcid) {
        emails.setPath(EMAIL.replace("{orcid}", orcid) );
    }
    
    public static void setPathToPersonalDetails(PersonalDetails personalDetails, String orcid) {
        personalDetails.setPath(PERSONAL_DETAILS.replace("{orcid}", orcid));
        if(personalDetails.getBiography() != null) {
            setPathToBiography(personalDetails.getBiography(), orcid);
        }
        
        if(personalDetails.getOtherNames() != null) {
            setPathToOtherNames(personalDetails.getOtherNames(), orcid);
        }
    }
    
    public static void setPathToPerson(Person person, String orcid) {
        person.setPath(PERSON.replace("{orcid}", orcid) );
        if(person.getAddresses() != null) {
            setPathToAddresses(person.getAddresses(), orcid);
        }
        
        if(person.getBiography() != null) {
            setPathToBiography(person.getBiography(), orcid);
        }
        
        if(person.getEmails() != null) {
            setPathToEmail(person.getEmails(), orcid);
        }
        
        if(person.getExternalIdentifiers() != null) {
            setPathToExternalIdentifiers(person.getExternalIdentifiers(), orcid);
        }
        
        if(person.getKeywords() != null) {
            setPathToKeywords(person.getKeywords(), orcid);
        }
        
        if(person.getOtherNames() != null) {
            setPathToOtherNames(person.getOtherNames(), orcid);
        }
        
        if(person.getResearcherUrls() != null) {
            setPathToResearcherUrls(person.getResearcherUrls(), orcid);
        }
    }
}
