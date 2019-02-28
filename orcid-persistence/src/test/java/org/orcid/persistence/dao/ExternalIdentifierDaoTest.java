package org.orcid.persistence.dao;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class ExternalIdentifierDaoTest extends DBUnitTest {

    private static String USER_ORCID = "0000-0000-0000-0003";
    private static String OTHER_USER_ORCID = "0000-0000-0000-0001";
    
    @Resource(name = "externalIdentifierDao")
    private ExternalIdentifierDao dao;

    @Resource
    private ProfileDao profileDao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ProfileEntityData.xml", "/data/SubjectEntityData.xml"));
    }

    @Test
    public void testRemoveExternalIdentifier() {
        Date now = new Date();
        Date justBeforeStart = new Date(now.getTime() - 1000);
        assertFalse(dao.removeExternalIdentifier("4444-4444-4444-4441", "d3clan"));
        assertFalse(dao.removeExternalIdentifier("4444-4444-4444-4443", "d3clan1"));
        assertTrue("Profile last modified should be updated", justBeforeStart.before(profileDao.retrieveLastModifiedDate("4444-4444-4444-4443")));
        assertTrue(dao.removeExternalIdentifier("4444-4444-4444-4443", "d3clan"));
    }
    
    @Test
    public void testGetExternalIdentifiers() {
        List<ExternalIdentifierEntity> extIds = dao.getExternalIdentifiers("4444-4444-4444-4442", 0L);
        assertNotNull(extIds);
        assertEquals(4, extIds.size());
        for(ExternalIdentifierEntity extId : extIds) {
            assertThat(extId.getId(), anyOf(is(2L), is(3L), is(4L), is(5L)));
            assertEquals("Facebook", extId.getExternalIdCommonName());
            assertNotNull(extId.getElementSourceId());
            if(extId.getId() == 2L) {
                assertEquals("http://www.facebook.com/abc123", extId.getExternalIdUrl());
                assertEquals("abc123", extId.getExternalIdReference());
                assertEquals("APP-5555555555555555", extId.getElementSourceId());
            } else if(extId.getId() == 3L) {
                assertEquals("http://www.facebook.com/abc456", extId.getExternalIdUrl());
                assertEquals("abc456", extId.getExternalIdReference());
                assertEquals("4444-4444-4444-4442", extId.getElementSourceId());
            } else if(extId.getId() == 4L) {
                assertEquals("http://www.facebook.com/abc789", extId.getExternalIdUrl());
                assertEquals("abc789", extId.getExternalIdReference());
                assertEquals("4444-4444-4444-4441", extId.getElementSourceId());
            } else {
                assertEquals(Long.valueOf(5), extId.getId());
                assertEquals("http://www.facebook.com/abc012", extId.getExternalIdUrl());
                assertEquals("abc012", extId.getExternalIdReference());
                assertEquals("APP-5555555555555555", extId.getElementSourceId());                
            }                                   
        }
    }
    
    @Test
    public void testGetExternalIdentifiersUsingVisibility() {
        List<ExternalIdentifierEntity> extIds = dao.getExternalIdentifiers("4444-4444-4444-4442", "LIMITED");
        assertNotNull(extIds);
        assertEquals(1, extIds.size());
        assertEquals(Long.valueOf(3), extIds.get(0).getId());
        
        extIds = dao.getExternalIdentifiers("4444-4444-4444-4442", "PUBLIC");
        assertNotNull(extIds);
        assertEquals(1, extIds.size());
        assertEquals(Long.valueOf(2), extIds.get(0).getId());                
    }
    
    @Test
    public void testGetExternalIdentifier() {
        ExternalIdentifierEntity extId = dao.getExternalIdentifierEntity("4444-4444-4444-4442", 3L);
        assertNotNull(extId);
        assertNotNull(extId.getDateCreated());
        assertNotNull(extId.getLastModified());
        assertEquals("Facebook", extId.getExternalIdCommonName());
        assertEquals("abc456", extId.getExternalIdReference());
        assertEquals("http://www.facebook.com/abc456", extId.getExternalIdUrl());
        assertEquals(Long.valueOf(3), extId.getId());        
        assertEquals("LIMITED", extId.getVisibility());
        assertNotNull(extId.getElementSourceId());
        assertEquals("4444-4444-4444-4442", extId.getElementSourceId());
    }
    
    @Test
    public void testGetExternalIdentifierThatDontBelongToThatUser() {
        try {
            dao.getExternalIdentifierEntity("4444-4444-4444-4441", 3L);
            fail();
        } catch(NoResultException nre) {
            
        }
        
        try {
            dao.getExternalIdentifierEntity("4444-4444-4444-4400", 3L);
            fail();
        } catch(NoResultException nre) {
            
        }
    }
    
    @Test
    public void removeAllTest() {
        long initialNumber = dao.countAll();
        long elementThatBelogsToUser = dao.getExternalIdentifiers(USER_ORCID, 0L).size();
        long otherUserElements = dao.getExternalIdentifiers(OTHER_USER_ORCID, 0L).size();
        assertEquals(5, elementThatBelogsToUser);
        assertTrue(elementThatBelogsToUser < initialNumber);
        assertEquals(3, otherUserElements);
        //Remove all elements that belongs to USER_ORCID
        dao.removeAllExternalIdentifiers(USER_ORCID);
        
        long finalNumberOfElements = dao.countAll();
        long finalNumberOfOtherUserElements = dao.getExternalIdentifiers(OTHER_USER_ORCID, 0L).size();
        long finalNumberOfElementsThatBelogsToUser = dao.getExternalIdentifiers(USER_ORCID, 0L).size();
        assertEquals(0, finalNumberOfElementsThatBelogsToUser);
        assertEquals(otherUserElements, finalNumberOfOtherUserElements);
        assertEquals((initialNumber - elementThatBelogsToUser), finalNumberOfElements);
    }
}
