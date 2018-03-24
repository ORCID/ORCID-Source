package org.orcid.core.cli;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.stream.XMLStreamException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.persistence.dao.OrgDao;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.dao.OrgDisambiguatedExternalIdentifierDao;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;

public class LoadLEIDataTest {
    @Mock
    private OrgDisambiguatedExternalIdentifierDao orgDisambiguatedExternalIdentifierDao;
    @Mock
    private OrgDisambiguatedDao orgDisambiguatedDao;
    @Mock
    private OrgDao orgDao;
    @InjectMocks
    private LoadLEIData loadLeiData;
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void testAddSimpleRecord() throws XMLStreamException, URISyntaxException, FileNotFoundException{
        Path path = Paths.get(getClass().getClassLoader().getResource("lei/simpleRecord.xml").toURI());
        File testFile = path.toFile();
        ArgumentCaptor<OrgDisambiguatedEntity> argument = ArgumentCaptor.forClass(OrgDisambiguatedEntity.class);
        doNothing().when(orgDisambiguatedDao).persist(argument.capture());
        loadLeiData.setFileToLoad(testFile);
        loadLeiData.execute();
        assertEquals("213800GW5X3N23U2YM51", argument.getValue().getSourceId());
        assertEquals("UNIVERSITY OF BATH (THE)", argument.getValue().getName());
        assertEquals("BATH", argument.getValue().getCity());
        assertEquals("GB", argument.getValue().getCountry().value());
        assertEquals("OTHER", argument.getValue().getOrgType());
        assertEquals(null, argument.getValue().getRegion());
        assertEquals("PENDING", argument.getValue().getIndexingStatus().toString());
        assertEquals(null, argument.getValue().getStatus());
        assertEquals("LEI", argument.getValue().getSourceType());
    }
    
    @Test
    public void testAddComplexRecord() throws XMLStreamException, URISyntaxException, FileNotFoundException{
        Path path = Paths.get(getClass().getClassLoader().getResource("lei/complex.xml").toURI());
        File testFile = path.toFile();
        ArgumentCaptor<OrgDisambiguatedEntity> argument = ArgumentCaptor.forClass(OrgDisambiguatedEntity.class);
        doNothing().when(orgDisambiguatedDao).persist(argument.capture());;
        ArgumentCaptor<OrgEntity> orgArgument = ArgumentCaptor.forClass(OrgEntity.class);
        doNothing().when(orgDao).persist(orgArgument.capture());
        loadLeiData.setFileToLoad(testFile);
        loadLeiData.execute();
        assertEquals("213800ZH4VUOQOUVYX93", argument.getValue().getSourceId());
        assertEquals("МОНБАТ АД", argument.getValue().getName());
        assertEquals("SOFIA", argument.getValue().getCity());
        assertEquals("BG", argument.getValue().getCountry().value());
        assertEquals("AKTSIONERNO DRUZHESTVO", argument.getValue().getOrgType());
        assertEquals("BG-22", argument.getValue().getRegion());
        assertEquals("PENDING", argument.getValue().getIndexingStatus().toString());
        assertEquals(null, argument.getValue().getStatus());
        assertEquals("LEI", argument.getValue().getSourceType());        
        assertEquals("MONBAT PLC",orgArgument.getAllValues().get(0).getName());
        assertEquals("SOFIA", orgArgument.getAllValues().get(0).getCity());
        assertEquals("BG", orgArgument.getAllValues().get(0).getCountry().value());
        assertEquals("BG-22", orgArgument.getAllValues().get(0).getRegion());
        assertEquals("213800ZH4VUOQOUVYX93", orgArgument.getAllValues().get(0).getOrgDisambiguated().getSourceId());        
    }
    
