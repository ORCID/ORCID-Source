package org.orcid.core.adapter.v3.converter;

import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.v3.release.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.v3.release.common.Organization;
import org.orcid.jaxb.model.v3.release.common.OrganizationAddress;
import org.orcid.persistence.jpa.entities.OrgEntity;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * Converter that converts OrgEntities to Organizations but not vice versa
 * @author georgenash
 *
 */
public class OrgConverter extends BidirectionalConverter<Organization, OrgEntity> {

    @Override
    public OrgEntity convertTo(Organization source, Type<OrgEntity> destinationType) {
        // incoming Organizations don't get converted to OrgEntities
        return null;
    }

    @Override
    public Organization convertFrom(OrgEntity source, Type<Organization> destinationType) {
        if (source != null) {
            Organization org = new Organization();
            OrganizationAddress address = new OrganizationAddress();
            org.setAddress(address);
            address.setCity(source.getCity() != null && !source.getCity().isEmpty() ? source.getCity() : null);
            address.setRegion(source.getRegion() != null && !source.getRegion().isEmpty() ? source.getRegion() : null);
            address.setCountry(source.getCountry() != null && !source.getCountry().isEmpty() ? Iso3166Country.fromValue(source.getCountry()) : null);
            org.setName(source.getName());

            if (source.getOrgDisambiguated() != null) {
                DisambiguatedOrganization disambiguated = new DisambiguatedOrganization();
                disambiguated.setDisambiguatedOrganizationIdentifier(source.getOrgDisambiguated().getSourceId());
                disambiguated.setDisambiguationSource(source.getOrgDisambiguated().getSourceType());
                disambiguated.setId(source.getOrgDisambiguated().getId());
                org.setDisambiguatedOrganization(disambiguated);
            }
            return org;
        }
        return null;
    }

   
}