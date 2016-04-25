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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.record_rc2.PersonalDetails;

/**
* 
* @author Angel Montenegro
* 
*/
public class PersonalDetailsManagerTest extends BaseTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", 
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml");
    
    private static final String ORCID="4444-4444-4444-4443";
    
    @Resource
    PersonalDetailsManager personalDetailsManager;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }
    
    @Test
    public void getPersonalDetailsTest() {
        PersonalDetails personalDetails = personalDetailsManager.getPersonalDetails(ORCID);
        assertNotNull(personalDetails);
        assertNotNull(personalDetails.getOtherNames());        
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        assertEquals(2, personalDetails.getOtherNames().getOtherNames().size());
        assertNotNull(personalDetails.getBiography());
        assertEquals(Visibility.LIMITED, personalDetails.getBiography().getVisibility());
        assertEquals("Richard Henry Sellers, CBE (8 September 1925 – 24 July 1980), known as Peter Sellers, was a British film actor, comedian and singer.", personalDetails.getBiography().getContent());
        assertNotNull(personalDetails.getName());        
        assertEquals("P. Sellers III", personalDetails.getName().getCreditName().getContent());
        assertEquals("Sellers", personalDetails.getName().getFamilyName().getContent());
        assertEquals("Peter", personalDetails.getName().getGivenNames().getContent());
        assertEquals(Visibility.LIMITED, personalDetails.getName().getVisibility());
    }

    @Test
    public void getPublicPersonalDetailsTest() {
        PersonalDetails personalDetails = personalDetailsManager.getPublicPersonalDetails(ORCID);
        assertNotNull(personalDetails);
        assertNotNull(personalDetails.getOtherNames());        
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        assertEquals(2, personalDetails.getOtherNames().getOtherNames().size());
        assertNull(personalDetails.getBiography());
        assertNull(personalDetails.getName());
    }    
}