    @Test 
    public void testUpdate() throws URISyntaxException, FileNotFoundException, XMLStreamException{
        Path path = Paths.get(getClass().getClassLoader().getResource("lei/complex.xml").toURI());
        File testFile = path.toFile();
        //fake finding existing
        OrgDisambiguatedEntity found = new OrgDisambiguatedEntity();
        found.setSourceId("213800ZH4VUOQOUVYX93");
        found.setSourceType("LEI");
        Mockito.when(orgDisambiguatedDao.findBySourceIdAndSourceType(Mockito.anyString(), Mockito.anyString())).thenReturn(found);
        //capture merge
        ArgumentCaptor<OrgDisambiguatedEntity> argument = ArgumentCaptor.forClass(OrgDisambiguatedEntity.class);
        Mockito.when(orgDisambiguatedDao.merge(argument.capture())).then(AdditionalAnswers.returnsFirstArg());
        //fake finding existing org
        Mockito.when(orgDao.findByAddressAndDisambiguatedOrg(Mockito.anyString(), Mockito.anyString(),Mockito.anyString(),Mockito.anyObject(),Mockito.anyObject())).thenReturn(new OrgEntity());        
        //make sure we're not calling the wrong things
        doThrow(new RuntimeException()).when(orgDao).persist(Mockito.anyObject());
        doThrow(new RuntimeException()).when(orgDao).merge(Mockito.anyObject());
        doThrow(new RuntimeException()).when(orgDisambiguatedDao).persist(Mockito.anyObject());
        loadLeiData.setFileToLoad(testFile);
        loadLeiData.execute();
        assertEquals("213800ZH4VUOQOUVYX93", argument.getValue().getSourceId());
        assertEquals("МОНБАТ АД", argument.getValue().getName());
        assertEquals("SOFIA", argument.getValue().getCity());
        assertEquals("BG", argument.getValue().getCountry().value());
        assertEquals("AKTSIONERNO DRUZHESTVO", argument.getValue().getOrgType());
        assertEquals("BG-22", argument.getValue().getRegion());
        assertEquals("PENDING", argument.getValue().getIndexingStatus().toString());
        assertEquals(null, argument.getValue().getStatus());
        assertEquals("LEI", argument.getValue().getSourceType()); 
    }
    
    @Test 
    public void testUpdateWithNewTranslatedName() throws URISyntaxException, FileNotFoundException, XMLStreamException{
        Path path = Paths.get(getClass().getClassLoader().getResource("lei/complex.xml").toURI());
        File testFile = path.toFile();
        //fake finding existing
        OrgDisambiguatedEntity found = new OrgDisambiguatedEntity();
        found.setSourceId("213800ZH4VUOQOUVYX93");
        found.setSourceType("LEI");
        Mockito.when(orgDisambiguatedDao.findBySourceIdAndSourceType(Mockito.anyString(), Mockito.anyString())).thenReturn(found);
        //capture merge
        ArgumentCaptor<OrgDisambiguatedEntity> argument = ArgumentCaptor.forClass(OrgDisambiguatedEntity.class);
        Mockito.when(orgDisambiguatedDao.merge(argument.capture())).then(AdditionalAnswers.returnsFirstArg());
        //fake not finding existing org
        ArgumentCaptor<OrgEntity> orgArgument = ArgumentCaptor.forClass(OrgEntity.class);
        doNothing().when(orgDao).persist(orgArgument.capture());;
        //make sure we're not calling the wrong things
        doThrow(new RuntimeException()).when(orgDao).merge(Mockito.anyObject());
        doThrow(new RuntimeException()).when(orgDisambiguatedDao).persist(Mockito.anyObject());
        loadLeiData.setFileToLoad(testFile);
        loadLeiData.execute();
        assertEquals("213800ZH4VUOQOUVYX93", argument.getValue().getSourceId());
        assertEquals("МОНБАТ АД", argument.getValue().getName());
        assertEquals("SOFIA", argument.getValue().getCity());
        assertEquals("BG", argument.getValue().getCountry().value());
        assertEquals("AKTSIONERNO DRUZHESTVO", argument.getValue().getOrgType());
        assertEquals("BG-22", argument.getValue().getRegion());
        assertEquals("PENDING", argument.getValue().getIndexingStatus().toString());
        assertEquals(null, argument.getValue().getStatus());
        assertEquals("LEI", argument.getValue().getSourceType());
        assertEquals("MONBAT PLC",orgArgument.getAllValues().get(0).getName());
        assertEquals("SOFIA", orgArgument.getAllValues().get(0).getCity());
        assertEquals("BG", orgArgument.getAllValues().get(0).getCountry().value());
        assertEquals("BG-22", orgArgument.getAllValues().get(0).getRegion());
        assertEquals("213800ZH4VUOQOUVYX93", orgArgument.getAllValues().get(0).getOrgDisambiguated().getSourceId());        
    }
    
}
