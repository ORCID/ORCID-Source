/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager.v3;

import org.orcid.core.manager.v3.read_only.AddressManagerReadOnly;
import org.orcid.jaxb.model.v3.dev1.record.Address;
import org.orcid.jaxb.model.v3.dev1.record.Addresses;

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
