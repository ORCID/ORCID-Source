package org.orcid.core.manager.v3.read_only.impl;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.adapter.v3.JpaJaxbAddressAdapter;
import org.orcid.core.manager.v3.read_only.AddressManagerReadOnly;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.Addresses;
import org.orcid.persistence.dao.AddressDao;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.springframework.cache.annotation.Cacheable;

public class AddressManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements AddressManagerReadOnly {
    
    @Resource
    protected JpaJaxbAddressAdapter adapter;
    
    protected AddressDao addressDao;
    
    public void setAddressDao(AddressDao addressDao) {
        this.addressDao = addressDao;
    }

    @Override
    @Cacheable(value = "primary-address", key = "#orcid.concat('-').concat(#lastModified)")
    public Address getPrimaryAddress(String orcid, long lastModified) {        
        List<AddressEntity> addresses = addressDao.getAddresses(orcid, getLastModified(orcid));
        Address address = null;
        if(addresses != null) {
            //Look for the address with the largest display index
            for(AddressEntity entity : addresses) {
                if(address == null || address.getDisplayIndex() < entity.getDisplayIndex()) {
                    address = adapter.toAddress(entity);                    
                } 
            }
        }                    
        return address;
    }

    @Override
    public Address getAddress(String orcid, Long putCode) {
        AddressEntity entity = addressDao.getAddress(orcid, putCode);
        return adapter.toAddress(entity);
    }
    
    @Override
    public Addresses getAddresses(String orcid) {
        List<AddressEntity> addresses = addressDao.getAddresses(orcid, getLastModified(orcid));
        return adapter.toAddressList(addresses);        
    }

    @Override
    public Addresses getPublicAddresses(String orcid) {
        List<AddressEntity> addresses = addressDao.getPublicAddresses(orcid, getLastModified(orcid));
        return adapter.toAddressList(addresses);        
    }
}
