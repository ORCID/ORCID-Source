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

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.IdentifierType;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.core.BaseTest;
import org.orcid.core.manager.impl.*;
import org.springframework.test.context.ContextConfiguration;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(inheritInitializers = false, inheritLocations = false, locations = { "classpath:orcid-core-context.xml" })
public class IdentifierTypeManagerTest extends BaseTest{

    private static final String CLIENT_1_ID = "4444-4444-4444-4498";

    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml");

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }
    
    @Mock
    private SourceManager sourceManager;
    
    @Resource
    private IdentifierTypeManager idTypeMan;
    
    @Before
    public void before() throws Exception {
        idTypeMan.setSourceManager(sourceManager);
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));   
    }
    
    @Test
    public void testCreateIdentifier(){
        IdentifierType id = idTypeMan.createIdentifierType(createIdentifierType(1));
        assertNotNull(id);
        assertNotNull(id.getPutCode());
        assertTrue(new Date().after(id.getDateCreated()));
    }
    
    @Test
    public void testFetchIdentifier(){
        IdentifierType id = idTypeMan.fetchIdentifierTypeByName("name1");
        assertEquals("name1",id.getName());
    }

    @Test
    public void testUpdateIdentifier() throws InterruptedException{
        IdentifierType id = idTypeMan.fetchIdentifierTypeByName("name1");
        Date last = id.getLastModified();
        id.setValidationRegex("test");
        
        id = idTypeMan.updateIdentifierType(id);
        assertTrue(last.before(id.getLastModified()));  
        
        id = idTypeMan.fetchIdentifierTypeByName("name1");
        assertEquals("name1",id.getName());
        assertEquals("test",id.getValidationRegex());
        assertTrue(last.before(id.getLastModified()));        
    }

    @Test
    public void testFetchEntities(){
        Map<String,IdentifierType> map = idTypeMan.fetchIdentifierTypes();
        assertEquals(1, map.size());
        assertTrue(map.containsKey("name1"));
        assertEquals("name1",map.get("name1").getName());
        assertNotNull(map.get("name1").getSourceClient());
        assertNotNull(map.get("name1").getPutCode());
        IdentifierType id = idTypeMan.createIdentifierType(createIdentifierType(2));
        map = idTypeMan.fetchIdentifierTypes();
        assertEquals(2, map.size());
        assertTrue(map.containsKey("name1"));
        assertTrue(map.containsKey("name2"));
        assertEquals("name2",map.get("name2").getName());
    }
    

    private IdentifierType createIdentifierType(int seed){
        IdentifierType id = new IdentifierType();        
        id.setName("name"+seed);
        id.setDeprecated(true);
        id.setResolutionPrefix("prefix"+seed);
        id.setValidationRegex("validation"+seed);   
        id.setDateCreated(new Date(10,10,10));
        id.setLastModified(new Date(11,11,11));
        ClientDetailsEntity client = new ClientDetailsEntity();
        client.setClientName(CLIENT_1_ID);
        id.setSourceClient(client);
        return id;
    }
    
}
