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
package org.orcid.persistence.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.orcid.jaxb.model.message.Locale;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.persistence.jpa.entities.EmailEventType;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;

public interface ProfileDao extends GenericDao<ProfileEntity, String> {

    List<ProfileEntity> retrieveSelectableSponsors();

    List<String> findOrcidsByName(String name);

    List<String> findByEventTypes(int maxResults, List<ProfileEventType> pet, Collection<String> orcidsToExclude, boolean not);

    List<String> findOrcidsByIndexingStatus(IndexingStatus indexingStatus, int maxResults);

    List<String> findOrcidsByIndexingStatus(IndexingStatus indexingStatus, int maxResults, Collection<String> orcidsToExclude);

    List<String> findUnclaimedNotIndexedAfterWaitPeriod(int waitPeriodDays, int maxResults, Collection<String> orcidsToExclude);

    List<String> findUnclaimedNeedingReminder(int reminderAfterDays, int maxResults, Collection<String> orcidsToExclude);

    List<String> findOrcidsNeedingEmailMigration(int maxResults);

    boolean orcidExists(String orcid);

    boolean emailExists(String email);

    void remove(String giverOrcid, String receiverOrcid);

    void removeChildrenWithGeneratedIds(ProfileEntity profileEntity);

    boolean hasBeenGivenPermissionTo(String giverOrcid, String receiverOrcid);

    boolean existsAndNotClaimedAndBelongsTo(String messageOrcid, String clientId);

    void updateIndexingStatus(String orcid, IndexingStatus indexingStatus);

    Long getConfirmedProfileCount();

    boolean updateProfile(ProfileEntity profile);

    Date retrieveLastModifiedDate(String orcid);

    Date updateLastModifiedDate(String orcid);

    void updateLastModifiedDateWithoutResult(String orcid);

    void updateLastModifiedDateAndIndexingStatus(String orcid);
    
    public List<String> findEmailsUnverfiedDays(int daysUnverified, int maxResults, EmailEventType ev);

    OrcidType retrieveOrcidType(String orcid);

    List<Object[]> findInfoForDecryptionAnalysis();

    Locale retrieveLocale(String orcid);

    void updateLocale(String orcid, Locale locale);

}
