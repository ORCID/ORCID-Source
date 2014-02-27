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
package org.orcid.core.manager.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.OrcidJaxbCopyManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.message.Address;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.Biography;
import org.orcid.jaxb.model.message.Claimed;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Contributor;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingList;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkContributors;
import org.springframework.util.Assert;

/**
 * Simple class containing static methods to copy values from updated profiles
 * to the existing one, whilst preserving or setting the visibility. If neither
 * the updated nor existing property contains a value for visbility a default
 * will be set using the {@link OrcidVisibilityDefaults}.
 * <p/>
 * It is important to note that the it is intentional that the values are copied
 * from the updated to the existing, in order to preserve values that would
 * otherwise be overwritten. Some of the values can only perform 'best shot' at
 * matching them, in which case it will use the defaults if not able to match
 * <p/>
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 13/03/2012
 */

public class OrcidJaxbCopyManagerImpl implements OrcidJaxbCopyManager {

    @Resource
    private SourceManager sourceManager;

    @Override
    public void copyRelevantUpdatedHistoryElements(OrcidHistory existing, OrcidHistory updated) {
        Assert.notNull(updated, "The updated history is null");
        Assert.notNull(existing, "The existing history is null");

        if (updated.isClaimed() && !existing.isClaimed()) {
            existing.setClaimed(new Claimed(true));
        }

        if (existing.getCompletionDate() == null && updated.getCompletionDate() != null) {
            existing.setCompletionDate(updated.getCompletionDate());
        }
        // TODO: There may be some others that need to be added to this.
    }

    @Override
    public void copyUpdatedBioToExistingWithVisibility(OrcidBio existing, OrcidBio updated) {
        Assert.notNull(updated, "The updated bio is null");
        Assert.notNull(existing, "The existing bio is null");

        copyUpdatedPersonalDetailsToExistingPreservingVisibility(existing, updated);
        copyUpdatedResearcherUrlPreservingVisbility(existing, updated);
        copyUpdatedContactDetailsToExistingPreservingVisibility(existing, updated);
        copyUpdatedKeywordsToExistingPreservingVisibility(existing, updated);
        copyUpdatedShortDescriptionToExistingPreservingVisibility(existing, updated);
        copyUpdatedExternalIdentifiersToExistingPreservingVisibility(existing, updated);
    }

    @Override
    public void copyAffiliationsToExistingPreservingVisibility(Affiliations existingAffiliations, Affiliations updatedAffiliations) {
        if (updatedAffiliations == null) {
            return;
        }
        if (existingAffiliations == null) {
            existingAffiliations = new Affiliations();
        }

        Map<String, Affiliation> updatedAffiliationsMap = updatedAffiliations.retrieveAffiliationAsMap();

        for (Iterator<Affiliation> existingAffiliationIterator = existingAffiliations.getAffiliation().iterator(); existingAffiliationIterator.hasNext();) {
            Affiliation existingAffiliation = existingAffiliationIterator.next();
            Affiliation updatedAffiliation = updatedAffiliationsMap.get(existingAffiliation.getPutCode());
            if (updatedAffiliation == null) {
                if (!(Visibility.PRIVATE.equals(existingAffiliation.getVisibility()) || isFromDifferentSource(existingAffiliation))) {
                    // Remove existing affiliations unless they are private (we
                    // need to keep those because the API user won't even know
                    // they are there) or they are from another source
                    existingAffiliationIterator.remove();
                }

            } else {
                // Check the source of the existing affiliation is the same as
                // the current source
                checkSource(existingAffiliation);
                if (updatedAffiliation.getVisibility() == null) {
                    // Keep the visibility from the existing affiliation unless
                    // was set by API user
                    updatedAffiliation.setVisibility(existingAffiliation.getVisibility());
                }
                // Can remove existing object because will be replaced by
                // incoming
                existingAffiliationIterator.remove();
            }
        }
        for (Affiliation updatedAffiliation : updatedAffiliations.getAffiliation()) {
            // Set default visibility for any remaining incoming affiliations
            if (updatedAffiliation.getVisibility() == null) {
                updatedAffiliation.setVisibility(OrcidVisibilityDefaults.AFFILIATE_NAME_DEFAULT.getVisibility());
            }
        }
        existingAffiliations.getAffiliation().addAll(updatedAffiliations.getAffiliation());
    }

