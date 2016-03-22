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
package org.orcid.core.manager.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.OrcidJaxbCopyManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.message.ActivitiesContainer;
import org.orcid.jaxb.model.message.Activity;
import org.orcid.jaxb.model.message.Address;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.Biography;
import org.orcid.jaxb.model.message.Claimed;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingList;
import org.orcid.jaxb.model.message.Keyword;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.OtherName;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.SourceClientId;
import org.orcid.jaxb.model.message.SourceOrcid;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.OrcidStringUtils;
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
        copyActivitiesToExistingPreservingVisibility(existingAffiliations, updatedAffiliations, OrcidVisibilityDefaults.AFFILIATE_NAME_DEFAULT.getVisibility());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void copyActivitiesToExistingPreservingVisibility(ActivitiesContainer existingActivities, ActivitiesContainer updatedActivities, Visibility defaultVisibility) {
        if (updatedActivities == null) {
            return;
        }
        if (existingActivities == null) {
            try {
                existingActivities = updatedActivities.getClass().newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        Map<String, ? extends Activity> updatedActivitiesMap = updatedActivities.retrieveActivitiesAsMap();
        Source targetSource = createSource(sourceManager.retrieveSourceOrcid());
        for (Iterator<? extends Activity> existingActivitiesIterator = existingActivities.retrieveActivities().iterator(); existingActivitiesIterator.hasNext();) {
            Activity existingActivity = existingActivitiesIterator.next();
            Activity updatedActivity = updatedActivitiesMap.get(existingActivity.getPutCode());
            if (updatedActivity == null) {
                if (!(Visibility.PRIVATE.equals(existingActivity.getVisibility()) || isFromDifferentSource(existingActivity))) {
                    // Remove existing activities unless they are private (we
                    // need to keep those because the API user won't even know
                    // they are there) or they are from another source
                    existingActivitiesIterator.remove();
                }

            } else {
                // Check the source of the existing activity is the same as
                // the current source
                checkSource(existingActivity);
                if (updatedActivity.getVisibility() == null || !updatedActivity.getVisibility().equals(existingActivity.getVisibility())) {
                    // Keep the visibility from the existing activity unless
                    // was set by API user
                    updatedActivity.setVisibility(existingActivity.getVisibility());
                }
                addSourceToActivity(updatedActivity, targetSource);
                // Can remove existing object because will be replaced by
                // incoming
                existingActivitiesIterator.remove();
            }
        }
        for (Activity updatedActivity : updatedActivities.retrieveActivities()) {
            // Set default visibility for any remaining incoming affiliations
            if (updatedActivity.getVisibility() == null) {
                updatedActivity.setVisibility(defaultVisibility);
            }
            if (updatedActivity.getPutCode() == null) {
                // Check source is correct for any newly added activities, if
                // mentioned
            	addSourceToActivity(updatedActivity, targetSource);
            }
        }
        existingActivities.retrieveActivities().addAll((List) updatedActivities.retrieveActivities());
    }

    private void addSourceToActivity(Activity updatedActivity, Source targetSource) {
		if(updatedActivity instanceof OrcidWork) {
			((OrcidWork) updatedActivity).setSource(targetSource);
		} else if(updatedActivity instanceof Funding) {
			((Funding) updatedActivity).setSource(targetSource);
		} else if(updatedActivity instanceof Affiliation) {
			((Affiliation) updatedActivity).setSource(targetSource);
		}
	}

	private void checkSource(Activity activity) {
        if (isFromDifferentSource(activity)) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("activity", "activity");
        	throw new WrongSourceException(params);
        }

    }
    
    private Source createSource(String amenderOrcid) {
        Source source = new Source();
        if (OrcidStringUtils.isValidOrcid(amenderOrcid)) {
            source.setSourceOrcid(new SourceOrcid(amenderOrcid));
            source.setSourceClientId(null);
        } else {
            source.setSourceClientId(new SourceClientId(amenderOrcid));
            source.setSourceOrcid(null);
        }
        return source;
    }

    private boolean isFromDifferentSource(Activity activity) {
        String currentSource = sourceManager.retrieveSourceOrcid();
        if (currentSource == null) {
            // Not under Spring security so anything goes
            return false;
        }
        return !currentSource.equals(activity.retrieveSourcePath());
    }

    @Override
    public void copyFundingListToExistingPreservingVisibility(FundingList existingFundings, FundingList updatedFundings) {
        copyActivitiesToExistingPreservingVisibility(existingFundings, updatedFundings, OrcidVisibilityDefaults.FUNDING_DEFAULT.getVisibility());
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
        
        if(Visibility.PRIVATE.equals(existingExternalIdentifiersVisibility)) {
           if(existingExternalIdentifiers != null) {
               for(ExternalIdentifier extId : updatedExternalIdentifiers.getExternalIdentifier()) {
                   if(!existingExternalIdentifiers.getExternalIdentifier().contains(extId)) {
                       existingExternalIdentifiers.getExternalIdentifier().add(extId);
                   }
               }
           } 
        } else {
            existing.setExternalIdentifiers(updatedExternalIdentifiers);
        }        
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
        
        if(existingShortDescription != null) {
            if(!existingShortDescription.getVisibility().equals(Visibility.PRIVATE)) {
                existing.setBiography(updatedShortDescription);    
            }
        } else {
            existing.setBiography(updatedShortDescription);
        }                
    }

    @Override
    public void copyUpdatedKeywordsToExistingPreservingVisibility(OrcidBio existing, OrcidBio updated) {
        if (updated.getKeywords() == null) {
            return;
        }

        Visibility existingKeywordsVisibility = existing.getKeywords() != null && existing.getKeywords().getVisibility() != null ? existing.getKeywords().getVisibility()
                : OrcidVisibilityDefaults.KEYWORD_DEFAULT.getVisibility();
        if(existingKeywordsVisibility.equals(Visibility.PRIVATE)) {
            Keywords existingKeywords = existing.getKeywords();
            if(existingKeywords != null) {
                for(Keyword keyword : updated.getKeywords().getKeyword()) {
                    if(!existingKeywords.getKeyword().contains(keyword)) {
                        existingKeywords.getKeyword().add(keyword);
                    }
                }
            }
        } else {
            Visibility updatedKeywordsVisibility = updated.getKeywords().getVisibility() != null ? updated.getKeywords().getVisibility() : existingKeywordsVisibility;
            Keywords updatedKeywords = updated.getKeywords();
            updatedKeywords.setVisibility(updatedKeywordsVisibility);
            existing.setKeywords(updatedKeywords);
        }        
    }

    @Override
    public void copyUpdatedContactDetailsToExistingPreservingVisibility(OrcidBio existing, OrcidBio updated) {
       ContactDetails existingContactDetails = existing.getContactDetails();
       ContactDetails updatedContactDetails = updated.getContactDetails();
//     copyUpdatedEmails(existingContactDetails, updatedContactDetails);
       copyUpdatedAddress(existingContactDetails, updatedContactDetails);
    }

    //Not being used now as the client is not allowed to add or edit emails.
    @SuppressWarnings("unused")
	private void copyUpdatedEmails(ContactDetails existingContactDetails, ContactDetails updatedContactDetails) {
    	String clientId = sourceManager.retrieveSourceOrcid();
    	List<Email> allEmails = new ArrayList<Email>();
		List<Email> existingEmails = existingContactDetails.getEmail();
		
		for(Email oldEmail : existingEmails) {
			Email tempEmail = null;
			if(updatedContactDetails != null) {
				tempEmail = updatedContactDetails.getEmailByString(oldEmail.getValue());
			}
			String oldEmSource = (oldEmail.getSourceClientId() == null) ? oldEmail.getSource() : oldEmail.getSourceClientId();
			if(clientId == null || (clientId != null && !clientId.equals(oldEmSource))) {
				allEmails.add(oldEmail);
				if(tempEmail != null) {
					updatedContactDetails.getEmail().remove(tempEmail);
				}
			} else {
				if(oldEmail.isPrimary()) {
					if(tempEmail != null) {
						updatedContactDetails.getEmail().remove(tempEmail);
						if(!Visibility.PRIVATE.equals(oldEmail.getVisibility())) {
							oldEmail.setVisibility(tempEmail.getVisibility());
						}
					}
					allEmails.add(oldEmail);
				} else if(Visibility.PRIVATE.equals(oldEmail.getVisibility())) {
					if(tempEmail != null) {
						updatedContactDetails.getEmail().remove(tempEmail);
					}
					allEmails.add(oldEmail);
				} else {
					if(tempEmail != null) {
						updatedContactDetails.getEmail().remove(tempEmail);
						tempEmail.setPrimary(false);
						allEmails.add(tempEmail);
					}
				}
			}
		}
		//Set primary = false for each remaining new email.
		if(updatedContactDetails != null) {
			for(Email newEmail : updatedContactDetails.getEmail()) {
				newEmail.setPrimary(false);
				allEmails.add(newEmail);
			}
		}
		
		for(Email email : allEmails) {
			if(email.getVisibility() == null) {
				email.setVisibility(OrcidVisibilityDefaults.ALTERNATIVE_EMAIL_DEFAULT.getVisibility());
			}
		}
		existingContactDetails.setEmail(allEmails);
	}

	private void copyUpdatedAddress(ContactDetails existingContactDetails, ContactDetails updatedContactDetails) {
		if(updatedContactDetails == null) {
			return;
		}
		if (existingContactDetails.getAddress() == null) {
            existingContactDetails.setAddress(updatedContactDetails.getAddress());
        } else {
            Address existingAddress = existingContactDetails.getAddress();
            Address updatedAddress = updatedContactDetails.getAddress();

            if (updatedAddress != null) {
                if (existingAddress.getCountry() != null) {
                    if (!Visibility.PRIVATE.equals(existingAddress.getCountry().getVisibility())) {
                        if(updatedAddress.getCountry() != null) {
                            existingAddress.getCountry().setValue(updatedAddress.getCountry().getValue());
                        }
                    }
                } else {
                    existingAddress.setCountry(updatedAddress.getCountry());
                }
            }
        }

        if (existingContactDetails.getAddress() != null && existingContactDetails.getAddress().getCountry() != null
                && existingContactDetails.getAddress().getCountry().getVisibility() == null) {
            existingContactDetails.getAddress().getCountry().setVisibility(OrcidVisibilityDefaults.COUNTRY_DEFAULT.getVisibility());
        }
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

        // If it is private, add the new researcher url but preserve visibility
        if (existingVisibility.equals(Visibility.PRIVATE)) {
            for (ResearcherUrl rUrl : updatedResearcherUrls.getResearcherUrl()) {
                if (!existingResearcherUrls.getResearcherUrl().contains(rUrl)) {
                    existingResearcherUrls.getResearcherUrl().add(rUrl);
                }
            }
        } else {
            Visibility updatedVisibility = (updatedResearcherUrls != null && updatedResearcherUrls.getVisibility() != null) ? updatedResearcherUrls.getVisibility()
                    : existingVisibility;

            // now visibility has been preserved, overwrite the content
            updatedResearcherUrls.setVisibility(updatedVisibility);
            existing.setResearcherUrls(updatedResearcherUrls);
        }
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
        if(updatedPersonalDetails.getFamilyName() != null && !PojoUtil.isEmpty(updatedPersonalDetails.getFamilyName().getContent())) {
            existingPersonalDetails.setFamilyName(updatedPersonalDetails.getFamilyName());
        }
        
        if(updatedPersonalDetails.getGivenNames() != null && !PojoUtil.isEmpty(updatedPersonalDetails.getGivenNames().getContent())) {
            existingPersonalDetails.setGivenNames(updatedPersonalDetails.getGivenNames());
        }        
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
        // If it is private, add the new ones, but preserve the visibility
        if (existingVisibility.equals(Visibility.PRIVATE)) {
            for (OtherName otherName : updatedOtherNames.getOtherName()) {
                if (!existingOtherNames.getOtherName().contains(otherName)) {
                    existingOtherNames.getOtherName().add(otherName);
                }
            }
        } else {
            updatedOtherNames.setVisibility(updatedOtherNames.getVisibility() != null ? updatedOtherNames.getVisibility() : existingVisibility);
            // now visibility has been preserved, overwrite the content
            existingPersonalDetails.setOtherNames(updatedOtherNames);
        }

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
                : OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility();
        // If it is private, ignore the request
        if (!existingVisibility.equals(Visibility.PRIVATE)) {
            Visibility updatedVisibility = (updatedCreditName != null && updatedCreditName.getVisibility() != null) ? updatedCreditName.getVisibility()
                    : existingVisibility;
            updatedCreditName.setVisibility(updatedVisibility);
            // now visibility has been preserved, overwrite the content
            existingPersonalDetails.setCreditName(updatedCreditName);
        }

    }

    @Override
    public void copyUpdatedWorksPreservingVisbility(OrcidWorks existingWorks, OrcidWorks updatedWorks) {
        copyActivitiesToExistingPreservingVisibility(existingWorks, updatedWorks, OrcidVisibilityDefaults.WORKS_DEFAULT.getVisibility());
    }

    @Override
    public void copyUpdatedFundingListVisibilityInformationOnlyPreservingVisbility(FundingList existingFundingList, FundingList updatedFundingList) {
        throw new RuntimeException("Not implemented!");
    }

	public void setSourceManager(SourceManager sourceManager) {
		this.sourceManager = sourceManager;
	}

}