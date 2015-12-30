package org.orcid.core.manager.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.orcid.core.adapter.JpaJaxbAddressAdapter;
import org.orcid.core.exception.ApplicationException;
import org.orcid.core.exception.OrcidDuplicatedElementException;
import org.orcid.core.manager.AddressManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.validator.PersonValidator;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.record_rc2.Address;
import org.orcid.persistence.dao.AddressDao;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;

public class AddressManagerImpl implements AddressManager {

    @Resource
    private AddressDao addressDao;
    
    @Resource
    private JpaJaxbAddressAdapter adapter;
    
    @Resource
    private OrcidSecurityManager orcidSecurityManager;
    
    @Resource
    private SourceManager sourceManager;
    
    @Override
    public Address getPrimaryAddress(String orcid) {
        List<AddressEntity> addresses = addressDao.findByOrcid(orcid);
        Address address = null;
        if(addresses != null) {
            for(AddressEntity entity : addresses) {
                if(entity.getPrimary()) {
                    address = adapter.toAddress(entity);
                    break;
                }
            }
        }                    
        return address;
    }

    @Override
    public Address getAddress(String orcid, Long putCode) {
        AddressEntity entity = addressDao.find(orcid, putCode);
        return adapter.toAddress(entity);
    }
    
    @Override
    @Transactional
    public Address updateAddress(String orcid, Long putCode, Address address, boolean isUserUpdating) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();

        // Validate the address
        PersonValidator.validateAddress(address, sourceEntity, false);
        // Validate it is not duplicated
        List<AddressEntity> existingAddresses = addressDao.findByOrcid(orcid);
        for (AddressEntity existing : existingAddresses) {
            //If it is not the same element
            if(!existing.getId().equals(address.getPutCode())) {
                if (isDuplicated(existing, address, sourceEntity)) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("type", "address");
                    params.put("value", address.getCountry().getValue().value());
                    throw new OrcidDuplicatedElementException(params);
                }
            }
        }

        AddressEntity updatedEntity = addressDao.find(putCode);
        if (updatedEntity == null) {
            throw new ApplicationException();
        }

        
        SourceEntity existingSource = updatedEntity.getSource();
        //If it is an update from the API, check the source and preserve the original visibility
        if(!isUserUpdating) {
            orcidSecurityManager.checkSource(existingSource);
            Visibility originalVisibility = Visibility.fromValue(updatedEntity.getVisibility().value());
            updatedEntity.setVisibility(originalVisibility);
        }        
        adapter.toAddressEntity(address, updatedEntity);
        updatedEntity.setLastModified(new Date());        
        updatedEntity.setSource(existingSource);
        addressDao.merge(updatedEntity);
        return adapter.toAddress(updatedEntity);
    }
    
    @Override
    @Transactional
    public Address createAddress(String orcid, Address address) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        // Validate the address
        PersonValidator.validateAddress(address, sourceEntity, true);
        // Validate it is not duplicated
        List<AddressEntity> existingAddresses = addressDao.findByOrcid(orcid);
        for (AddressEntity existing : existingAddresses) {
            if (isDuplicated(existing, address, sourceEntity)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "address");
                params.put("value", address.getCountry().getValue().value());
                throw new OrcidDuplicatedElementException(params);
            }
        }

        AddressEntity newEntity = adapter.toAddressEntity(address);
        ProfileEntity profile = new ProfileEntity(orcid);
        newEntity.setUser(profile);
        newEntity.setDateCreated(new Date());
        newEntity.setSource(sourceEntity);        
        setIncomingPrivacy(newEntity, profile);
        addressDao.persist(newEntity);
        return adapter.toAddress(newEntity);
    }

    @Override
    @Transactional
    public boolean deleteAddress(String orcid, Long putCode) {
        AddressEntity entity = addressDao.find(orcid, putCode);
        SourceEntity existingSource = entity.getSource();
        orcidSecurityManager.checkSource(existingSource);

        try {
            addressDao.remove(entity);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean isDuplicated(AddressEntity existing, Address address, SourceEntity source) {
        //If they have the same source
        if(existing.getSource() != null && existing.getSource().getSourceId().equals(source.getSourceId())) {
            if(existing.getIso2Country().equals(address.getCountry().getValue())) {
                return true;
            }
        }
        return false;
    }    
    
    private void setIncomingPrivacy(AddressEntity entity, ProfileEntity profile) {
        Visibility incomingCountryVisibility = entity.getVisibility();
        Visibility defaultCountryVisibility = profile.getProfileAddressVisibility() == null
                ? Visibility.fromValue(OrcidVisibilityDefaults.COUNTRY_DEFAULT.getVisibility().value())
                : org.orcid.jaxb.model.common.Visibility.fromValue(profile.getProfileAddressVisibility().value());
        if (profile.getClaimed() != null && profile.getClaimed()) {
            if (defaultCountryVisibility.isMoreRestrictiveThan(incomingCountryVisibility)) {
                entity.setVisibility(defaultCountryVisibility);
            }
        } else if (incomingCountryVisibility == null) {
            entity.setVisibility(org.orcid.jaxb.model.common.Visibility.PRIVATE);
        }
    }
}
