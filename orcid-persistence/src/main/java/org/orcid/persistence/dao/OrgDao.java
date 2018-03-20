package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.persistence.jpa.entities.AmbiguousOrgEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface OrgDao extends GenericDao<OrgEntity, Long> {

    List<AmbiguousOrgEntity> getAmbiguousOrgs(int firstResult, int maxResults);

    List<OrgEntity> getOrgs(String searchTerm, int firstResult, int maxResults);
    
    List<OrgEntity> getOrgsByName(String searchTerm);

    OrgEntity findByNameCityRegionAndCountry(String name, String city, String region, Iso3166Country country);
    
    void removeOrgsByClientSourceId(String clientSourceId);

    OrgEntity findByAddressAndDisambiguatedOrg(String name, String city, String region, Iso3166Country country, OrgDisambiguatedEntity orgDisambiguated);

}
