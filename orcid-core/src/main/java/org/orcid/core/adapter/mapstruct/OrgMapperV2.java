package org.orcid.core.adapter.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.orcid.jaxb.model.common_v2.DisambiguatedOrganization;
import org.orcid.jaxb.model.common_v2.Iso3166Country;
import org.orcid.jaxb.model.common_v2.Organization;
import org.orcid.jaxb.model.common_v2.OrganizationAddress;
import org.orcid.persistence.jpa.entities.OrgEntity;

/**
 * Replaces inline org field mappings for V2 MapStruct adapters.
 */
@Mapper(componentModel = "spring")
public abstract class OrgMapperV2 {

    public static final OrgMapperV2 INSTANCE = Mappers.getMapper(OrgMapperV2.class);

    public OrgEntity convertTo(Organization source) {
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