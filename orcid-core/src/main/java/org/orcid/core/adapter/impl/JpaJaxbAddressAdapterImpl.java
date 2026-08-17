package org.orcid.core.adapter.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.orcid.core.adapter.JpaJaxbAddressAdapter;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Addresses;
import org.orcid.persistence.jpa.entities.AddressEntity;

public class JpaJaxbAddressAdapterImpl implements JpaJaxbAddressAdapter {

    private Object mapperFacade;

    public void setMapperFacade(Object mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

    @Override
    public AddressEntity toAddressEntity(Address address) {
        if (address == null) {
            return null;
        }
        AddressEntity result = map(address, AddressEntity.class);
        if(result.getDisplayIndex() == null) {
            result.setDisplayIndex(0L);
        }
        return result;
    }

    @Override
    public Address toAddress(AddressEntity entity) {
        if (entity == null) {
            return null;
        }
        return map(entity, Address.class);
    }

    @Override
    public Addresses toAddressList(Collection<AddressEntity> entities) {
        if (entities == null) {
            return null;
        }
        Addresses addresses = new Addresses();
        List<Address> addressList = mapAsList(entities, Address.class);
        addresses.setAddress(addressList);
        return addresses;
    }

    @Override
    public AddressEntity toAddressEntity(Address address, AddressEntity existing) {
        if (address == null) {
            return null;
        }
        map(address, existing);
        return existing;
    }

    private <S, D> D map(S source, Class<D> destinationClass) {
        return destinationClass.cast(invoke("map", new Class<?>[] { Object.class, Class.class }, source, destinationClass));
    }

    private <S, D> D map(S source, D destinationObject) {
        return cast(invoke("map", new Class<?>[] { Object.class, Object.class }, source, destinationObject));
    }

    private <S, D> List<D> mapAsList(Collection<S> source, Class<D> destinationClass) {
        if (source.isEmpty()) {
            return Collections.emptyList();
        }
        // Avoid direct dependency on Orika types while preserving configured mapper behavior.
        Object mapped = invoke("mapAsList", new Class<?>[] { Iterable.class, Class.class }, source, destinationClass);
        if (mapped instanceof List<?>) {
            return cast(mapped);
        }
        return source.stream().map(item -> map(item, destinationClass)).collect(Collectors.toList());
    }

    private Object invoke(String methodName, Class<?>[] parameterTypes, Object... args) {
        if (mapperFacade == null) {
            throw new IllegalStateException("Mapper facade has not been set");
        }
        try {
            return mapperFacade.getClass().getMethod(methodName, parameterTypes).invoke(mapperFacade, args);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to invoke mapper facade method: " + methodName, e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T cast(Object value) {
        return (T) value;
    }

}
