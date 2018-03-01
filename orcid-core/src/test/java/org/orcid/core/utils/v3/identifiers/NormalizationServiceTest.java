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
package org.orcid.core.utils.v3.identifiers;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.utils.v3.identifiers.NormalizationService;
import org.orcid.jaxb.model.v3.dev1.common.TransientNonEmptyString;
import org.orcid.jaxb.model.v3.dev1.record.ExternalID;
import org.orcid.jaxb.model.v3.dev1.record.Relationship;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class NormalizationServiceTest {

    @Resource
    NormalizationService norm;
    
    @Test
    public void checkCaseNormalized(){
        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.SELF);
        id1.setType("agr");
        id1.setValue("UPPER");
        id1.setNormalized(new TransientNonEmptyString(norm.normalise(id1.getType(), id1.getValue())));
        
        ExternalID id2 = new ExternalID();
        id2.setRelationship(Relationship.SELF);
        id2.setType("agr");
        id2.setValue("upper");
        id2.setNormalized(new TransientNonEmptyString("upper"));   
        
        assertEquals(id1,id2);
    }
    
    @Test
    public void checkDOIAndCaseNormalized(){
        ExternalID normed = new ExternalID();
        normed.setRelationship(Relationship.SELF);
        normed.setType("doi");
        normed.setValue("10.1/upper");
        normed.setNormalized(new TransientNonEmptyString("10.1/upper"));  //everything should normalize to this.       

        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.SELF);
        id1.setType("doi");
        id1.setValue("https://dx.doi.org/10.1/UPPER");
        id1.setNormalized(new TransientNonEmptyString(norm.normalise(id1.getType(), id1.getValue())));
        assertEquals(id1,normed);
        
        id1.setValue("http://doi.org/10.1/UPPER");
        id1.setNormalized(new TransientNonEmptyString(norm.normalise(id1.getType(), id1.getValue())));        
        assertEquals(id1,normed);
        
        id1.setValue("10.1/UPPER");
        id1.setNormalized(new TransientNonEmptyString(norm.normalise(id1.getType(), id1.getValue())));
        assertEquals(id1,normed);

    }
    
    @Test
    public void checkISBNAndCaseNormalized(){
        ExternalID normed = new ExternalID();
        normed.setRelationship(Relationship.SELF);
        normed.setType("isbn");
        normed.setValue("ISBN: 123-456-7-89x junk");
        normed.setNormalized(new TransientNonEmptyString("123456789X"));  //everything should normalize to this.       
        
        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.SELF);
        id1.setType("isbn");
        id1.setValue("ISBN: 123-456-7-89x junk");
        id1.setNormalized(new TransientNonEmptyString(norm.normalise(id1.getType(), id1.getValue())));        
        assertEquals(normed,id1);
    }
    
    @Test
    public void checkBibcodeAndCaseNormalized(){
        ExternalID normed = new ExternalID();
        normed.setRelationship(Relationship.SELF);
        normed.setType("bibcode");
        normed.setValue(" 123456789.A23456789 ");
        normed.setNormalized(new TransientNonEmptyString("123456789.A23456789"));  //everything should normalize to this.       
        
        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.SELF);
        id1.setType("bibcode");
        id1.setValue(" 123456789.A23456789 ");
        id1.setNormalized(new TransientNonEmptyString(norm.normalise(id1.getType(), id1.getValue())));        
        assertEquals(normed,id1);
    }

}
