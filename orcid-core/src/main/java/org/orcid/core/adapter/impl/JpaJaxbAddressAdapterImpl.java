package org.orcid.core.adapter.impl;

import java.util.Collection;
import java.util.List;

import org.orcid.core.adapter.JpaJaxbAddressAdapter;
import org.orcid.jaxb.model.record_rc2.Address;
import org.orcid.jaxb.model.record_rc2.Addresses;
import org.orcid.persistence.jpa.entities.AddressEntity;

import ma.glasnost.orika.MapperFacade;

public class JpaJaxbAddressAdapterImpl implements JpaJaxbAddressAdapter {

    private MapperFacade mapperFacade;

    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

    @Override
    public AddressEntity toAddressEntity(Address address) {
        if (address == null) {
            return null;
        }
        return mapperFacade.map(address, AddressEntity.class);
    }

    @Override
    public Address toAddress(AddressEntity entity) {
        if (entity == null) {
            return null;
        }
        return mapperFacade.map(entity, Address.class);
    }

    @Override
    public Addresses toAddressList(Collection<AddressEntity> entities) {
        if (entities == null) {
            return null;
        }
        Addresses addresses = new Addresses();
        List<Address> addressList = mapperFacade.mapAsList(entities, Address.class);
        addresses.setAddress(addressList);
        return addresses;
    }

    @Override
    public AddressEntity toAddressEntity(Address address, AddressEntity existing) {
        if (address == null) {
            return null;
        }
        mapperFacade.map(address, existing);
        return existing;
    }

}
