package org.orcid.core.adapter.v3;

import java.util.Collection;

import org.orcid.jaxb.model.v3.rc2.record.Address;
import org.orcid.jaxb.model.v3.rc2.record.Addresses;
import org.orcid.persistence.jpa.entities.AddressEntity;

public interface JpaJaxbAddressAdapter {

    AddressEntity toAddressEntity(Address address);

    Address toAddress(AddressEntity entity);

    Addresses toAddressList(Collection<AddressEntity> entities);

    AddressEntity toAddressEntity(Address address, AddressEntity existing);
}
