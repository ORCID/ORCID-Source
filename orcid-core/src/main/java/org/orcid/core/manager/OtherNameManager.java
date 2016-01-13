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
package org.orcid.core.manager;

import java.util.List;

import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.persistence.jpa.entities.OtherNameEntity;

public interface OtherNameManager {
    /**
     * Get other names for an specific orcid account
     * @param orcid          
     * @return
     *           The list of other names related with the specified orcid profile
     * */
    public List<OtherNameEntity> getOtherName(String orcid);

    /**
     * Update other name entity with new values
     * @param otherName
     * @return
     *          true if the other name was sucessfully updated, false otherwise
     * */
    public boolean updateOtherName(OtherNameEntity otherName);

    /**
     * Create other name for the specified account
     * @param orcid
     * @param displayName
     * @return
     *          true if the other name was successfully created, false otherwise 
     * */
    public boolean addOtherName(String orcid, String displayName);

    /**
     * Delete other name from database
     * @param otherName
     * @return 
     *          true if the other name was successfully deleted, false otherwise
     * */
    public boolean deleteOtherName(OtherNameEntity otherName);

    /**
     * Get a list of other names and decide which other names might be deleted,
     * and which ones should be created
     * @param orcid
     * @param otherNames
     * */
    public void updateOtherNames(String orcid, OtherNames otherNames);

    org.orcid.jaxb.model.record_rc2.OtherNames getOtherNamesV2(String orcid);
    
    org.orcid.jaxb.model.record_rc2.OtherNames getPublicOtherNamesV2(String orcid);
    
    org.orcid.jaxb.model.record_rc2.OtherNames getMinimizedOtherNamesV2(String orcid);
    
    org.orcid.jaxb.model.record_rc2.OtherName getOtherNameV2(String orcid, Long putCode);

    boolean deleteOtherNameV2(String orcid, Long putCode);

    org.orcid.jaxb.model.record_rc2.OtherName createOtherNameV2(String orcid, org.orcid.jaxb.model.record_rc2.OtherName otherName);

    org.orcid.jaxb.model.record_rc2.OtherName updateOtherNameV2(String orcid, Long putCode, org.orcid.jaxb.model.record_rc2.OtherName otherName);
    
    org.orcid.jaxb.model.record_rc2.OtherNames updateOtherNamesV2(String orcid, org.orcid.jaxb.model.record_rc2.OtherNames otherNames, org.orcid.jaxb.model.common_rc2.Visibility defaultVisiblity);
}
