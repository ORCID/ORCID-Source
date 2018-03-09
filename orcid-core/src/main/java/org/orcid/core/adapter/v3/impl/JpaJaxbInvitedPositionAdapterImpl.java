package org.orcid.core.adapter.v3.impl;

import java.util.Collection;
import java.util.List;

import ma.glasnost.orika.MapperFacade;

import org.orcid.core.adapter.v3.JpaJaxbInvitedPositionAdapter;
import org.orcid.jaxb.model.v3.dev1.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.dev1.record.InvitedPosition;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class JpaJaxbInvitedPositionAdapterImpl implements JpaJaxbInvitedPositionAdapter {

    private MapperFacade mapperFacade;

    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

    @Override
    public OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(InvitedPosition invitedPosition) {
        if(invitedPosition == null)
            return null;
        return mapperFacade.map(invitedPosition, OrgAffiliationRelationEntity.class);
    }

    @Override
    public InvitedPosition toInvitedPosition(OrgAffiliationRelationEntity entity) {
        if(entity == null)
            return null;
        return mapperFacade.map(entity, InvitedPosition.class);
    }

    public InvitedPositionSummary toInvitedPositionSummary(OrgAffiliationRelationEntity entity) {
        if(entity == null)
            return null;
        return mapperFacade.map(entity, InvitedPositionSummary.class);
    }
    
    @Override
    public List<InvitedPosition> toInvitedPosition(Collection<OrgAffiliationRelationEntity> entities) {
        if(entities == null)
            return null;
        return mapperFacade.mapAsList(entities, InvitedPosition.class);
    }

    @Override
    public List<InvitedPositionSummary> toInvitedPositionSummary(Collection<OrgAffiliationRelationEntity> entities) {
        if(entities == null)
            return null;
        return mapperFacade.mapAsList(entities, InvitedPositionSummary.class);
    }
    
    @Override
    public OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(InvitedPosition invitedPosition, OrgAffiliationRelationEntity existing) {
        if (invitedPosition == null) {
            return null;
        }
        mapperFacade.map(invitedPosition, existing);
        return existing;
    }
}
