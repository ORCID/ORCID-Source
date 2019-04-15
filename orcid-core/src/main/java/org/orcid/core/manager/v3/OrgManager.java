package org.orcid.core.manager.v3;

import java.io.Writer;
import java.util.List;

import org.orcid.jaxb.model.message.Organization;
import org.orcid.jaxb.model.v3.release.common.MultipleOrganizationHolder;
import org.orcid.jaxb.model.v3.release.common.OrganizationHolder;
import org.orcid.jaxb.model.v3.release.record.ResearchResourceHosts;
import org.orcid.persistence.jpa.entities.AmbiguousOrgEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface OrgManager {

    List<AmbiguousOrgEntity> getAmbiguousOrgs(int firstResult, int maxResults);

    void writeAmbiguousOrgs(Writer writer);

    void writeDisambiguatedOrgs(Writer writer);

    List<OrgEntity> getOrgs(String searchTerm, int firstResult, int maxResults);
    
    List<OrgEntity> getOrgsByName(String searchTerm);

    OrgEntity getOrgEntity(OrganizationHolder holder);
    
    OrgEntity getOrgEntity(Organization org);

    List<OrgEntity> getOrgEntities(MultipleOrganizationHolder holder);
}
