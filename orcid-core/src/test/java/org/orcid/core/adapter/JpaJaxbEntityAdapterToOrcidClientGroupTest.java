/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.adapter;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.utils.OrcidJaxbCopyUtilsTest;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileWorkEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.test.DBUnitTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Will Simpson
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class JpaJaxbEntityAdapterToOrcidClientGroupTest extends DBUnitTest {

    public static final String CLIENT_GROUP = "/orcid-client-group-for-db-unit-data.xml";

    @Autowired
    private GenericDao<ProfileEntity, String> profileDao;

    @Autowired
    private JpaJaxbEntityAdapter adapter;

    private Unmarshaller unmarshaller;
    private OrcidMessage orcidMessageV20;   
    
    public JpaJaxbEntityAdapterToOrcidClientGroupTest() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(OrcidMessage.class);
        unmarshaller = context.createUnmarshaller();
        orcidMessageV20 = getOrcidMessage("/orcid-public-full-v20.xml");        
    }
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml",
                "/data/ProfileWorksEntityData.xml", "/data/ClientDetailsEntityData.xml"), null);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ClientDetailsEntityData.xml", "/data/ProfileWorksEntityData.xml", "/data/WorksEntityData.xml",
                "/data/ProfileEntityData.xml", "/data/SecurityQuestionEntityData.xml"), null);
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testToOrcidProfile() throws IOException {
        ProfileEntity profileEntity = profileDao.find("4444-4444-4444-4446");
        OrcidClientGroup orcidClientGroup = adapter.toOrcidClientGroup(profileEntity);
        assertNotNull(orcidClientGroup);
        String expected = IOUtils.toString(getClass().getResourceAsStream(CLIENT_GROUP), "UTF-8");
        //assertEquals(expected, orcidClientGroup.toString());
    }
    
    @Test
    public void testToProfileEntity() throws Exception {        
        ProfileEntity profileEntity = adapter.toProfileEntity(orcidMessageV20.getOrcidProfile(), new ProfileEntity());
        assertNotNull(profileEntity);
        assertNotNull(profileEntity.getSource());
        assertEquals("8888-8888-8888-8880", profileEntity.getSource().getId());
        assertEquals("Josiah",profileEntity.getGivenNames());
        assertEquals(3, profileEntity.getResearcherUrls().size());
        assertEquals(3, profileEntity.getProfileWorks().size());
        Set<ProfileWorkEntity> profileWorks = profileEntity.getProfileWorks();        
        for(ProfileWorkEntity profileWork : profileWorks){
            WorkEntity work = profileWork.getWork();
            if(work.getTitle().equals("Work title 1")){
                assertEquals("Journal Title # 1", work.getJournalTitle());
            } else if(work.getTitle().equals("Work title 2")){
                assertEquals("Journal Title # 2", work.getJournalTitle());
            } else if(work.getTitle().equals("Work Title 3")){
                assertNull(work.getJournalTitle());
            } else {
                fail();
            }
        }
        
    }
    
    private OrcidMessage getOrcidMessage(String s) throws JAXBException {
        InputStream inputStream = OrcidJaxbCopyUtilsTest.class.getResourceAsStream(s);
        return (OrcidMessage) unmarshaller.unmarshal(inputStream);
    }
}
