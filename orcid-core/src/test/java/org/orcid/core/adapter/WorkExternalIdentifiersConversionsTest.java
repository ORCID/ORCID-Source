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
package org.orcid.core.adapter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.orcid.core.adapter.impl.jsonidentifiers.PeerReviewWorkExternalIDConverter;
import org.orcid.core.adapter.impl.jsonidentifiers.WorkExternalIDsConverter;
import org.orcid.jaxb.model.common_v2.Url;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.ExternalIDs;
import org.orcid.jaxb.model.record_v2.Relationship;

public class WorkExternalIdentifiersConversionsTest {

    private final String expected = "{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://what.com\"},\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"value\"}}";
    private final String expectedIDs = "{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://what.com\"},\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"value\"}},{\"relationship\":\"PART_OF\",\"url\":{\"value\":\"http://whatnow.com\"},\"workExternalIdentifierType\":\"SOURCE_WORK_ID\",\"workExternalIdentifierId\":{\"content\":\"value2\"}}]}";

    @Test
    public void testConvertFromExternalID(){
        PeerReviewWorkExternalIDConverter conv = new PeerReviewWorkExternalIDConverter();
        String externalIdentifiersAsString = expected;
        ExternalID id = conv.convertFrom(externalIdentifiersAsString, null);
        assertEquals(Relationship.SELF,id.getRelationship());
        assertEquals(new Url("http://what.com"), id.getUrl());
        assertEquals("doi",id.getType());
        assertEquals("value",id.getValue());
    }

    @Test
    public void testConvertToExternalID(){
        PeerReviewWorkExternalIDConverter conv = new PeerReviewWorkExternalIDConverter();
        ExternalID id = new ExternalID(); 
        id.setRelationship(Relationship.SELF);
        id.setType("doi");
        id.setUrl(new Url("http://what.com"));
        id.setValue("value");
        String externalIdentifiersAsString = conv.convertTo(id, null);  
        assertEquals(expected, externalIdentifiersAsString);
    }

    @Test
    public void testConvertFromExternalIDs(){
        WorkExternalIDsConverter conv = new WorkExternalIDsConverter();
        ExternalID id = new ExternalID(); 
        id.setRelationship(Relationship.SELF);
        id.setType("doi");
        id.setUrl(new Url("http://what.com"));
        id.setValue("value");
        ExternalID id2 = new ExternalID(); 
        id2.setRelationship(Relationship.PART_OF);
        id2.setType("source-work-id");
        id2.setUrl(new Url("http://whatnow.com"));
        id2.setValue("value2");
        ExternalIDs ids = new ExternalIDs();
        ids.getExternalIdentifier().add(id);
        ids.getExternalIdentifier().add(id2);
        String externalIdentifiersAsString = conv.convertTo(ids, null);    
        assertEquals(expectedIDs,externalIdentifiersAsString);
    }

    @Test
    public void testConvertToExternalIDs(){
        WorkExternalIDsConverter conv = new WorkExternalIDsConverter();
        String externalIdentifiersAsString = expectedIDs;
        ExternalIDs ids = conv.convertFrom(externalIdentifiersAsString, null);
        assertEquals(Relationship.SELF,ids.getExternalIdentifier().get(0).getRelationship());
        assertEquals(new Url("http://what.com"), ids.getExternalIdentifier().get(0).getUrl());
        assertEquals("doi",ids.getExternalIdentifier().get(0).getType());
        assertEquals("value",ids.getExternalIdentifier().get(0).getValue());
        assertEquals(Relationship.PART_OF,ids.getExternalIdentifier().get(1).getRelationship());
        assertEquals(new Url("http://whatnow.com"), ids.getExternalIdentifier().get(1).getUrl());
        assertEquals("source-work-id",ids.getExternalIdentifier().get(1).getType());
        assertEquals("value2",ids.getExternalIdentifier().get(1).getValue());
    }

}
