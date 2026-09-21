package org.orcid.core.adapter.mapstruct.v3.impl;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.v3.JpaJaxbAddressAdapter;
import org.orcid.core.adapter.mapstruct.SourceMapperV3;
import org.orcid.core.adapter.mapstruct.VisibilityMapperV3;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.Addresses;
import org.orcid.persistence.jpa.entities.AddressEntity;

@Mapper(
    componentModel = "spring", 
    uses = {SourceMapperV3.class, VisibilityMapperV3.class}
)
public abstract class JpaJaxbAddressAdapterImpl implements JpaJaxbAddressAdapter {

    @Override
    public AddressEntity toAddressEntity(Address address) {
        if (address == null) {
            return null;
        }
        
        AddressEntity result = mapToEntity(address);
        
        // Preserve original logic: default display index to 0 for new entities
        if (result.getDisplayIndex() == null) {
            result.setDisplayIndex(0L);
        }
        
        return result;
    }

    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "country.value", target = "iso2Country")
    // Orika mapped these using fieldBToA (Database -> API only)
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "displayIndex", ignore = true)
    protected abstract AddressEntity mapToEntity(Address address);

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "iso2Country", target = "country.value")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = ".", target = "source")
    public abstract Address toAddress(AddressEntity entity);

    @Override
    public Addresses toAddressList(Collection<AddressEntity> entities) {
        if (entities == null) {
            return null;
        }
        
        Addresses addresses = new Addresses();
        // MapStruct generates the list iteration method below
        addresses.setAddress(toAddressListInternal(entities));
        return addresses;
    }

    protected abstract List<Address> toAddressListInternal(Collection<AddressEntity> entities);

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "country.value", target = "iso2Country")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "displayIndex", ignore = true)
    public abstract AddressEntity toAddressEntity(Address address, @MappingTarget AddressEntity existing);

}