package org.orcid.core.utils.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.persistence.jpa.entities.RecordNameEntity;

public class RecordNameUtilsTest {

    @Test
    public void testGetPublicNamePublicVisibilityCreditName() {
        RecordNameEntity recordName = new RecordNameEntity();
        recordName.setVisibility(Visibility.PUBLIC);
        recordName.setCreditName("credit name");
        
        String publicName = RecordNameUtils.getPublicName(recordName);
        assertNotNull(recordName);
        assertEquals(recordName.getCreditName(), publicName);
    }
    
    @Test
    public void testGetPublicNamePublicVisibilityNoCreditName() {
        RecordNameEntity recordName = new RecordNameEntity();
        recordName.setVisibility(Visibility.PUBLIC);
        recordName.setGivenNames("given name");
        recordName.setFamilyName("family name");
        
        String publicName = RecordNameUtils.getPublicName(recordName);
        assertNotNull(recordName);
        assertEquals(recordName.getGivenNames() + " " + recordName.getFamilyName(), publicName);
    }
    
    @Test
    public void testGetPublicNamePublicVisibilityNoFamilyName() {
        RecordNameEntity recordName = new RecordNameEntity();
        recordName.setVisibility(Visibility.PUBLIC);
        recordName.setGivenNames("given name");
        
        String publicName = RecordNameUtils.getPublicName(recordName);
        assertNotNull(recordName);
        assertEquals(recordName.getGivenNames(), publicName);
    }
    
    @Test
    public void testGetPublicNamePrivateVisibility() {
        RecordNameEntity recordName = new RecordNameEntity();
        recordName.setVisibility(Visibility.PRIVATE);
        recordName.setCreditName("credit name");
        recordName.setGivenNames("given name");
        recordName.setFamilyName("family name");
        
        String publicName = RecordNameUtils.getPublicName(recordName);
        assertNotNull(recordName);
        assertNull(publicName);
    }

}
