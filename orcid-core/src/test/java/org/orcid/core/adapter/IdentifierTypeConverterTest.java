package org.orcid.core.adapter;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.core.adapter.impl.IdentifierTypePOJOConverter;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.IdentifierTypeEntity;
import org.orcid.pojo.IdentifierType;

public class IdentifierTypeConverterTest extends BaseTest {

    @Test
    public void testFromPojo(){
        IdentifierType id = new IdentifierType();
        id.setPutCode(1l);
        id.setName("name-test");
        id.setDeprecated(true);
        id.setResolutionPrefix("prefix");
        id.setValidationRegex("validation");   
        id.setDateCreated(new Date(10,10,10));
        id.setLastModified(new Date(11,11,11));
        ClientDetailsEntity client = new ClientDetailsEntity();
        client.setClientName("clientName");
        id.setSourceClient(client);
        
        IdentifierTypeEntity entity = new IdentifierTypePOJOConverter().fromPojo(id);
        assertEquals(Long.valueOf(1l), entity.getId());
        assertEquals("NAME_TEST",entity.getName());
        assertEquals(true,entity.getIsDeprecated());
        assertEquals("prefix",entity.getResolutionPrefix());
        assertEquals("validation",entity.getValidationRegex());
        assertEquals(new Date(10,10,10), entity.getDateCreated());
        assertEquals(new Date(11,11,11), entity.getLastModified());
        assertEquals("clientName",entity.getSourceClient().getClientName());
    }
    
    @Test
    public void testToPojo(){
        IdentifierTypeEntity entity1 = new IdentifierTypeEntity();
        entity1.setId(1l);
        entity1.setName("NAME_TEST");
        entity1.setIsDeprecated(true);
        entity1.setResolutionPrefix("prefix");
        entity1.setValidationRegex("validation");   
        entity1.setDateCreated(new Date(10,10,10));
        entity1.setLastModified(new Date(11,11,11));
        ClientDetailsEntity client = new ClientDetailsEntity();
        client.setClientName("clientName");
        entity1.setSourceClient(client);
        
        IdentifierType id = new IdentifierTypePOJOConverter().fromEntity(entity1);
        assertEquals(Long.valueOf(1l), id.getPutCode());
        assertEquals("name-test",id.getName());
        assertEquals(true,id.getDeprecated());
        assertEquals("prefix",id.getResolutionPrefix());
        assertEquals("validation",id.getValidationRegex());
        assertEquals(new Date(10,10,10), id.getDateCreated());
        assertEquals(new Date(11,11,11), id.getLastModified());
        assertEquals("clientName",id.getSourceClient().getClientName());
    }
}
