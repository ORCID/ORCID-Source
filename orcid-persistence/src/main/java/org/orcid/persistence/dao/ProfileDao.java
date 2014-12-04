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
package org.orcid.persistence.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.GroupType;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.Locale;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.jpa.entities.EmailEventType;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;

public interface ProfileDao extends GenericDao<ProfileEntity, String> {

    List<ProfileEntity> retrieveSelectableSponsors();

    List<String> findOrcidsByName(String name);

    public boolean exists(String orcid);

    List<String> findByEventTypes(int maxResults, List<ProfileEventType> pet, Collection<String> orcidsToExclude, boolean not);

    List<String> findOrcidsByIndexingStatus(IndexingStatus indexingStatus, int maxResults);

    List<String> findOrcidsByIndexingStatus(IndexingStatus indexingStatus, int maxResults, Collection<String> orcidsToExclude);

    List<String> findOrcidsByIndexingStatus(Collection<IndexingStatus> indexingStatuses, int maxResults, Collection<String> orcidsToExclude);

    List<String> findUnclaimedNotIndexedAfterWaitPeriod(int waitPeriodDays, int maxDaysBack, int maxResults, Collection<String> orcidsToExclude);

    List<String> findUnclaimedNeedingReminder(int reminderAfterDays, int maxResults, Collection<String> orcidsToExclude);

    List<String> findOrcidsNeedingEmailMigration(int maxResults);

    List<ProfileEntity> findProfilesThatMissedIndexing(int maxResults);

    boolean orcidExists(String orcid);

    void remove(String giverOrcid, String receiverOrcid);

    void removeChildrenWithGeneratedIds(ProfileEntity profileEntity);

    boolean hasBeenGivenPermissionTo(String giverOrcid, String receiverOrcid);

    boolean existsAndNotClaimedAndBelongsTo(String messageOrcid, String clientId);

    void updateIndexingStatus(String orcid, IndexingStatus indexingStatus);

    Long getConfirmedProfileCount();

    public void updateCountry(String orcid, Iso3166Country country, Visibility activitiesVisibilityDefault);

    public Iso3166Country getCountry(String orcid);

    public void updateBiography(String orcid, String biography, Visibility visibility);

    boolean updateProfile(ProfileEntity profile);

    Date retrieveLastModifiedDate(String orcid);

    Date updateLastModifiedDate(String orcid);

    void updateLastModifiedDateWithoutResult(String orcid);

    void updateLastModifiedDateAndIndexingStatusWithoutResult(String orcid, Date lastModified, IndexingStatus indexingStatus);

    void updateLastModifiedDateAndIndexingStatus(String orcid);

    public List<String> findEmailsUnverfiedDays(int daysUnverified, int maxResults, EmailEventType ev);

    OrcidType retrieveOrcidType(String orcid);

    List<Object[]> findInfoForDecryptionAnalysis();

    Locale retrieveLocale(String orcid);

    void updateLocale(String orcid, Locale locale);

    boolean deprecateProfile(String deprecatedOrcid, String primaryOrcid);

    String retrievePrimaryAccountOrcid(String deprecatedOrcid);

    boolean isProfileDeprecated(String orcid);

    void updateEncryptedPassword(String orcid, String encryptedPassword);

    void updateSecurityQuestion(String orcid, Integer securityQuestionId, String encryptedSecurityAnswer);

    void updatePreferences(String orcid, boolean sendChangeNotifications, boolean sendOrcidNews, boolean sendMemberUpdateRequests,
            Visibility activitiesVisibilityDefault, boolean enableDeveloperTools, float sendEmailFrequencyDays);

    List<ProfileEntity> findProfilesByOrcidType(OrcidType type);

    public void updateNames(String orcid, String givenName, String familyName, String creditName, Visibility creditNameVisibility);

    boolean updateDeveloperTools(String orcid, boolean enabled);

    public boolean updateResearcherUrlsVisibility(String orcid, Visibility visibility);

    public boolean getClaimedStatus(String orcid);

    ClientType getClientType(String orcid);

    GroupType getGroupType(String orcid);
}
