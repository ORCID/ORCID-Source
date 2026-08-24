package org.orcid.core.adapter.impl.mapstruct;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.JpaJaxbAddressAdapter;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Addresses;
import org.orcid.persistence.jpa.entities.AddressEntity;

@Mapper(componentModel = "spring")
public abstract class JpaJaxbAddressAdapterImpl implements JpaJaxbAddressAdapter {

    /**
     * Ensure displayIndex is ONLY defaulted during new entity creation
     */
    @Override
    public AddressEntity toAddressEntity(Address address) {
        if (address == null) {
            return null;
        }
        
        // Delegate to MapStruct for the actual mapping
        AddressEntity result = mapToAddressEntity(address);
        
        // Set display index
        if (result.getDisplayIndex() == null) {
            result.setDisplayIndex(0L);
        }
        
        return result;
    }

    /**
     * Internal method for MapStruct to generate the creation logic.
     */
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "country.value", target = "iso2Country")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "displayIndex", ignore = true)
    protected abstract AddressEntity mapToAddressEntity(Address address);

    /**
     * Maps Database AddressEntity to an Address Object.
     */
    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "iso2Country", target = "country.value")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    public abstract Address toAddress(AddressEntity entity);

    /**
     * Custom wrapper mapping for the collection. 
     */
    @Override
    public Addresses toAddressList(Collection<AddressEntity> entities) {
        if (entities == null) {
            return null;
        }
        Addresses addresses = new Addresses();
        addresses.setAddress(toAddressListInternal(entities));
        return addresses;
    }

    /**
     * MapStruct automatically generates the iteration logic.
     */
    protected abstract List<Address> toAddressListInternal(Collection<AddressEntity> entities);

    /**
     * Updates an existing Database AddressEntity from an Address Object.
     */
    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "country.value", target = "iso2Country")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "displayIndex", ignore = true)
    public abstract AddressEntity toAddressEntity(Address address, @MappingTarget AddressEntity existing);
}