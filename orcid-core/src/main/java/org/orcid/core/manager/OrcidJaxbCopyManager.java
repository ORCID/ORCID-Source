package org.orcid.core.manager;

import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.FundingList;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidWorks;

public interface OrcidJaxbCopyManager {

    public abstract void copyRelevantUpdatedHistoryElements(OrcidHistory existing, OrcidHistory updated);

    public abstract void copyUpdatedBioToExistingWithVisibility(OrcidBio existing, OrcidBio updated);

    public abstract void copyAffiliationsToExistingPreservingVisibility(Affiliations existingAffiliations, Affiliations updatedAffiliations);

    public abstract void copyFundingListToExistingPreservingVisibility(FundingList existingFundings, FundingList updatedFundings);

    public abstract void copyUpdatedExternalIdentifiersToExistingPreservingVisibility(OrcidBio existing, OrcidBio updated);

    public abstract void copyUpdatedShortDescriptionToExistingPreservingVisibility(OrcidBio existing, OrcidBio updated);

    public abstract void copyUpdatedKeywordsToExistingPreservingVisibility(OrcidBio existing, OrcidBio updated);

    public abstract void copyUpdatedContactDetailsToExistingPreservingVisibility(OrcidBio existing, OrcidBio updated);

    public abstract void copyUpdatedResearcherUrlPreservingVisbility(OrcidBio existing, OrcidBio updated);

    public abstract void copyUpdatedPersonalDetailsToExistingPreservingVisibility(OrcidBio existing, OrcidBio updated);

    public abstract void copyUpdatedWorksPreservingVisbility(OrcidWorks existingWorks, OrcidWorks updatedWorks);

    public abstract void copyUpdatedFundingListVisibilityInformationOnlyPreservingVisbility(FundingList existingFundingList, FundingList updatedFundingList);

}