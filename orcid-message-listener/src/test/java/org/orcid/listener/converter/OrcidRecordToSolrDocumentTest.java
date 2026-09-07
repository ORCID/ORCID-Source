package org.orcid.listener.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Arrays;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import org.junit.Test;
import org.orcid.jaxb.model.common.PeerReviewType;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.common.Role;
import org.orcid.jaxb.model.v3.release.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.v3.release.common.OrcidIdentifier;
import org.orcid.jaxb.model.v3.release.common.Organization;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.jaxb.model.v3.release.record.Person;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.jaxb.model.v3.release.record.ResearchResourceHosts;
import org.orcid.jaxb.model.v3.release.record.ResearchResourceItem;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.FundingGroup;
import org.orcid.jaxb.model.v3.release.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Fundings;
import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Works;
import org.orcid.listener.solr.OrcidRecordToSolrDocument;
import org.orcid.utils.solr.entities.OrcidSolrDocument;
import org.orcid.utils.solr.entities.SolrConstants;

public class OrcidRecordToSolrDocumentTest {
        
    @Test
    public void convertTest() throws JAXBException{
        //as above, but with PDB identifier
        Record record = getRecord("/record_3.0/samples/read_samples/record-3.0.xml");
        ResearchResource researchResource = getResearchResource("/record_3.0/samples/read_samples/research-resource-3.0.xml");
        
        ResearchResourceHosts rrh = new ResearchResourceHosts();
        Organization org = new Organization();
        org.setName("ORCID");
        DisambiguatedOrganization disambiguatedOrg = new DisambiguatedOrganization();
        disambiguatedOrg.setDisambiguationSource("FUNDREF");
        disambiguatedOrg.setDisambiguatedOrganizationIdentifier("ORCID_ORG_ID");
        disambiguatedOrg.setId(1000L);
        org.setDisambiguatedOrganization(disambiguatedOrg);
        rrh.setOrganization(Arrays.asList(org));
        
        for(ResearchResourceItem rri : researchResource.getResourceItems()) {
            if(rri.getName().contains("Giant Laser 1")) {
                rri.setHosts(rrh);
            }
        }
        
        OrcidRecordToSolrDocument v3 = new  OrcidRecordToSolrDocument(false);
        OrcidSolrDocument v3Doc = v3.convert(record, Arrays.asList(researchResource));
        
        assertEquals("8888-8888-8888-8880", v3Doc.getOrcid());
        assertEquals("credit-name", v3Doc.getCreditName());
        assertEquals("give-names", v3Doc.getGivenNames());
        assertEquals("family-name", v3Doc.getFamilyName());
        assertEquals("give-names family-name", v3Doc.getGivenAndFamilyNames());
        assertEquals(1, v3Doc.getEmailAddresses().size());
        assertEquals("user1@email.com", v3Doc.getEmailAddresses().get(0));
        assertEquals(2, v3Doc.getCurrentInstitutionAffiliationNames().size());
        assertTrue(v3Doc.getCurrentInstitutionAffiliationNames().contains("service-org"));
        assertTrue(v3Doc.getCurrentInstitutionAffiliationNames().contains("membership-org"));
        assertEquals(5, v3Doc.getPastInstitutionAffiliationNames().size());
        assertTrue(v3Doc.getPastInstitutionAffiliationNames().contains("distinction-org"));
        assertTrue(v3Doc.getPastInstitutionAffiliationNames().contains("education-org"));
        assertTrue(v3Doc.getPastInstitutionAffiliationNames().contains("invited-position-org"));
        assertTrue(v3Doc.getPastInstitutionAffiliationNames().contains("qualification-org"));
        assertTrue(v3Doc.getPastInstitutionAffiliationNames().contains("employment-org"));
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
        assertEquals(4, v3Doc.getOrganisationIds().size());
        assertEquals(2, v3Doc.getOrganisationIds().get(SolrConstants.FUNDREF_ORGANISATION_ID).size());
        assertTrue(v3Doc.getOrganisationIds().get(SolrConstants.FUNDREF_ORGANISATION_ID).contains("common:disambiguated-organization-identifier-funding"));
        assertTrue(v3Doc.getOrganisationIds().get(SolrConstants.FUNDREF_ORGANISATION_ID).contains("ORCID_ORG_ID"));
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

        // Relationship based identifiers. These were previously untested, so they are asserted here
        // to protect the existing self/part-of behaviour.
        assertNotNull(v3Doc.getSelfIds());
        assertNotNull(v3Doc.getPartOfIds());
        assertNotNull(v3Doc.getVersionOfIds());
        assertNotNull(v3Doc.getFundedByIds());

        // The funding in this record carries a grant_number external id with a 'self' relationship,
        // but fundings do not contribute relationship based identifiers. Funding grant numbers stay
        // searchable through the separate 'grant-numbers' field asserted above.
        assertTrue(!v3Doc.getSelfIds().containsKey("grant_number" + SolrConstants.DYNAMIC_SELF));

        // This record has no version-of or funded-by identifiers
        assertTrue(v3Doc.getVersionOfIds().isEmpty());
        assertTrue(v3Doc.getFundedByIds().isEmpty());
    }

