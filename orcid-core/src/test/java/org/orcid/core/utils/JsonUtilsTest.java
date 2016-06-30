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

import static org.junit.Assert.*;

import org.junit.Test;
import org.orcid.jaxb.model.message.Contributor;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.WorkContributors;

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
        org.orcid.jaxb.model.message.WorkExternalIdentifier extId = wExtId1_2.getWorkExternalIdentifier().get(0);
        assertEquals(org.orcid.jaxb.model.message.WorkExternalIdentifierType.DOI, extId.getWorkExternalIdentifierType());
        assertEquals("12345", extId.getWorkExternalIdentifierId().getContent());
        
        String jsonString2_ExternalIdentifiers = "{\"workExternalIdentifier\":[{\"url\":\"http://orcid.org\", \"relationship\":\"SELF\", \"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"67890\"}}],\"scope\":null}";
        wExtId1_2 = JsonUtils.<org.orcid.jaxb.model.message.WorkExternalIdentifiers> readObjectFromJsonString(jsonString2_ExternalIdentifiers, org.orcid.jaxb.model.message.WorkExternalIdentifiers.class);
        assertNotNull(wExtId1_2);
        assertNotNull(wExtId1_2.getWorkExternalIdentifier());
        extId = wExtId1_2.getWorkExternalIdentifier().get(0);
        assertEquals(org.orcid.jaxb.model.message.WorkExternalIdentifierType.DOI, extId.getWorkExternalIdentifierType());
        assertEquals("67890", extId.getWorkExternalIdentifierId().getContent());
    }
    
    @Test
    public void testJsonStringToWorkExternalIdentifiersV2_0() {
        String jsonString1_2ExternalIdentifiers = "{\"workExternalIdentifier\":[{\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"12345\"}}],\"scope\":null}";
        org.orcid.jaxb.model.record_rc1.WorkExternalIdentifiers wExtId2_0 = JsonUtils.<org.orcid.jaxb.model.record_rc1.WorkExternalIdentifiers> readObjectFromJsonString(jsonString1_2ExternalIdentifiers, org.orcid.jaxb.model.record_rc1.WorkExternalIdentifiers.class);
        assertNotNull(wExtId2_0);
        assertNotNull(wExtId2_0.getWorkExternalIdentifier());
        org.orcid.jaxb.model.record_rc1.WorkExternalIdentifier extId = wExtId2_0.getWorkExternalIdentifier().get(0);
        assertEquals(org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierType.DOI, extId.getWorkExternalIdentifierType());
        assertEquals("12345", extId.getWorkExternalIdentifierId().getContent());
        assertNull(extId.getUrl());
        assertNull(extId.getRelationship());
        
        String jsonString2_ExternalIdentifiers = "{\"workExternalIdentifier\":[{\"url\":\"http://orcid.org\", \"relationship\":\"SELF\", \"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"67890\"}}],\"scope\":null}";
        wExtId2_0 = JsonUtils.<org.orcid.jaxb.model.record_rc1.WorkExternalIdentifiers> readObjectFromJsonString(jsonString2_ExternalIdentifiers, org.orcid.jaxb.model.record_rc1.WorkExternalIdentifiers.class);
        assertNotNull(wExtId2_0);
        assertNotNull(wExtId2_0.getWorkExternalIdentifier());
        extId = wExtId2_0.getWorkExternalIdentifier().get(0);
        assertEquals(org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierType.DOI, extId.getWorkExternalIdentifierType());
        assertEquals("67890", extId.getWorkExternalIdentifierId().getContent());
        assertNotNull(extId.getUrl());
        assertEquals("http://orcid.org", extId.getUrl().getValue());
        assertNotNull(extId.getRelationship());
        assertEquals(org.orcid.jaxb.model.record_rc1.Relationship.SELF, extId.getRelationship());
        
    }
    
    @Test
    public void testJsonStringToFundingExternalIdentifiersV1_2(){
        String jsonString1_2ExternalIdentifiers = "{\"fundingExternalIdentifier\":[{\"type\":\"GRANT_NUMBER\",\"value\":\"123\",\"url\":{\"value\":\"http://orcid.org\"}}]}";
        org.orcid.jaxb.model.message.FundingExternalIdentifiers fExtId1_2 = JsonUtils.<org.orcid.jaxb.model.message.FundingExternalIdentifiers> readObjectFromJsonString(jsonString1_2ExternalIdentifiers, org.orcid.jaxb.model.message.FundingExternalIdentifiers.class);
        assertNotNull(fExtId1_2);
        assertNotNull(fExtId1_2.getFundingExternalIdentifier());
        org.orcid.jaxb.model.message.FundingExternalIdentifier extId = fExtId1_2.getFundingExternalIdentifier().get(0);
        assertEquals(org.orcid.jaxb.model.message.FundingExternalIdentifierType.GRANT_NUMBER, extId.getType());
        assertEquals("123", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://orcid.org", extId.getUrl().getValue());
        
        String jsonString2_0ExternalIdentifiers = "{\"fundingExternalIdentifier\":[{\"relationship\":\"SELF\",\"type\":\"GRANT_NUMBER\",\"value\":\"456\",\"url\":{\"value\":\"http://orcid.org/updated\"}}]}";
        fExtId1_2 = JsonUtils.<org.orcid.jaxb.model.message.FundingExternalIdentifiers> readObjectFromJsonString(jsonString2_0ExternalIdentifiers, org.orcid.jaxb.model.message.FundingExternalIdentifiers.class);
        extId = fExtId1_2.getFundingExternalIdentifier().get(0);
        assertEquals(org.orcid.jaxb.model.message.FundingExternalIdentifierType.GRANT_NUMBER, extId.getType());
        assertEquals("456", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://orcid.org/updated", extId.getUrl().getValue());        
    }
    
    @Test
    public void testJsonStringToFundingExternalIdentifiersV2_0(){
        String jsonString1_2ExternalIdentifiers = "{\"externalIdentifier\":[{\"type\":\"GRANT_NUMBER\",\"value\":\"123\",\"url\":{\"value\":\"http://orcid.org\"}}]}";
        org.orcid.jaxb.model.record_rc1.FundingExternalIdentifiers fExtId2_0 = JsonUtils.<org.orcid.jaxb.model.record_rc1.FundingExternalIdentifiers> readObjectFromJsonString(jsonString1_2ExternalIdentifiers, org.orcid.jaxb.model.record_rc1.FundingExternalIdentifiers.class);
        assertNotNull(fExtId2_0);
        assertNotNull(fExtId2_0.getExternalIdentifier());
        org.orcid.jaxb.model.record_rc1.FundingExternalIdentifier extId = fExtId2_0.getExternalIdentifier().get(0);
        assertEquals(org.orcid.jaxb.model.record_rc1.FundingExternalIdentifierType.GRANT_NUMBER, extId.getType());
        assertEquals("123", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://orcid.org", extId.getUrl().getValue());
        
        String jsonString2_0ExternalIdentifiers = "{\"externalIdentifier\":[{\"relationship\":\"SELF\",\"type\":\"GRANT_NUMBER\",\"value\":\"456\",\"url\":{\"value\":\"http://orcid.org/updated\"}}]}";
        fExtId2_0 = JsonUtils.<org.orcid.jaxb.model.record_rc1.FundingExternalIdentifiers> readObjectFromJsonString(jsonString2_0ExternalIdentifiers, org.orcid.jaxb.model.record_rc1.FundingExternalIdentifiers.class);
        assertNotNull(fExtId2_0);
        assertNotNull(fExtId2_0.getExternalIdentifier());
        extId = fExtId2_0.getExternalIdentifier().get(0);
        assertEquals(org.orcid.jaxb.model.record_rc1.FundingExternalIdentifierType.GRANT_NUMBER, extId.getType());
        assertEquals("456", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://orcid.org/updated", extId.getUrl().getValue());
        assertEquals(org.orcid.jaxb.model.record_rc1.Relationship.SELF, extId.getRelationship());
        
    }
}
