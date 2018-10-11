package org.orcid.core.adapter.v3;

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.v3.rc2.record.Service;
import org.orcid.jaxb.model.v3.rc2.record.summary.ServiceSummary;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;

/**
 * 
 * @author Angel Montenegro
 *
 */
public interface JpaJaxbServiceAdapter {

    OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Service service);

    Service toService(OrgAffiliationRelationEntity entity);
    
    ServiceSummary toServiceSummary(OrgAffiliationRelationEntity entity);

    List<Service> toService(Collection<OrgAffiliationRelationEntity> entities);
    
    List<ServiceSummary> toServiceSummary(Collection<OrgAffiliationRelationEntity> entities);
    
    OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Service service, OrgAffiliationRelationEntity existing);

}
