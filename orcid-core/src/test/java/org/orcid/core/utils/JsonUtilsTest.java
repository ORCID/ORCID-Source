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
package org.orcid.core.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.orcid.jaxb.model.message.Contributor;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.WorkContributors;
import org.orcid.jaxb.model.record.Relationship;

/**
 * 
 * @author Will Simpson
 * 
 */
public class JsonUtilsTest {

    @Test
    public void testWorkContributorsToJsonString() {
        WorkContributors workContributors = new WorkContributors();
        Contributor contributor1 = new Contributor();
        workContributors.getContributor().add(contributor1);
        contributor1.setCreditName(new CreditName("A Contributor"));

        String result = JsonUtils.convertToJsonString(workContributors);
        assertEquals(
                "{\"contributor\":[{\"contributorOrcid\":null,\"creditName\":{\"content\":\"A Contributor\",\"visibility\":null},\"contributorEmail\":null,\"contributorAttributes\":null}]}",
                result);
    }

    @Test
    public void testJsonStringToWorkContributors() {
        String jsonString = "{\"contributor\":[{\"contributorOrcid\":null,\"creditName\":{\"content\":\"A Contributor\",\"visibility\":null},\"contributorEmail\":null,\"contributorAttributes\":null}]}";
        WorkContributors workContributors = JsonUtils.<WorkContributors> readObjectFromJsonString(jsonString, WorkContributors.class);
        assertEquals(1, workContributors.getContributor().size());
        assertEquals("A Contributor", workContributors.getContributor().get(0).getCreditName().getContent());
    }

    @Test
    public void testJsonStringToWorkExternalIdentifiersV1_2() {
        String jsonString1_2ExternalIdentifiers = "{\"workExternalIdentifier\":[{\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"12345\"}}],\"scope\":null}";
        org.orcid.jaxb.model.message.WorkExternalIdentifiers wExtId1_2 = JsonUtils.<org.orcid.jaxb.model.message.WorkExternalIdentifiers> readObjectFromJsonString(jsonString1_2ExternalIdentifiers, org.orcid.jaxb.model.message.WorkExternalIdentifiers.class);
        assertNotNull(wExtId1_2);
        assertNotNull(wExtId1_2.getWorkExternalIdentifier());
        assertEquals(org.orcid.jaxb.model.message.WorkExternalIdentifierType.DOI, wExtId1_2.getWorkExternalIdentifier().get(0).getWorkExternalIdentifierType());
        assertEquals("12345", wExtId1_2.getWorkExternalIdentifier().get(0).getWorkExternalIdentifierId().getContent());
        
        String jsonString1_2ExternalIdentifiersWithNewValues = "{\"workExternalIdentifier\":[{\"external-identifier-url\":\"http://orcid.org\", \"relationship\":\"SELF\", \"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"12345\"}}],\"scope\":null}";
        wExtId1_2 = JsonUtils.<org.orcid.jaxb.model.message.WorkExternalIdentifiers> readObjectFromJsonString(jsonString1_2ExternalIdentifiersWithNewValues, org.orcid.jaxb.model.message.WorkExternalIdentifiers.class);
        assertNotNull(wExtId1_2);
        assertNotNull(wExtId1_2.getWorkExternalIdentifier());
        assertEquals(org.orcid.jaxb.model.message.WorkExternalIdentifierType.DOI, wExtId1_2.getWorkExternalIdentifier().get(0).getWorkExternalIdentifierType());
        assertEquals("12345", wExtId1_2.getWorkExternalIdentifier().get(0).getWorkExternalIdentifierId().getContent());
    }
    
    @Test
    public void testJsonStringToWorkExternalIdentifiersV2_0() {
        String jsonString1_2ExternalIdentifiers = "{\"workExternalIdentifier\":[{\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"12345\"}}],\"scope\":null}";
        org.orcid.jaxb.model.record.WorkExternalIdentifiers wExtId1_2 = JsonUtils.<org.orcid.jaxb.model.record.WorkExternalIdentifiers> readObjectFromJsonString(jsonString1_2ExternalIdentifiers, org.orcid.jaxb.model.record.WorkExternalIdentifiers.class);
        assertNotNull(wExtId1_2);
        assertNotNull(wExtId1_2.getWorkExternalIdentifier());
        assertEquals(org.orcid.jaxb.model.record.WorkExternalIdentifierType.DOI, wExtId1_2.getWorkExternalIdentifier().get(0).getWorkExternalIdentifierType());
        assertEquals("12345", wExtId1_2.getWorkExternalIdentifier().get(0).getWorkExternalIdentifierId().getContent());
        assertNull(wExtId1_2.getWorkExternalIdentifier().get(0).getUrl());
        assertNull(wExtId1_2.getWorkExternalIdentifier().get(0).getRelationship());
        
        String jsonString1_2ExternalIdentifiersWithNewValues = "{\"workExternalIdentifier\":[{\"external-identifier-url\":\"http://orcid.org\", \"relationship\":\"SELF\", \"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"12345\"}}],\"scope\":null}";
        wExtId1_2 = JsonUtils.<org.orcid.jaxb.model.record.WorkExternalIdentifiers> readObjectFromJsonString(jsonString1_2ExternalIdentifiersWithNewValues, org.orcid.jaxb.model.record.WorkExternalIdentifiers.class);
        assertNotNull(wExtId1_2);
        assertNotNull(wExtId1_2.getWorkExternalIdentifier());
        assertEquals(org.orcid.jaxb.model.record.WorkExternalIdentifierType.DOI, wExtId1_2.getWorkExternalIdentifier().get(0).getWorkExternalIdentifierType());
        assertEquals("12345", wExtId1_2.getWorkExternalIdentifier().get(0).getWorkExternalIdentifierId().getContent());
        assertNotNull(wExtId1_2.getWorkExternalIdentifier().get(0).getUrl());
        assertEquals("http://orcid.org", wExtId1_2.getWorkExternalIdentifier().get(0).getUrl().getValue());
        assertNotNull(wExtId1_2.getWorkExternalIdentifier().get(0).getRelationship());
        assertEquals(Relationship.SELF, wExtId1_2.getWorkExternalIdentifier().get(0).getRelationship());
        
    }
    
}
