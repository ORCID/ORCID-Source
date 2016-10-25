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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.orcid.core.BaseTest;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.IdentifierTypeEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.IdentifierType;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IdentifierTypeManagerTest extends BaseTest{

    private static final String CLIENT_1_ID = "APP-6666666666666666";

    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml");

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }
    
    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(DATA_FILES);
    }
    
    @Mock
    private SourceManager sourceManager;
    
    @Mock
    private OrcidSecurityManager securityManager;
    
    @Resource
    private IdentifierTypeManager idTypeMan;
    
    private List<String> v2Ids = Arrays.asList(new String[]{"pdb","kuid"});
    
    @Before
    public void before() throws Exception {
        idTypeMan.setSourceManager(sourceManager);
        idTypeMan.setSecurityManager(securityManager);
        doNothing().when(securityManager).checkSource(Matchers.any(IdentifierTypeEntity.class));
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));   
    }
    
    @Test
    public void test0FetchEntities(){
        Map<String,IdentifierType> map = idTypeMan.fetchIdentifierTypesByAPITypeName();
        assertEquals(34+v2Ids.size(), map.size());
        checkExists(map,"other-id"); 
        for (String id : v2Ids){
            checkExists(map, id);            
        }
    }

    private void checkExists(Map<String, IdentifierType> map, String id) {
        assertTrue(map.containsKey(id));
        assertEquals(id,map.get(id).getName());
        assertNotNull(map.get(id).getPutCode());
    }
    
    @Test
    public void test1FetchIdentifier(){
        IdentifierType id = idTypeMan.fetchIdentifierTypeByDatabaseName("DOI");
        assertEquals("doi",id.getName());
        id = idTypeMan.fetchIdentifierTypeByDatabaseName("OTHER_ID");
        assertEquals("other-id",id.getName());
    }
    
    @Test
    @Transactional
    @Rollback
    public void test2CreateAndUpdateIdentifier(){
        IdentifierType id = idTypeMan.createIdentifierType(createIdentifierType(1));
        assertNotNull(id);
        assertNotNull(id.getPutCode());
        assertTrue(new Date().after(id.getDateCreated()));
        id = idTypeMan.fetchIdentifierTypeByDatabaseName("TEST1");
        assertNotNull(id);
        
        id = idTypeMan.fetchIdentifierTypeByDatabaseName("TEST1");
        Date last = id.getLastModified();
        id.setValidationRegex("test");
        
        id = idTypeMan.updateIdentifierType(id);
        assertTrue(last.before(id.getLastModified()));  
        
        id = idTypeMan.fetchIdentifierTypeByDatabaseName("TEST1");
        assertEquals("test1",id.getName());
        assertEquals("test",id.getValidationRegex());
        assertTrue(last.getTime() < id.getLastModified().getTime()); 
    }
    
    private IdentifierType createIdentifierType(int seed){
        IdentifierType id = new IdentifierType();        
        id.setName("test"+seed);
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
