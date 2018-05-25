package org.orcid.core.adapter.v3;

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.v3.rc1.record.Membership;
import org.orcid.jaxb.model.v3.rc1.record.summary.MembershipSummary;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;

/**
 * 
 * @author Angel Montenegro
 *
 */
public interface JpaJaxbMembershipAdapter {

    OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Membership membership);

    Membership toMembership(OrgAffiliationRelationEntity entity);
    
    MembershipSummary toMembershipSummary(OrgAffiliationRelationEntity entity);

    List<Membership> toMembership(Collection<OrgAffiliationRelationEntity> entities);
    
    List<MembershipSummary> toMembershipSummary(Collection<OrgAffiliationRelationEntity> entities);
    
    OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Membership membership, OrgAffiliationRelationEntity existing);

}
