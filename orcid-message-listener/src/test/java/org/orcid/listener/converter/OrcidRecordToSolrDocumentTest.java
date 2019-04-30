package org.orcid.listener.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.orcid.jaxb.model.common.PeerReviewType;
import org.orcid.jaxb.model.common.Role;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.listener.solr.OrcidRecordToSolrDocument;
import org.orcid.utils.solr.entities.OrcidSolrDocument;
import org.orcid.utils.solr.entities.SolrConstants;

public class OrcidRecordToSolrDocumentTest {
        
    @Test
    public void convertTest() throws JAXBException{
        //as above, but with PDB identifier
        Record record = getRecord("/record_3.0/samples/read_samples/record-3.0.xml");
        ResearchResource researchResource = getResearchResource("/record_3.0/samples/read_samples/research-resource-3.0.xml");
        
        OrcidRecordToSolrDocument v3 = new  OrcidRecordToSolrDocument(false);
        OrcidSolrDocument v3Doc = v3.convert(record, Arrays.asList(researchResource));
        
        assertEquals("8888-8888-8888-8880", v3Doc.getOrcid());
        assertEquals("credit-name", v3Doc.getCreditName());
        assertEquals("give-names", v3Doc.getGivenNames());
        assertEquals("family-name", v3Doc.getFamilyName());
        assertEquals("give-names family-name", v3Doc.getGivenAndFamilyNames());
        assertEquals(1, v3Doc.getEmailAddresses().size());
        assertEquals("user1@email.com", v3Doc.getEmailAddresses().get(0));
        assertEquals(5, v3Doc.getCurrentInstitutionAffiliationNames().size());
        assertTrue(v3Doc.getCurrentInstitutionAffiliationNames().contains("service-org"));
        assertTrue(v3Doc.getCurrentInstitutionAffiliationNames().contains("qualification-org"));
        assertTrue(v3Doc.getCurrentInstitutionAffiliationNames().contains("membership-org"));
        assertTrue(v3Doc.getCurrentInstitutionAffiliationNames().contains("invited-position-org"));
        assertTrue(v3Doc.getCurrentInstitutionAffiliationNames().contains("employment-org"));        
        assertEquals(2, v3Doc.getPastInstitutionAffiliationNames().size());
        assertTrue(v3Doc.getPastInstitutionAffiliationNames().contains("distinction-org"));
        assertTrue(v3Doc.getPastInstitutionAffiliationNames().contains("education-org"));        
        assertEquals(1, v3Doc.getExternalIdReferences().size());
        assertEquals("value-1", v3Doc.getExternalIdReferences().get(0));
        assertEquals(1, v3Doc.getExternalIdSources().size());
        assertEquals("8888-8888-8888-8880", v3Doc.getExternalIdSources().get(0));
        assertEquals(1, v3Doc.getExternalIdReferences().size());
        assertEquals("value-1", v3Doc.getExternalIdReferences().get(0));
        assertEquals(1, v3Doc.getExternalIdTypeAndValue().size());
        assertEquals("type-1=value-1", v3Doc.getExternalIdTypeAndValue().get(0));
        assertEquals(2, v3Doc.getFundingTitles().size());
        assertTrue(v3Doc.getFundingTitles().contains("common:title"));
        assertTrue(v3Doc.getFundingTitles().contains("common:translated-title"));
        assertEquals(1, v3Doc.getGrantNumbers().size());        
        assertEquals("external-id-value-1", v3Doc.getGrantNumbers().get(0));        
        assertEquals(1, v3Doc.getKeywords().size());
        assertEquals("keyword1", v3Doc.getKeywords().get(0));
        assertEquals(3, v3Doc.getOrganisationIds().size());
        assertEquals(1, v3Doc.getOrganisationIds().get(SolrConstants.FUNDREF_ORGANISATION_ID).size());
        assertTrue(v3Doc.getOrganisationIds().get(SolrConstants.FUNDREF_ORGANISATION_ID).contains("common:disambiguated-organization-identifier-funding"));
        assertEquals(4, v3Doc.getOrganisationIds().get(SolrConstants.GRID_ORGANISATION_ID).size());
        assertTrue(v3Doc.getOrganisationIds().get(SolrConstants.GRID_ORGANISATION_ID).contains("common:disambiguated-organization-identifier-invited-position"));
        assertTrue(v3Doc.getOrganisationIds().get(SolrConstants.GRID_ORGANISATION_ID).contains("common:disambiguated-organization-identifier-distinction"));
        assertTrue(v3Doc.getOrganisationIds().get(SolrConstants.GRID_ORGANISATION_ID).contains("common:disambiguated-organization-identifier-education"));
        assertTrue(v3Doc.getOrganisationIds().get(SolrConstants.GRID_ORGANISATION_ID).contains("common:disambiguated-organization-identifier-employment"));
        assertEquals(4, v3Doc.getOrganisationIds().get(SolrConstants.RINGGOLD_ORGANISATION_ID).size());
        assertTrue(v3Doc.getOrganisationIds().get(SolrConstants.RINGGOLD_ORGANISATION_ID).contains("common:disambiguated-organization-identifier-peer-review"));
        assertTrue(v3Doc.getOrganisationIds().get(SolrConstants.RINGGOLD_ORGANISATION_ID).contains("common:disambiguated-organization-identifier-membership"));
        assertTrue(v3Doc.getOrganisationIds().get(SolrConstants.RINGGOLD_ORGANISATION_ID).contains("common:disambiguated-organization-identifier-qualification"));
        assertTrue(v3Doc.getOrganisationIds().get(SolrConstants.RINGGOLD_ORGANISATION_ID).contains("common:disambiguated-organization-identifier-service"));
        assertEquals(1, v3Doc.getOtherNames().size());
        assertEquals("other-name-1", v3Doc.getOtherNames().get(0));
        assertEquals(1, v3Doc.getPeerReviewGroupId().size());
        assertTrue(v3Doc.getPeerReviewGroupId().contains("orcid-generated:12345"));        
        assertEquals(1, v3Doc.getPeerReviewRole().size());
        assertTrue(v3Doc.getPeerReviewRole().contains(Role.REVIEWER.value()));
        assertEquals(1, v3Doc.getPeerReviewType().size());
        assertTrue(v3Doc.getPeerReviewType().contains(PeerReviewType.REVIEW.value()));
        assertNull(v3Doc.getPrimaryRecord());
        assertEquals(2, v3Doc.getWorkTitles().size());        
        assertTrue(v3Doc.getWorkTitles().contains("common:title"));
        assertTrue(v3Doc.getWorkTitles().contains("common:translated-title"));        
        assertEquals(2, v3Doc.getResearchResourceProposalTitles().size());
        assertTrue(v3Doc.getResearchResourceProposalTitles().contains("Giant Laser Award"));
        assertTrue(v3Doc.getResearchResourceProposalTitles().contains("Giant Laser Award2"));
        assertEquals(2, v3Doc.getResearchResourceItemNames().size());
        assertTrue(v3Doc.getResearchResourceItemNames().contains("Giant Laser 1")); 
        assertTrue(v3Doc.getResearchResourceItemNames().contains("Moon Targets"));         
    }
    
    private Record getRecord(String name) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Record.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = this.getClass().getResourceAsStream(name);
        return (Record) unmarshaller.unmarshal(inputStream);
    }
    
    private ResearchResource getResearchResource(String name) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(ResearchResource.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = this.getClass().getResourceAsStream(name);
        return (ResearchResource) unmarshaller.unmarshal(inputStream);
    }
}
