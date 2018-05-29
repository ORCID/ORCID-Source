package org.orcid.core.manager.read_only.impl;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.adapter.JpaJaxbAddressAdapter;
import org.orcid.core.manager.read_only.AddressManagerReadOnly;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Addresses;
import org.orcid.persistence.dao.AddressDao;
import org.orcid.persistence.jpa.entities.AddressEntity;

public class AddressManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements AddressManagerReadOnly {
    
    @Resource
    protected JpaJaxbAddressAdapter adapter;
    
    protected AddressDao addressDao;
    
    public void setAddressDao(AddressDao addressDao) {
        this.addressDao = addressDao;
    }

    @Override
    public Address getPrimaryAddress(String orcid) {        
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
        List<AddressEntity> addresses = addressDao.getAddresses(orcid, Visibility.PUBLIC.name());
        return adapter.toAddressList(addresses);        
    }
    
}
