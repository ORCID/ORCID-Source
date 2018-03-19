package org.orcid.core.adapter;

import java.util.Collection;

import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Addresses;
import org.orcid.persistence.jpa.entities.AddressEntity;

public interface JpaJaxbAddressAdapter {

    AddressEntity toAddressEntity(Address address);

    Address toAddress(AddressEntity entity);

    Addresses toAddressList(Collection<AddressEntity> entities);

    AddressEntity toAddressEntity(Address address, AddressEntity existing);
}