    @Override
    public void copyFundingListToExistingPreservingVisibility(FundingList existingFundings, FundingList updatedFundings) {
        if (updatedFundings == null) {
            return;
        }
        List<Funding> updatedFundingList = updatedFundings.getFundings();
        if (updatedFundingList.isEmpty()) {
            return;
        }
        List<Funding> existingFundingsList = existingFundings.getFundings();
        for (Funding updatedFunding : updatedFundingList) {
            mergeFundings(existingFundingsList, updatedFunding);
        }
        existingFundingsList.clear();
        existingFundingsList.addAll(updatedFundingList);
    }

    @Override
    public void copyUpdatedExternalIdentifiersToExistingPreservingVisibility(OrcidBio existing, OrcidBio updated) {
        if (updated.getExternalIdentifiers() == null) {
            return;
        }
        ExternalIdentifiers existingExternalIdentifiers = existing.getExternalIdentifiers();
        ExternalIdentifiers updatedExternalIdentifiers = updated.getExternalIdentifiers();

        Visibility existingExternalIdentifiersVisibility = existingExternalIdentifiers != null ? existingExternalIdentifiers.getVisibility() : null;
        Visibility updatedExternalIdentifiersVisibility = updatedExternalIdentifiers.getVisibility();

        if (updatedExternalIdentifiersVisibility == null && existingExternalIdentifiersVisibility == null) {
            updatedExternalIdentifiers.setVisibility(OrcidVisibilityDefaults.EXTERNAL_IDENTIFIER_DEFAULT.getVisibility());
        } else if (updatedExternalIdentifiersVisibility == null && existingExternalIdentifiersVisibility != null) {
            updatedExternalIdentifiers.setVisibility(existingExternalIdentifiersVisibility);
        }
        existing.setExternalIdentifiers(updatedExternalIdentifiers);
    }

    @Override
    public void copyUpdatedShortDescriptionToExistingPreservingVisibility(OrcidBio existing, OrcidBio updated) {
        if (updated.getBiography() == null) {
            return;
        }
        Biography existingShortDescription = existing.getBiography();
        Biography updatedShortDescription = updated.getBiography();

        Visibility existingShortDescriptionVisibility = existingShortDescription != null ? existingShortDescription.getVisibility()
                : OrcidVisibilityDefaults.SHORT_DESCRIPTION_DEFAULT.getVisibility();
        Visibility updatedShortDescriptionVisibility = updatedShortDescription != null ? updatedShortDescription.getVisibility() : existingShortDescriptionVisibility;

        if (updatedShortDescriptionVisibility == null && existingShortDescriptionVisibility == null) {
            updatedShortDescription.setVisibility(OrcidVisibilityDefaults.SHORT_DESCRIPTION_DEFAULT.getVisibility());
        } else if (updatedShortDescriptionVisibility == null && existingShortDescriptionVisibility != null) {
            updatedShortDescription.setVisibility(existingShortDescriptionVisibility);
        }
        existing.setBiography(updatedShortDescription);
    }

    @Override
    public void copyUpdatedKeywordsToExistingPreservingVisibility(OrcidBio existing, OrcidBio updated) {
        if (updated.getKeywords() == null) {
            return;
        }

        Visibility existingKeywordsVisibility = existing.getKeywords() != null && existing.getKeywords().getVisibility() != null ? existing.getKeywords().getVisibility()
                : OrcidVisibilityDefaults.KEYWORD_DEFAULT.getVisibility();
        Visibility updatedKeywordsVisibility = updated.getKeywords().getVisibility() != null ? updated.getKeywords().getVisibility() : existingKeywordsVisibility;

        Keywords updatedKeywords = updated.getKeywords();
        updatedKeywords.setVisibility(updatedKeywordsVisibility);
        existing.setKeywords(updatedKeywords);
    }

