package org.orcid.record.v3.rc1;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.orcid.jaxb.model.v3.rc1.common.TransientNonEmptyString;
import org.orcid.jaxb.model.v3.rc1.record.ExternalID;

public class ExternalIDNormalizedValueTest {

    @Test
    public void testEqualsAndHashcode(){
        ExternalID id1 = new ExternalID();
        id1.setType("doi");
        id1.setValue("VALUE");
        id1.setNormalized(new TransientNonEmptyString("value"));
        ExternalID id2 = new ExternalID();
        id2.setType("doi");
        id2.setValue("value");
        id2.setNormalized(new TransientNonEmptyString("value"));        
        assertEquals(id1,id2);
        assertEquals(id1.hashCode(),id2.hashCode());
        
        Set<ExternalID> set1 = new HashSet<ExternalID>();
        id1.getNormalized().setValue("value");
        set1.add(id1);
        set1.add(id2);
        assertEquals(1,set1.size());
        
        id1.getNormalized().setValue("VALUE");        
        assertNotEquals(id1,id2);
        assertNotEquals(id1.hashCode(),id2.hashCode());
        
        Set<ExternalID> set2 = new HashSet<ExternalID>();
        set2.add(id1);
        set2.add(id2);
        assertEquals(2,set2.size());
        
    }
}
