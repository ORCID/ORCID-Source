package org.orcid.api.common.util.v3;

import static org.orcid.core.api.OrcidApiConstants.ADDRESS;
import static org.orcid.core.api.OrcidApiConstants.BIOGRAPHY;
import static org.orcid.core.api.OrcidApiConstants.EMAIL;
import static org.orcid.core.api.OrcidApiConstants.EXTERNAL_IDENTIFIERS;
import static org.orcid.core.api.OrcidApiConstants.KEYWORDS;
import static org.orcid.core.api.OrcidApiConstants.OTHER_NAMES;
import static org.orcid.core.api.OrcidApiConstants.PERSON;
import static org.orcid.core.api.OrcidApiConstants.PERSONAL_DETAILS;
import static org.orcid.core.api.OrcidApiConstants.RESEARCHER_URLS;

import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.Addresses;
import org.orcid.jaxb.model.v3.release.record.Biography;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.Keywords;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.OtherNames;
import org.orcid.jaxb.model.v3.release.record.Person;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.release.record.PersonalDetails;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;

public class ElementUtils {

    public static void setPathToResearcherUrls(ResearcherUrls researcherUrls, String orcid) {
        if (researcherUrls != null) {
            researcherUrls.setPath(RESEARCHER_URLS.replace("{orcid}", orcid));
            if (researcherUrls.getResearcherUrls() != null && !researcherUrls.getResearcherUrls().isEmpty()) {
                for (ResearcherUrl rUrl : researcherUrls.getResearcherUrls()) {
                    setPathToResearcherUrl(rUrl, orcid);
                }
            }
        }
    }

    public static void setPathToResearcherUrl(ResearcherUrl researcherUrl, String orcid) {
        if(researcherUrl != null) {
            researcherUrl.setPath(RESEARCHER_URLS.replace("{orcid}", orcid) + '/' + researcherUrl.getPutCode());
        }        
    }
    
    public static void setPathToExternalIdentifiers(PersonExternalIdentifiers extIds, String orcid) {
        if (extIds != null) {
            extIds.setPath(EXTERNAL_IDENTIFIERS.replace("{orcid}", orcid));
            if (extIds.getExternalIdentifiers() != null && !extIds.getExternalIdentifiers().isEmpty()) {
                for (PersonExternalIdentifier extId : extIds.getExternalIdentifiers()) {
                    setPathToExternalIdentifier(extId, orcid);
                }
            }
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
        if (otherNames != null) {
            otherNames.setPath(OTHER_NAMES.replace("{orcid}", orcid));
            if (otherNames.getOtherNames() != null && !otherNames.getOtherNames().isEmpty()) {
                for (OtherName otherName : otherNames.getOtherNames()) {
                    setPathToOtherName(otherName, orcid);
                }
            }
        }
    }

    public static void setPathToOtherName(OtherName otherName, String orcid) {
        if(otherName != null) {
            otherName.setPath(OTHER_NAMES.replace("{orcid}", orcid) + '/' + otherName.getPutCode());
        }        
    }

    public static void setPathToKeywords(Keywords keywords, String orcid) {
        if (keywords != null) {
            keywords.setPath(KEYWORDS.replace("{orcid}", orcid));
            if (keywords.getKeywords() != null && !keywords.getKeywords().isEmpty()) {
                for (Keyword keyword : keywords.getKeywords()) {
                    setPathToKeyword(keyword, orcid);
                }
            }
        }
    }

    public static void setPathToKeyword(Keyword keyword, String orcid) {
        if(keyword != null) {
            keyword.setPath(KEYWORDS.replace("{orcid}", orcid) + '/' + keyword.getPutCode());
        }        
    }
    
    public static void setPathToAddresses(Addresses addresses, String orcid) {
        if (addresses != null) {
            addresses.setPath(ADDRESS.replace("{orcid}", orcid));
            if (addresses.getAddress() != null && !addresses.getAddress().isEmpty()) {
                for (Address address : addresses.getAddress()) {
                    setPathToAddress(address, orcid);
                }
            }
        }
    }
    
    public static void setPathToAddress(Address address, String orcid) {
        if(address != null) {
            address.setPath(ADDRESS.replace("{orcid}", orcid) + '/' + address.getPutCode());
        }        
    }
    
    public static void setPathToEmail(Emails emails, String orcid) {
        if(emails != null) {
            emails.setPath(EMAIL.replace("{orcid}", orcid) );
        }        
    }
    
    public static void setPathToPersonalDetails(PersonalDetails personalDetails, String orcid) {
        if(personalDetails != null) {
            personalDetails.setPath(PERSONAL_DETAILS.replace("{orcid}", orcid));
            if(personalDetails.getBiography() != null) {
                setPathToBiography(personalDetails.getBiography(), orcid);
            }
            
            if(personalDetails.getOtherNames() != null) {
                setPathToOtherNames(personalDetails.getOtherNames(), orcid);
            }
        }
    }
    
    public static void setPathToPerson(Person person, String orcid) {
        if (person != null) {
            person.setPath(PERSON.replace("{orcid}", orcid));
            if (person.getAddresses() != null) {
                setPathToAddresses(person.getAddresses(), orcid);
            }

            if (person.getBiography() != null) {
                setPathToBiography(person.getBiography(), orcid);
            }

            if (person.getEmails() != null) {
                setPathToEmail(person.getEmails(), orcid);
            }

            if (person.getExternalIdentifiers() != null) {
                setPathToExternalIdentifiers(person.getExternalIdentifiers(), orcid);
            }

            if (person.getKeywords() != null) {
                setPathToKeywords(person.getKeywords(), orcid);
            }

            if (person.getOtherNames() != null) {
                setPathToOtherNames(person.getOtherNames(), orcid);
            }

            if (person.getResearcherUrls() != null) {
                setPathToResearcherUrls(person.getResearcherUrls(), orcid);
            }
        }
    }

    public static void setPathToRecord(Record record, String orcid) {
        if (record != null) {
            record.setPath("/" + orcid);
            if (record.getPerson() != null) {
                setPathToPerson(record.getPerson(), orcid);
            }

            if (record.getActivitiesSummary() != null) {
                ActivityUtils.setPathToActivity(record.getActivitiesSummary(), orcid);
            }
        }
    }
    
}
