package org.orcid.core.manager;

import org.orcid.jaxb.model.record_rc2.Address;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface AddressManager {
    Address getPrimaryAddress(String orcid);
    
    Address getAddress(String orcid, Long putCode);

    Address updateAddress(String orcid, Long putCode, Address address, boolean isUserUpdating);

    Address createAddress(String orcid, Address address);

    boolean deleteAddress(String orcid, Long putCode);
}
