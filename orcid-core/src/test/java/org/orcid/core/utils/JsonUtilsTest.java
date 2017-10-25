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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.orcid.jaxb.model.common_v2.ContributorAttributes;
import org.orcid.jaxb.model.common_v2.ContributorRole;
import org.orcid.jaxb.model.common_v2.Url;
import org.orcid.jaxb.model.message.Contributor;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.WorkContributors;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.ExternalIDs;
import org.orcid.jaxb.model.record_v2.FundingContributor;
import org.orcid.jaxb.model.record_v2.FundingContributorAttributes;
import org.orcid.jaxb.model.record_v2.FundingContributorRole;
import org.orcid.jaxb.model.record_v2.FundingContributors;
import org.orcid.jaxb.model.record_v2.Relationship;
import org.orcid.jaxb.model.record_v2.SequenceType;

import com.fasterxml.jackson.databind.JsonNode;

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
    
    @Test
    public void testJsonFileToJsonNode() throws URISyntaxException {
        Path path = Paths.get(getClass().getClassLoader()
                .getResource("json_object.json").toURI());
        File test = path.toFile();
        assertTrue(test.exists());
        
        JsonNode rootNode = JsonUtils.read(test);        
        assertEquals("string_value", rootNode.get("string").asText());
        assertFalse(rootNode.get("boolean").asBoolean());
        assertEquals(123456, rootNode.get("number").asInt());
        assertEquals(3, rootNode.get("array").size());
        assertEquals("element1", rootNode.get("array").get(0).asText());
        assertEquals("element2", rootNode.get("array").get(1).asText());
        assertEquals("element3", rootNode.get("array").get(2).asText());
        assertEquals("id", rootNode.get("object").get("att1").asText());
        assertEquals("name", rootNode.get("object").get("att2").asText());
        assertEquals("size", rootNode.get("object").get("att3").asText());
        assertEquals("object_1", rootNode.get("object_array").get(0).get("id").asText());
        assertEquals("object_2", rootNode.get("object_array").get(1).get("id").asText());
        assertEquals("object_3", rootNode.get("object_array").get(2).get("id").asText());
    }
    
    @Test
    public void testFilterInvalidXMLCharacters() {
        // From work contributors
        org.orcid.jaxb.model.record_v2.WorkContributors workContributors = new org.orcid.jaxb.model.record_v2.WorkContributors();
        org.orcid.jaxb.model.common_v2.Contributor workContributor1 = new org.orcid.jaxb.model.common_v2.Contributor();
        workContributors.getContributor().add(workContributor1);        
        workContributor1.setCreditName(new org.orcid.jaxb.model.common_v2.CreditName('\uffff' + "Work " + '\u0000' + "Contributor" + '\ufffe'));
        workContributor1.setContributorEmail(new org.orcid.jaxb.model.common_v2.ContributorEmail("test@" + '\uffff' + "email.com"));
        workContributor1.setContributorOrcid(getContributorOrcid());        
        workContributor1.setContributorAttributes(getContributorAttributes());        
        
        String workResult = JsonUtils.convertToJsonString(workContributors);
        assertEquals(
                "{\"contributor\":[{\"contributorOrcid\":{\"uri\":\"http://test.orcid.org/0000-0000-0000-0000\",\"path\":\"0000-0000-0000-0000\",\"host\":\"test.orcid.org\"},\"creditName\":{\"content\":\"Work Contributor\"},\"contributorEmail\":{\"value\":\"test@email.com\"},\"contributorAttributes\":{\"contributorSequence\":\"ADDITIONAL\",\"contributorRole\":\"ASSIGNEE\"}}]}",
                workResult);
        
        // From funding contributors
        FundingContributors fundingContributors = new FundingContributors();
        FundingContributor fundingContributor1 = new FundingContributor();
        fundingContributors.getContributor().add(fundingContributor1);        
        fundingContributor1.setCreditName(new org.orcid.jaxb.model.common_v2.CreditName('\uffff' + "Funding " + '\u0000' + "Contributor" + '\ufffe'));
        fundingContributor1.setContributorEmail(new org.orcid.jaxb.model.common_v2.ContributorEmail("test@" + '\uffff' + "email.com"));
        fundingContributor1.setContributorOrcid(getContributorOrcid());    
        FundingContributorAttributes fca = new FundingContributorAttributes();
        fca.setContributorRole(FundingContributorRole.LEAD);
        fundingContributor1.setContributorAttributes(fca);
        
        String fundingResult = JsonUtils.convertToJsonString(fundingContributors);
        assertEquals(
                "{\"contributor\":[{\"contributorOrcid\":{\"uri\":\"http://test.orcid.org/0000-0000-0000-0000\",\"path\":\"0000-0000-0000-0000\",\"host\":\"test.orcid.org\"},\"creditName\":{\"content\":\"Funding Contributor\"},\"contributorEmail\":{\"value\":\"test@email.com\"},\"contributorAttributes\":{\"contributorRole\":\"LEAD\"}}]}",
                fundingResult);
        
        // From external ids
        ExternalIDs externalIDs = new ExternalIDs();
        ExternalID extId = new ExternalID();
        extId.setType('\u0000' + "ExtId " + '\u0000' + "Type" + '\u0000');
        extId.setUrl(new Url("http://" + '\u0000' + "test.orcid.org" + '\u0000' + "/"));
        extId.setValue('\u0000' + "The" + '\u0000' + " value" + '\u0000');
        extId.setRelationship(Relationship.PART_OF);
        externalIDs.getExternalIdentifier().add(extId);
    
        String extIdsResult = JsonUtils.convertToJsonString(externalIDs);
        assertEquals(
                "{\"externalIdentifier\":[{\"type\":\"ExtId Type\",\"value\":\"The value\",\"url\":{\"value\":\"http://test.orcid.org/\"},\"relationship\":\"PART_OF\"}]}",
                extIdsResult);
    }
    
    private org.orcid.jaxb.model.common_v2.ContributorOrcid getContributorOrcid() {
        org.orcid.jaxb.model.common_v2.ContributorOrcid c = new org.orcid.jaxb.model.common_v2.ContributorOrcid();
        c.setHost("test" + '\u0000' + ".orcid" + '\u0000' + ".org");
        c.setPath("0000" + '\ufffe' + "-0000" + '\ufffe' + "-0000" + '\ufffe' + "-0000");
        c.setUri("http:" + '\u0000' + "//test" + '\u0000' + ".orcid" + '\u0000' + ".org/" + '\u0000' + "0000" + '\ufffe' + "-0000" + '\ufffe' + "-0000" + '\ufffe' + "-0000");
        return c;
    }
    
    private ContributorAttributes getContributorAttributes() {
        ContributorAttributes c = new ContributorAttributes();
        c.setContributorRole(ContributorRole.ASSIGNEE);
        c.setContributorSequence(SequenceType.ADDITIONAL);
        return c;
    }
}
