package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.jaxb.model.record_v2.Person;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class PersonDetailsManagerTest extends DBUnitTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/RecordNameEntityData.xml",
            "/data/BiographyEntityData.xml");

    private static final String ORCID = "0000-0000-0000-0003";
    
    @Resource
    private PersonDetailsManager personDetailsManager;

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
    public void testGetPersonDetails() {
        Person person = personDetailsManager.getPersonDetails(ORCID);
        assertNotNull(person);
        
        assertNotNull(person.getExternalIdentifiers());
        assertNotNull(person.getExternalIdentifiers().getExternalIdentifiers());
        assertEquals(5, person.getExternalIdentifiers().getExternalIdentifiers().size());
        
        assertNotNull(person.getResearcherUrls());
        assertNotNull(person.getResearcherUrls().getResearcherUrls());
        assertEquals(5, person.getResearcherUrls().getResearcherUrls().size());
        
        assertNotNull(person.getOtherNames());
        assertNotNull(person.getOtherNames().getOtherNames());
        assertEquals(5, person.getOtherNames().getOtherNames().size());
        
        assertNotNull(person.getAddresses());
        assertNotNull(person.getAddresses().getAddress());
        assertEquals(5, person.getAddresses().getAddress().size());
        
        assertNotNull(person.getKeywords());
        assertNotNull(person.getKeywords().getKeywords());
        assertEquals(5, person.getKeywords().getKeywords().size());
        
        assertNotNull(person.getEmails());
        assertNotNull(person.getEmails().getEmails());
        assertEquals(4, person.getEmails().getEmails().size());
        
        for (Email email : person.getEmails().getEmails()) {
            assertTrue(email.isVerified());
        }
        
        assertNotNull(person.getBiography());
        assertEquals(Visibility.PUBLIC, person.getBiography().getVisibility());
        assertEquals("Biography for 0000-0000-0000-0003", person.getBiography().getContent());
        
        assertNotNull(person.getName());    
        assertNotNull(person.getName().getCreditName());
        assertEquals("Credit Name", person.getName().getCreditName().getContent());
        assertNotNull(person.getName().getFamilyName());
        assertEquals("Family Name", person.getName().getFamilyName().getContent());
        assertNotNull(person.getName().getGivenNames());
        assertEquals("Given Names", person.getName().getGivenNames().getContent());
        assertEquals(Visibility.PUBLIC, person.getName().getVisibility());        
    }

    @Test
    public void testGetPublicPersonDetails() {
        Person person = personDetailsManager.getPublicPersonDetails(ORCID);
        assertNotNull(person);
        
        assertNotNull(person.getExternalIdentifiers());
        assertNotNull(person.getExternalIdentifiers().getExternalIdentifiers());
        assertEquals(1, person.getExternalIdentifiers().getExternalIdentifiers().size());
        assertEquals(Long.valueOf(13), person.getExternalIdentifiers().getExternalIdentifiers().get(0).getPutCode());
        
        assertNotNull(person.getResearcherUrls());
        assertNotNull(person.getResearcherUrls().getResearcherUrls());
        assertEquals(1, person.getResearcherUrls().getResearcherUrls().size());
        assertEquals(Long.valueOf(13), person.getResearcherUrls().getResearcherUrls().get(0).getPutCode());
        
        assertNotNull(person.getOtherNames());
        assertNotNull(person.getOtherNames().getOtherNames());
        assertEquals(1, person.getOtherNames().getOtherNames().size());
        assertEquals(Long.valueOf(13), person.getOtherNames().getOtherNames().get(0).getPutCode());
        
        assertNotNull(person.getAddresses());
        assertNotNull(person.getAddresses().getAddress());
        assertEquals(1, person.getAddresses().getAddress().size());
        assertEquals(Long.valueOf(9), person.getAddresses().getAddress().get(0).getPutCode());
        
        assertNotNull(person.getKeywords());
        assertNotNull(person.getKeywords().getKeywords());
        assertEquals(1, person.getKeywords().getKeywords().size());
        assertEquals(Long.valueOf(9), person.getKeywords().getKeywords().get(0).getPutCode());
        
        assertNotNull(person.getEmails());
        assertNotNull(person.getEmails().getEmails());
        assertEquals(1, person.getEmails().getEmails().size());
        assertEquals("public_0000-0000-0000-0003@test.orcid.org", person.getEmails().getEmails().get(0).getEmail());
        
        assertNotNull(person.getBiography());
        assertEquals(Visibility.PUBLIC, person.getBiography().getVisibility());
        assertEquals("Biography for 0000-0000-0000-0003", person.getBiography().getContent());
        
        assertNotNull(person.getName());    
        assertNotNull(person.getName().getCreditName());
        assertEquals("Credit Name", person.getName().getCreditName().getContent());
        assertNotNull(person.getName().getFamilyName());
        assertEquals("Family Name", person.getName().getFamilyName().getContent());
        assertNotNull(person.getName().getGivenNames());
        assertEquals("Given Names", person.getName().getGivenNames().getContent());
        assertEquals(Visibility.PUBLIC, person.getName().getVisibility());
    }
}