    @Override
    public void copyUpdatedContactDetailsToExistingPreservingVisibility(OrcidBio existing, OrcidBio updated) {
        if (updated.getContactDetails() == null) {
            return;
        }

        ContactDetails updatedContactDetails = updated.getContactDetails();
        ContactDetails existingContactDetails = existing.getContactDetails();

        Email updatedPrimaryEmail = updatedContactDetails.retrievePrimaryEmail();

        for (Email existingEmail : existingContactDetails.getEmail()) {
            Email updatedEmail = updatedContactDetails.getEmailByString(existingEmail.getValue());
            if (updatedEmail == null) {
                // Make sure existing private emails are preserved. The API
                // client wouldn't have been able to see these to know to write
                // them back.
                if (Visibility.PRIVATE.equals(existingEmail.getVisibility())) {
                    // Make sure not ending up with more than one primary.
                    if (updatedPrimaryEmail != null) {
                        existingEmail.setPrimary(false);
                    }
                    updatedContactDetails.getEmail().add(existingEmail);
                }
            } else {
                if (updatedEmail.getVisibility() == null) {
                    // Make sure existing privacy level is preserved if not
                    // specified in incoming.
                    updatedEmail.setVisibility(existingEmail.getVisibility());
                }
            }
        }

        // Set any remaining null visibilities to the default value.
        for (Email email : updatedContactDetails.getEmail()) {
            if (email.getVisibility() == null) {
                if (email.isPrimary()) {
                    email.setVisibility(OrcidVisibilityDefaults.PRIMARY_EMAIL_DEFAULT.getVisibility());
                } else {
                    email.setVisibility(OrcidVisibilityDefaults.ALTERNATIVE_EMAIL_DEFAULT.getVisibility());
                }
            }
        }

        if (updatedContactDetails.getAddress() == null) {
            updatedContactDetails.setAddress(existingContactDetails.getAddress());
            if (updatedContactDetails.getAddress() != null && updatedContactDetails.getAddress().getCountry().getVisibility() == null) {
                if (updatedContactDetails.getAddress().getCountry().getVisibility() == null) {
                    updatedContactDetails.getAddress().getCountry().setVisibility(OrcidVisibilityDefaults.COUNTRY_DEFAULT.getVisibility());
                }
            }
        } else if (updatedContactDetails.getAddress() != null && updatedContactDetails.getAddress().getCountry() == null) {
            Address existingAddress = existingContactDetails.getAddress();
            if (existingAddress.getCountry() != null) {
                updatedContactDetails.getAddress().setCountry(existingAddress.getCountry());
                if (updatedContactDetails.getAddress().getCountry().getVisibility() == null) {
                    updatedContactDetails.getAddress().getCountry().setVisibility(OrcidVisibilityDefaults.COUNTRY_DEFAULT.getVisibility());
                }
            }
        } else if (updatedContactDetails.getAddress().getCountry() != null && updatedContactDetails.getAddress().getCountry().getVisibility() == null) {
            Address existingAddress = existingContactDetails.getAddress();
            if (existingAddress != null && existingAddress.getCountry().getVisibility() != null) {
                updatedContactDetails.getAddress().getCountry().setVisibility(existingAddress.getCountry().getVisibility());
            } else {
                updatedContactDetails.getAddress().getCountry().setVisibility(OrcidVisibilityDefaults.COUNTRY_DEFAULT.getVisibility());
            }
        }
        existing.setContactDetails(updated.getContactDetails());
    }

