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

import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.ProfileEntity;

public interface ProfileDao extends GenericDao<ProfileEntity, String> {

    List<ProfileEntity> retrieveSelectableSponsors();

    ProfileEntity findByEmail(String email);

    List<String> findOrcidsByName(String name);

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

    Date retrieveLastModifiedDate(String orcid);

    OrcidType retrieveOrcidType(String orcid);

}
