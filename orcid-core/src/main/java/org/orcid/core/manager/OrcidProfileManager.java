package org.orcid.core.manager;

import java.util.List;

import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.Preferences;
import org.orcid.persistence.jpa.entities.IndexingStatus;

/**
 * @author Will Simpson
 */
@Deprecated
public interface OrcidProfileManager extends OrcidProfileManagerReadOnly {

    /**
     * Creates a new profile, assigning it a new ORCID
     * 
     * @param orcidProfile
     *            the new profile 
     * @return the profile as it is represented in the data store after its
     *         creation
     */
    OrcidProfile createOrcidProfile(OrcidProfile orcidProfile, boolean createdByMember, boolean usedCaptcha);

    /**
     * Creates a new profile, assigning it a new ORCID. Also, sends a
     * notification email to the email in the created profile.
     * 
     * @param orcidProfile
     *            the new profile valiues
     * @return the profile as it is represented in the data store after its
     *         creation
     */

    OrcidProfile createOrcidProfileAndNotify(OrcidProfile orcidProfile);

    /**
     * Updates an existing profile
     * 
     * @param orcidProfile
     *            the new properties of the profile
     * @return the updated profile as it new state dictates
     */
    OrcidProfile updateOrcidProfile(OrcidProfile orcidProfile);
        
    /**
     * Returns true if ORCID exist.
     * 
     * @param orcid
     * @return
     */
    public boolean exists(String orcid);

    OrcidProfile retrievePublicOrcidProfile(String orcid);

    /**
     * Like {@link #updatePersonalInformation(OrcidProfile)}, but for primary
     * institution and joint affiliation (not past institutions).
     * 
     * @see #updatePersonalInformation(OrcidProfile)
     */
    OrcidProfile updateAffiliations(OrcidProfile orcidProfile);

    /**
     * Like {@link #updatePersonalInformation(OrcidProfile)}, but for primary
     * fundings and joint affiliation (not past institutions).
     * 
     * @see #updatePersonalInformation(OrcidProfile)
     */
    OrcidProfile updateFundings(OrcidProfile orcidProfile);

    void updatePreferences(String orcid, Preferences preferences);

    /**
     * Overwrites preferences in the DB with the values in updatedProfile.
     */
    OrcidProfile updateOrcidPreferences(OrcidProfile updatedOrcidProfile);

    /**
     * Adds the works from orcidProfile to the existing profile in the DB
     * (without removing existing works, and without any attempt at
     * de-duplication).
     * 
     * @param orcidProfile
     *            The works to add to the profile.
     */
    void addOrcidWorks(OrcidProfile orcidProfile);    

    /**
     * Deletes an entire ORCID profile - use with care...
     * 
     * @param orcid
     *            the ORCID
     * 
     */
    OrcidProfile deleteProfile(String orcid);

    /**
     * Adds a new {@link org.orcid.jaxb.model.message.Affiliation} to the
     * {@link} OrcidProfile} and returns the updated values
     * 
     * @param orcidProfile
     * @return
     */
    void addAffiliations(OrcidProfile orcidProfile);

    /**
     * Adds a new {@link org.orcid.jaxb.model.message.FundingList} to the
     * {@link} OrcidProfile} and returns the updated values
     * 
     * @param orcidProfile
     * @return
     */
    void addFundings(OrcidProfile orcidProfile);

    /**
     * Attempt to locate a profile with the email address. This is for internal
     * use only, and should not be exposed to any external clients.
     * 
     * @param email
     * @return
     */
    OrcidProfile retrieveOrcidProfileByEmail(String email, LoadOptions loadOptions);

    /**
     * Updates the ORCID bio data
     * 
     * @param orcidProfile
     * @return
     */
    OrcidProfile updateOrcidBio(OrcidProfile orcidProfile);

    /**
     * Updates the ORCID works only
     * 
     * @param orcidProfile
     * @return
     */
    OrcidProfile updateOrcidWorks(OrcidProfile orcidProfile);

    /**
     * Add new external identifiers to an existing profile
     * 
     * @param orcidProfile
     * @return
     */
    OrcidProfile addExternalIdentifiers(OrcidProfile orcidProfile);

    OrcidProfile revokeDelegate(String giverOrcid, String receiverOrcid);    
    
    void processUnclaimedProfilesToFlagForIndexing();
    
    void processUnclaimedProfilesForReminder();

    boolean lockProfile(String orcid, String lockReason, String description);

    boolean unlockProfile(String orcid);

    boolean isLocked(String orcid);

    OrcidWorks dedupeWorks(OrcidWorks orcidWorks);

    void updateLastModifiedDate(String orcid);

    public void checkWorkExternalIdentifiersAreNotDuplicated(List<OrcidWork> newOrcidWorksList, List<OrcidWork> existingWorkList);
    
    public void setCompareWorksUsingScopusWay(boolean compareWorksUsingScopusWay);
}