    @Test
    public void convertIndexesVersionOfAndFundedByTest() {
        Record record = new Record();
        record.setOrcidIdentifier(new OrcidIdentifier("0000-0001-2345-6789"));
        // The activities are only indexed when a person is present on the record
        record.setPerson(new Person());

        WorkSummary work = new WorkSummary();
        work.setExternalIdentifiers(externalIds(
                externalId("doi", "10.1000/self", Relationship.SELF),
                externalId("doi", "10.1000/part-of", Relationship.PART_OF),
                externalId("doi", "10.1000/version-of", Relationship.VERSION_OF),
                externalId("grant_number", "work-funded-by", Relationship.FUNDED_BY)));
        WorkGroup workGroup = new WorkGroup();
        workGroup.getWorkSummary().add(work);
        Works works = new Works();
        works.getWorkGroup().add(workGroup);

        FundingSummary funding = new FundingSummary();
        funding.setExternalIdentifiers(externalIds(
                externalId("proposal-id", "funding-self", Relationship.SELF),
                externalId("grant_number", "funding-funded-by", Relationship.FUNDED_BY)));
        FundingGroup fundingGroup = new FundingGroup();
        fundingGroup.getFundingSummary().add(funding);
        Fundings fundings = new Fundings();
        fundings.getFundingGroup().add(fundingGroup);

        ActivitiesSummary activities = new ActivitiesSummary();
        activities.setWorks(works);
        activities.setFundings(fundings);
        record.setActivitiesSummary(activities);

        OrcidSolrDocument doc = new OrcidRecordToSolrDocument(false).convert(record, null);

        // version-of on works is the gap this change closes
        assertEquals(Arrays.asList("10.1000/version-of"), doc.getVersionOfIds().get("doi" + SolrConstants.DYNAMIC_VERSION_OF));

        // funded-by was not indexed for any activity type before this change
        assertEquals(Arrays.asList("work-funded-by"), doc.getFundedByIds().get("grant_number" + SolrConstants.DYNAMIC_FUNDED_BY));

        // self and part-of keep working
        assertEquals(Arrays.asList("10.1000/self"), doc.getSelfIds().get("doi" + SolrConstants.DYNAMIC_SELF));
        assertEquals(Arrays.asList("10.1000/part-of"), doc.getPartOfIds().get("doi" + SolrConstants.DYNAMIC_PART_OF));

        // Fundings deliberately contribute no relationship based identifiers. Indexing them would
        // change the results of self and part-of queries that already work today, which is out of
        // scope for PD-6176. The funding above carries both a 'self' and a 'funded-by' identifier
        // and neither should appear.
        assertNull(doc.getSelfIds().get("proposal-id" + SolrConstants.DYNAMIC_SELF));
        assertNull(doc.getFundedByIds().get("proposal-id" + SolrConstants.DYNAMIC_FUNDED_BY));
        assertTrue(!doc.getFundedByIds().get("grant_number" + SolrConstants.DYNAMIC_FUNDED_BY).contains("funding-funded-by"));
    }

    private ExternalIDs externalIds(ExternalID... ids) {
        ExternalIDs externalIds = new ExternalIDs();
        for (ExternalID id : ids) {
            externalIds.getExternalIdentifier().add(id);
        }
        return externalIds;
    }

    private ExternalID externalId(String type, String value, Relationship relationship) {
        ExternalID id = new ExternalID();
        id.setType(type);
        id.setValue(value);
        id.setRelationship(relationship);
        return id;
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
