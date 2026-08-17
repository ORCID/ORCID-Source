package org.orcid.core.adapter.v3.impl;

import java.util.Collection;
import java.util.List;

import org.orcid.core.adapter.impl.MapperFacadeSupport;
import org.orcid.core.adapter.v3.JpaJaxbMembershipAdapter;
import org.orcid.jaxb.model.v3.release.record.Membership;
import org.orcid.jaxb.model.v3.release.record.summary.MembershipSummary;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class JpaJaxbMembershipAdapterImpl implements JpaJaxbMembershipAdapter {

    private final MapperFacadeSupport mapperFacade = new MapperFacadeSupport();

    public void setMapperFacade(Object mapperFacade) {
        this.mapperFacade.setMapperFacade(mapperFacade);
    }

    @Override
    public OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Membership membership) {
        if(membership == null)
            return null;
        return mapperFacade.map(membership, OrgAffiliationRelationEntity.class);
    }

    @Override
    public Membership toMembership(OrgAffiliationRelationEntity entity) {
        if(entity == null)
            return null;
        return mapperFacade.map(entity, Membership.class);
    }

    public MembershipSummary toMembershipSummary(OrgAffiliationRelationEntity entity) {
        if(entity == null)
            return null;
        return mapperFacade.map(entity, MembershipSummary.class);
    }
    
    @Override
    public List<Membership> toMembership(Collection<OrgAffiliationRelationEntity> entities) {
        if(entities == null)
            return null;
        return mapperFacade.mapAsList(entities, Membership.class);
    }

    @Override
    public List<MembershipSummary> toMembershipSummary(Collection<OrgAffiliationRelationEntity> entities) {
        if(entities == null)
            return null;
        return mapperFacade.mapAsList(entities, MembershipSummary.class);
    }
    
    @Override
    public OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Membership membership, OrgAffiliationRelationEntity existing) {
        if (membership == null) {
            return null;
        }
        mapperFacade.map(membership, existing);
        return existing;
    }
}
