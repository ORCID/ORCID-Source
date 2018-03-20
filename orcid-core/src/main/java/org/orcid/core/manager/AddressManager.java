package org.orcid.core.manager;

import org.orcid.core.manager.read_only.AddressManagerReadOnly;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Addresses;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface AddressManager extends AddressManagerReadOnly {    
    Address updateAddress(String orcid, Long putCode, Address address, boolean isApiRequest);

    Address createAddress(String orcid, Address address, boolean isApiRequest);

    boolean deleteAddress(String orcid, Long putCode);
    
    Addresses updateAddresses(String orcid, Addresses addresses);
    
    /**
     * Removes all address that belongs to a given record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all address will be
     *            removed.
     */
    void removeAllAddress(String orcid);
}