    @Override
    public void copyUpdatedResearcherUrlPreservingVisbility(OrcidBio existing, OrcidBio updated) {
        if (updated.getResearcherUrls() == null) {
            return;
        }
        ResearcherUrls existingResearcherUrls = existing.getResearcherUrls();
        ResearcherUrls updatedResearcherUrls = updated.getResearcherUrls();

        Visibility existingVisibility = (existingResearcherUrls != null && existingResearcherUrls.getVisibility() != null) ? existingResearcherUrls.getVisibility()
                : OrcidVisibilityDefaults.RESEARCHER_URLS_DEFAULT.getVisibility();
        Visibility updatedVisibility = (updatedResearcherUrls != null && updatedResearcherUrls.getVisibility() != null) ? updatedResearcherUrls.getVisibility()
                : existingVisibility;

        // now visibility has been preserved, overwrite the content
        updatedResearcherUrls.setVisibility(updatedVisibility);
        existing.setResearcherUrls(updatedResearcherUrls);
    }

    @Override
    public void copyUpdatedPersonalDetailsToExistingPreservingVisibility(OrcidBio existing, OrcidBio updated) {

        PersonalDetails existingPersonalDetails = existing.getPersonalDetails();
        PersonalDetails updatedPersonalDetails = updated.getPersonalDetails();

        // if no update, nothing to do
        if (updatedPersonalDetails == null) {
            return;
        }

        // if existing null, update unconditionally - we've no previous to
        // compare to
        if (existingPersonalDetails == null) {
            existing.setPersonalDetails(updatedPersonalDetails);
            return;
        }

        // otherwise preserve visibility of other names && credit names
        copyOtherNamesPreservingVisibility(existingPersonalDetails, updatedPersonalDetails);
        copyCreditNamePreservingVisibility(existingPersonalDetails, updatedPersonalDetails);
        existing.setPersonalDetails(updatedPersonalDetails);

    }

    private void copyOtherNamesPreservingVisibility(PersonalDetails existingPersonalDetails, PersonalDetails updatedPersonalDetails) {
        OtherNames existingOtherNames = existingPersonalDetails.getOtherNames();
        OtherNames updatedOtherNames = updatedPersonalDetails.getOtherNames();

        // if no update, nothing to do
        if (updatedOtherNames == null) {
            return;
        }

        // otherwise take into account the visibility of updated and existing
        Visibility existingVisibility = existingOtherNames.getVisibility() != null ? existingOtherNames.getVisibility() : OrcidVisibilityDefaults.OTHER_NAMES_DEFAULT
                .getVisibility();
        updatedOtherNames.setVisibility(updatedOtherNames.getVisibility() != null ? updatedOtherNames.getVisibility() : existingVisibility);
        // now visibility has been preserved, overwrite the content
        existingPersonalDetails.setOtherNames(updatedOtherNames);

    }

    private void copyCreditNamePreservingVisibility(PersonalDetails existingPersonalDetails, PersonalDetails updatedPersonalDetails) {
        CreditName existingCreditName = existingPersonalDetails.getCreditName();
        CreditName updatedCreditName = updatedPersonalDetails.getCreditName();

        // if no update, nothing to do
        if (updatedCreditName == null) {
            return;
        }

        // otherwise take into account the visibility of updated and existing
        Visibility existingVisibility = (existingCreditName != null && existingCreditName.getVisibility() != null) ? existingCreditName.getVisibility()
                : OrcidVisibilityDefaults.CREDIT_NAME_DEFAULT.getVisibility();
        Visibility updatedVisibility = (updatedCreditName != null && updatedCreditName.getVisibility() != null) ? updatedCreditName.getVisibility() : existingVisibility;
        updatedCreditName.setVisibility(updatedVisibility);

        // now visibility has been preserved, overwrite the content
        existingPersonalDetails.setCreditName(updatedCreditName);

    }

