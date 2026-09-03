package org.orcid.core.adapter.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.v3.release.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.v3.release.common.Organization;
import org.orcid.jaxb.model.v3.release.common.OrganizationAddress;
import org.orcid.persistence.jpa.entities.OrgEntity;

/**
 * Replaces the old Orika-only {@code org.orcid.core.adapter.v3.converter.OrgConverter}.
 * Plain, framework-free conversion logic - no Orika dependency.
 *
 * Converts OrgEntities to Organizations but not vice versa.
 */
@Mapper(componentModel = "spring")
public abstract class OrgMapperV3 {

    public static final OrgMapperV3 INSTANCE = Mappers.getMapper(OrgMapperV3.class);

    public OrgEntity convertTo(Organization source) {
        // incoming Organizations don't get converted to OrgEntities
        return null;
    }

    public Organization convertFrom(OrgEntity source) {
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
