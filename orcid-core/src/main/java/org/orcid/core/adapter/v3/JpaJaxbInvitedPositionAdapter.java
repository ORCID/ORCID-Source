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
package org.orcid.core.adapter.v3;

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.v3.dev1.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.dev1.record.InvitedPosition;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;

/**
 * 
 * @author Angel Montenegro
 *
 */
public interface JpaJaxbInvitedPositionAdapter {

    OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(InvitedPosition invitedPosition);

    InvitedPosition toInvitedPosition(OrgAffiliationRelationEntity entity);
    
    InvitedPositionSummary toInvitedPositionSummary(OrgAffiliationRelationEntity entity);

    List<InvitedPosition> toInvitedPosition(Collection<OrgAffiliationRelationEntity> entities);
    
    List<InvitedPositionSummary> toInvitedPositionSummary(Collection<OrgAffiliationRelationEntity> entities);
    
    OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(InvitedPosition invitedPosition, OrgAffiliationRelationEntity existing);

}