    @Override
    public void copyUpdatedWorksPreservingVisbility(OrcidWorks existingWorks, OrcidWorks updatedWorks) {

        if (updatedWorks == null) {
            // nothing to update, bale out
            return;
        }

        if (existingWorks == null) {
            existingWorks = new OrcidWorks();
        }

        Map<String, OrcidWork> updatedWorksMap = updatedWorks.retrieveOrcidWorksAsMap();

        for (OrcidWork existingWork : existingWorks.getOrcidWork()) {
            OrcidWork updatedWork = updatedWorksMap.get(existingWork.getPutCode());
            if (updatedWork == null) {
                // Make sure private works are preserved
                if (Visibility.PRIVATE.equals(existingWork.getVisibility())) {
                    updatedWorks.getOrcidWork().add(existingWork);
                }
            } else {
                // Check the source of the existing work is the same as the
                // current source
                checkSource(existingWork);
                // Make sure privacy preserved if not specified in incoming
                if (updatedWork.getVisibility() == null) {
                    updatedWork.setVisibility(existingWork.getVisibility());
                }
            }
        }

        // Set remaining null visibilities to default value
        List<OrcidWork> orcidWorkToUpdate = updatedWorks.getOrcidWork();
        for (OrcidWork orcidWork : orcidWorkToUpdate) {
            if (orcidWork.getVisibility() == null) {
                orcidWork.setVisibility(OrcidVisibilityDefaults.WORKS_DEFAULT.getVisibility());
            }
            WorkContributors workContributors = orcidWork.getWorkContributors();
            if (workContributors != null) {
                for (Contributor contributor : workContributors.getContributor()) {
                    CreditName creditName = contributor.getCreditName();
                    if (creditName != null) {
                        creditName.setVisibility(orcidWork.getVisibility());
                    }
                }
            }
        }

        existingWorks.setOrcidWork(orcidWorkToUpdate);
    }

    private void checkSource(OrcidWork existingWork) {
        String currentSource = sourceManager.retrieveSourceOrcid();
        if (currentSource == null) {
            // Not under Spring security so anything goes
            return;
        }
        if (!currentSource.equals(existingWork.getWorkSource().getPath())) {
            throw new WrongSourceException();
        }
    }

    private void checkSource(Affiliation existingAffiliation) {
        if (isFromDifferentSource(existingAffiliation)) {
            throw new WrongSourceException();
        }

    }

    private boolean isFromDifferentSource(Affiliation existingAffiliation) {
        String currentSource = sourceManager.retrieveSourceOrcid();
        if (currentSource == null) {
            // Not under Spring security so anything goes
            return false;
        }
        return !currentSource.equals(existingAffiliation.getSource().getSourceOrcid().getPath());
    }

    private Funding obtainLikelyEqual(Funding toCompare, List<Funding> toCompareTo) {
        if (toCompare != null && toCompareTo != null && !toCompareTo.isEmpty()) {
            for (Funding ai : toCompareTo) {
                if (ai.equals(toCompare)) {
                    return ai;
                }
            }
        }
        return null;
    }

    @Override
    public void copyUpdatedFundingListVisibilityInformationOnlyPreservingVisbility(FundingList existingFundingList, FundingList updatedFundingList) {
        throw new RuntimeException("Not implemented!");
    }

    private void mergeFundings(List<Funding> existingFundings, Funding updatedFunding) {
        Funding likelyExisting = obtainLikelyEqual(updatedFunding, existingFundings);
        if (likelyExisting != null) {
            Visibility likelyExistingFundingInstitutionNameVisibility = likelyExisting.getVisibility();

            if (likelyExistingFundingInstitutionNameVisibility == null && updatedFunding.getVisibility() == null) {
                updatedFunding.setVisibility(OrcidVisibilityDefaults.FUNDING_DEFAULT.getVisibility());
            } else if (updatedFunding.getVisibility() == null && likelyExistingFundingInstitutionNameVisibility != null) {
                updatedFunding.setVisibility(likelyExistingFundingInstitutionNameVisibility);
            }
        } else {
            // if you can't match this type, default its value if null
            updatedFunding.setVisibility(updatedFunding.getVisibility() != null ? updatedFunding.getVisibility() : OrcidVisibilityDefaults.FUNDING_DEFAULT
                    .getVisibility());
        }
    }

}
