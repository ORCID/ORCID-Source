package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.PersonalDetails;

/**
* 
* @author Angel Montenegro
* 
*/
public class PersonalDetailsManagerTest extends BaseTest {

    private static final List<String> DATA_FILES = Arrays.asList(
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");
    
    private static final String ORCID1 = "0000-0000-0000-0001";
    private static final String ORCID2 = "0000-0000-0000-0002";
    private static final String ORCID3 = "0000-0000-0000-0003";
    
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
    public void getPersonalDetailsTest1() {
        PersonalDetails personalDetails = personalDetailsManager.getPersonalDetails(ORCID1);
        assertNotNull(personalDetails);
        assertNotNull(personalDetails.getOtherNames());        
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        assertEquals(3, personalDetails.getOtherNames().getOtherNames().size());
        boolean found1 = false, found2 = false, found3 = false;
        for(OtherName otherName : personalDetails.getOtherNames().getOtherNames()){
            long putCode = otherName.getPutCode();
            if(putCode == 18) {
                found1 = true;
            } else if(putCode == 19) {
                found2 = true;
            } else if(putCode == 20) {
                found3 = true;
            } else {
                fail("Invalid put code found " + putCode);
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertNotNull(personalDetails.getBiography());
        assertEquals(Visibility.PRIVATE, personalDetails.getBiography().getVisibility());
        assertEquals("Biography for 0000-0000-0000-0001", personalDetails.getBiography().getContent());
        assertNotNull(personalDetails.getName());        
        assertEquals("Leonardo", personalDetails.getName().getCreditName().getContent());
        assertEquals("da Vinci", personalDetails.getName().getFamilyName().getContent());
        assertEquals("Leonardo", personalDetails.getName().getGivenNames().getContent());
        assertEquals(Visibility.PRIVATE, personalDetails.getName().getVisibility());
    }

    @Test
    public void getPublicPersonalDetailsTest() {
        PersonalDetails personalDetails = personalDetailsManager.getPublicPersonalDetails(ORCID1);
        assertNotNull(personalDetails);
        assertNotNull(personalDetails.getOtherNames());        
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        assertEquals(1, personalDetails.getOtherNames().getOtherNames().size());
        assertEquals(Long.valueOf(18), personalDetails.getOtherNames().getOtherNames().get(0).getPutCode());
        assertNull(personalDetails.getBiography());        
        assertNull(personalDetails.getName());
    }
    
    @Test
    public void getPersonalDetailsTest2() {
        PersonalDetails personalDetails = personalDetailsManager.getPersonalDetails(ORCID2);
        assertNotNull(personalDetails);
        assertNotNull(personalDetails.getOtherNames());
        assertTrue(personalDetails.getOtherNames().getOtherNames().isEmpty());
        assertNotNull(personalDetails.getBiography());
        assertEquals(Visibility.LIMITED, personalDetails.getBiography().getVisibility());
        assertEquals("Biography for 0000-0000-0000-0002", personalDetails.getBiography().getContent());
        assertNotNull(personalDetails.getName());        
        assertEquals("Credit Name", personalDetails.getName().getCreditName().getContent());
        assertEquals("Family Name", personalDetails.getName().getFamilyName().getContent());
        assertEquals("Given Names", personalDetails.getName().getGivenNames().getContent());
        assertEquals(Visibility.LIMITED, personalDetails.getName().getVisibility());
    }
    
    @Test
    public void getPublicPersonalDetailsTest2() {
        PersonalDetails personalDetails = personalDetailsManager.getPublicPersonalDetails(ORCID2);
        assertNotNull(personalDetails);
        assertNotNull(personalDetails.getOtherNames());
        assertTrue(personalDetails.getOtherNames().getOtherNames().isEmpty());
        assertNull(personalDetails.getBiography());        
        assertNull(personalDetails.getName());
    } 
    
    @Test
    public void getPersonalDetailsTest3() {
        PersonalDetails personalDetails = personalDetailsManager.getPersonalDetails(ORCID3);
        assertNotNull(personalDetails);
        assertNotNull(personalDetails.getOtherNames());        
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        assertEquals(5, personalDetails.getOtherNames().getOtherNames().size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false, found5 = false;
        for(OtherName otherName : personalDetails.getOtherNames().getOtherNames()){
            long putCode = otherName.getPutCode();
            if(putCode == 13) {
                found1 = true;
            } else if(putCode == 14) {
                found2 = true;
            } else if(putCode == 15) {
                found3 = true;
            } else if(putCode == 16) {
                found4 = true;
            } else if(putCode == 17) {
                found5 = true;
            } else {
                fail("Invalid put code found " + putCode);
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        assertTrue(found5);
        assertNotNull(personalDetails.getBiography());
        assertEquals(Visibility.PUBLIC, personalDetails.getBiography().getVisibility());
        assertEquals("Biography for 0000-0000-0000-0003", personalDetails.getBiography().getContent());
        assertNotNull(personalDetails.getName());        
        assertEquals("Credit Name", personalDetails.getName().getCreditName().getContent());
        assertEquals("Family Name", personalDetails.getName().getFamilyName().getContent());
        assertEquals("Given Names", personalDetails.getName().getGivenNames().getContent());
        assertEquals(Visibility.PUBLIC, personalDetails.getName().getVisibility());
    }

    @Test
    public void getPublicPersonalDetailsTest3() {
        PersonalDetails personalDetails = personalDetailsManager.getPublicPersonalDetails(ORCID3);
        assertNotNull(personalDetails);
        assertNotNull(personalDetails.getOtherNames());        
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        assertEquals(1, personalDetails.getOtherNames().getOtherNames().size());
        assertEquals(Long.valueOf(13), personalDetails.getOtherNames().getOtherNames().get(0).getPutCode());
        assertNotNull(personalDetails.getBiography());
        assertEquals(Visibility.PUBLIC, personalDetails.getBiography().getVisibility());
        assertEquals("Biography for 0000-0000-0000-0003", personalDetails.getBiography().getContent());
        assertNotNull(personalDetails.getName());        
        assertEquals("Credit Name", personalDetails.getName().getCreditName().getContent());
        assertEquals("Family Name", personalDetails.getName().getFamilyName().getContent());
        assertEquals("Given Names", personalDetails.getName().getGivenNames().getContent());
        assertEquals(Visibility.PUBLIC, personalDetails.getName().getVisibility());
    }
}
