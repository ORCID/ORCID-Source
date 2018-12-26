package org.orcid.core.version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.GregorianCalendar;

import javax.annotation.Resource;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.utils.DateUtils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class V3VersionConverterChainTest {

    @Resource
    private V3VersionConverterChain v3VersionConverterChain;

    @Test
    public void upgradeRC1ToRC2Test() {
        XMLGregorianCalendar gc1 = DateUtils.convertToXMLGregorianCalendar(new GregorianCalendar(2018, 1, 1));
        XMLGregorianCalendar gc2 = DateUtils.convertToXMLGregorianCalendar(new GregorianCalendar(2019, 1, 1));
        
        // Work test
        org.orcid.jaxb.model.v3.rc1.record.Work rc1Work = new org.orcid.jaxb.model.v3.rc1.record.Work();
        rc1Work.setCountry(new org.orcid.jaxb.model.v3.rc1.common.Country(org.orcid.jaxb.model.v3.rc1.common.Iso3166Country.US));                
        rc1Work.setCreatedDate(new org.orcid.jaxb.model.v3.rc1.common.CreatedDate(gc1));        

        rc1Work.setJournalTitle(new org.orcid.jaxb.model.v3.rc1.common.Title("Journal title"));
        rc1Work.setLanguageCode("en");
        rc1Work.setLastModifiedDate(new org.orcid.jaxb.model.v3.rc1.common.LastModifiedDate(gc2));
        rc1Work.setPath("/0000-0000-0000-0000/rcX/work/123");

        rc1Work.setPublicationDate(new org.orcid.jaxb.model.v3.rc1.common.PublicationDate(new org.orcid.jaxb.model.v3.rc1.common.Year(2018),
                new org.orcid.jaxb.model.v3.rc1.common.Month(1), new org.orcid.jaxb.model.v3.rc1.common.Day(1)));

        rc1Work.setPutCode(123L);
        rc1Work.setShortDescription("Short description");
        rc1Work.setSource(new org.orcid.jaxb.model.v3.rc1.common.Source("0000-0000-0000-0000"));
        rc1Work.setUrl(new org.orcid.jaxb.model.v3.rc1.common.Url("http://www.orcid.org"));
        rc1Work.setVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility.LIMITED);

        rc1Work.setWorkCitation(
                new org.orcid.jaxb.model.v3.rc1.record.Citation("This is the citation", org.orcid.jaxb.model.v3.rc1.record.CitationType.FORMATTED_UNSPECIFIED));
        org.orcid.jaxb.model.v3.rc1.common.Contributor c = new org.orcid.jaxb.model.v3.rc1.common.Contributor();
        org.orcid.jaxb.model.v3.rc1.common.ContributorAttributes ca = new org.orcid.jaxb.model.v3.rc1.common.ContributorAttributes();
        ca.setContributorRole(org.orcid.jaxb.model.v3.rc1.common.ContributorRole.ASSIGNEE);
        ca.setContributorSequence(org.orcid.jaxb.model.v3.rc1.record.SequenceType.ADDITIONAL);
        c.setContributorAttributes(ca);
        c.setContributorEmail(new org.orcid.jaxb.model.v3.rc1.common.ContributorEmail("contributor@orcid.org"));
        c.setContributorOrcid(new org.orcid.jaxb.model.v3.rc1.common.ContributorOrcid("0000-0000-0000-0000"));
        c.setCreditName(new org.orcid.jaxb.model.v3.rc1.common.CreditName("Credit Name"));
        org.orcid.jaxb.model.v3.rc1.record.WorkContributors wc = new org.orcid.jaxb.model.v3.rc1.record.WorkContributors();
        wc.getContributor().add(c);
        rc1Work.setWorkContributors(wc);
        org.orcid.jaxb.model.v3.rc1.record.ExternalIDs extIds = new org.orcid.jaxb.model.v3.rc1.record.ExternalIDs();
        org.orcid.jaxb.model.v3.rc1.record.ExternalID extId = new org.orcid.jaxb.model.v3.rc1.record.ExternalID();
        extId.setRelationship(org.orcid.jaxb.model.v3.rc1.record.Relationship.SELF);
        extId.setType("type");
        extId.setUrl(new org.orcid.jaxb.model.v3.rc1.common.Url("http://www.orcid.org"));
        extId.setValue("extId1");
        extIds.getExternalIdentifier().add(extId);
        rc1Work.setWorkExternalIdentifiers(extIds);
        org.orcid.jaxb.model.v3.rc1.record.WorkTitle title = new org.orcid.jaxb.model.v3.rc1.record.WorkTitle();
        title.setSubtitle(new org.orcid.jaxb.model.v3.rc1.common.Subtitle("The subtitle"));
        title.setTitle(new org.orcid.jaxb.model.v3.rc1.common.Title("The title"));
        title.setTranslatedTitle(new org.orcid.jaxb.model.v3.rc1.common.TranslatedTitle("Translated", "en"));
        rc1Work.setWorkTitle(title);
        rc1Work.setWorkType(org.orcid.jaxb.model.v3.rc1.record.WorkType.DISSERTATION);

        // Map and test
        org.orcid.jaxb.model.v3.rc2.record.Work rc2Work = (org.orcid.jaxb.model.v3.rc2.record.Work) (v3VersionConverterChain
                .upgrade(new V3Convertible(rc1Work, "3.0_rc1"), "3.0_rc2")).getObjectToConvert();

        assertNotNull(rc2Work);
        assertEquals(org.orcid.jaxb.model.common.Iso3166Country.US, rc2Work.getCountry().getValue());
        assertEquals(new org.orcid.jaxb.model.v3.rc2.common.CreatedDate(gc1), rc2Work.getCreatedDate());
        assertEquals("Journal title", rc2Work.getJournalTitle().getContent());
        assertEquals("en", rc2Work.getLanguageCode());
        assertEquals(new org.orcid.jaxb.model.v3.rc2.common.LastModifiedDate(gc2), rc2Work.getLastModifiedDate());
        assertEquals("/0000-0000-0000-0000/rcX/work/123", rc2Work.getPath());
        assertEquals("2018", rc2Work.getPublicationDate().getYear().getValue());
        assertEquals("01", rc2Work.getPublicationDate().getMonth().getValue());
        assertEquals("01", rc2Work.getPublicationDate().getDay().getValue());
        assertEquals(Long.valueOf(123), rc2Work.getPutCode());
        assertEquals("Short description", rc2Work.getShortDescription());
        assertEquals(new org.orcid.jaxb.model.v3.rc2.common.Source("0000-0000-0000-0000"), rc2Work.getSource());
        assertEquals("http://www.orcid.org", rc2Work.getUrl().getValue());
        assertEquals(org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED, rc2Work.getVisibility());
        assertEquals("This is the citation", rc2Work.getWorkCitation().getCitation());
        assertEquals(org.orcid.jaxb.model.v3.rc2.record.CitationType.FORMATTED_UNSPECIFIED, rc2Work.getWorkCitation().getWorkCitationType());
        assertEquals(1, rc2Work.getWorkContributors().getContributor().size());

        assertEquals(org.orcid.jaxb.model.v3.rc2.common.ContributorRole.ASSIGNEE,
                rc2Work.getWorkContributors().getContributor().get(0).getContributorAttributes().getContributorRole());
        assertEquals(org.orcid.jaxb.model.v3.rc2.record.SequenceType.ADDITIONAL,
                rc2Work.getWorkContributors().getContributor().get(0).getContributorAttributes().getContributorSequence());
        assertEquals("contributor@orcid.org", rc2Work.getWorkContributors().getContributor().get(0).getContributorEmail().getValue());
        assertEquals("0000-0000-0000-0000", rc2Work.getWorkContributors().getContributor().get(0).getContributorOrcid().getPath());
        assertEquals("Credit Name", rc2Work.getWorkContributors().getContributor().get(0).getCreditName().getContent());
        assertEquals(1, rc2Work.getWorkExternalIdentifiers().getExternalIdentifier().size());
        assertEquals(org.orcid.jaxb.model.v3.rc2.record.Relationship.SELF, rc2Work.getWorkExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertEquals("type", rc2Work.getWorkExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("http://www.orcid.org", rc2Work.getWorkExternalIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals("extId1", rc2Work.getWorkExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("The subtitle", rc2Work.getWorkTitle().getSubtitle().getContent());
        assertEquals("The title", rc2Work.getWorkTitle().getTitle().getContent());
        assertEquals("Translated", rc2Work.getWorkTitle().getTranslatedTitle().getContent());
        assertEquals("en", rc2Work.getWorkTitle().getTranslatedTitle().getLanguageCode());
        assertEquals(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS, rc2Work.getWorkType());
        
        rc2Work = null;
        
        // Work summary test
        org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary rc1WorkSummary = new org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary();
        rc1WorkSummary.setCreatedDate(new org.orcid.jaxb.model.v3.rc1.common.CreatedDate(gc1));
        rc1WorkSummary.setDisplayIndex("1");
        rc1WorkSummary.setExternalIdentifiers(extIds);
        rc1WorkSummary.setJournalTitle(new org.orcid.jaxb.model.v3.rc1.common.Title("Journal title"));
        rc1WorkSummary.setLastModifiedDate(new org.orcid.jaxb.model.v3.rc1.common.LastModifiedDate(gc2));
        rc1WorkSummary.setPath("/0000-0000-0000-0000/rcX/work/123");
        rc1WorkSummary.setPublicationDate(new org.orcid.jaxb.model.v3.rc1.common.PublicationDate(new org.orcid.jaxb.model.v3.rc1.common.Year(2018),
                new org.orcid.jaxb.model.v3.rc1.common.Month(1), new org.orcid.jaxb.model.v3.rc1.common.Day(1)));
        rc1WorkSummary.setPutCode(123L);
        rc1WorkSummary.setSource(new org.orcid.jaxb.model.v3.rc1.common.Source("0000-0000-0000-0000"));
        rc1WorkSummary.setTitle(title);
        rc1WorkSummary.setType(org.orcid.jaxb.model.v3.rc1.record.WorkType.DISSERTATION);
        rc1WorkSummary.setUrl(new org.orcid.jaxb.model.v3.rc1.common.Url("http://www.orcid.org"));
        rc1WorkSummary.setVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility.LIMITED);

        org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary rc2WorkSummary = (org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary) (v3VersionConverterChain
                .upgrade(new V3Convertible(rc1WorkSummary, "3.0_rc1"), "3.0_rc2")).getObjectToConvert();

        assertNotNull(rc2WorkSummary);
        assertEquals(new org.orcid.jaxb.model.v3.rc2.common.CreatedDate(gc1), rc2WorkSummary.getCreatedDate());
        assertEquals("1", rc2WorkSummary.getDisplayIndex());
        assertEquals(1, rc2WorkSummary.getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals(org.orcid.jaxb.model.v3.rc2.record.Relationship.SELF, rc2WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertEquals("type", rc2WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("http://www.orcid.org", rc2WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals("extId1", rc2WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("Journal title", rc2WorkSummary.getJournalTitle().getContent());
        assertEquals(new org.orcid.jaxb.model.v3.rc2.common.LastModifiedDate(gc2), rc2WorkSummary.getLastModifiedDate());
        assertEquals("/0000-0000-0000-0000/rcX/work/123", rc2WorkSummary.getPath());
        assertEquals("2018", rc2WorkSummary.getPublicationDate().getYear().getValue());
        assertEquals("01", rc2WorkSummary.getPublicationDate().getMonth().getValue());
        assertEquals("01", rc2WorkSummary.getPublicationDate().getDay().getValue());
        assertEquals(Long.valueOf(123), rc2WorkSummary.getPutCode());
        assertEquals(new org.orcid.jaxb.model.v3.rc2.common.Source("0000-0000-0000-0000"), rc2WorkSummary.getSource());
        assertEquals("The subtitle", rc2WorkSummary.getTitle().getSubtitle().getContent());
        assertEquals("The title", rc2WorkSummary.getTitle().getTitle().getContent());
        assertEquals("Translated", rc2WorkSummary.getTitle().getTranslatedTitle().getContent());
        assertEquals("en", rc2WorkSummary.getTitle().getTranslatedTitle().getLanguageCode());
        assertEquals(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS, rc2WorkSummary.getType());
        assertEquals("http://www.orcid.org", rc2WorkSummary.getUrl().getValue());
        assertEquals(org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED, rc2WorkSummary.getVisibility());
    }

    @Test
    public void downgradeRC2ToRC1Test() {
        XMLGregorianCalendar gc1 = DateUtils.convertToXMLGregorianCalendar(new GregorianCalendar(2018, 1, 1));
        XMLGregorianCalendar gc2 = DateUtils.convertToXMLGregorianCalendar(new GregorianCalendar(2019, 1, 1));

        // Work test
        org.orcid.jaxb.model.v3.rc2.record.Work rc2Work = new org.orcid.jaxb.model.v3.rc2.record.Work();
        rc2Work.setCountry(new org.orcid.jaxb.model.v3.rc2.common.Country(org.orcid.jaxb.model.common.Iso3166Country.US));
        rc2Work.setCreatedDate(new org.orcid.jaxb.model.v3.rc2.common.CreatedDate(gc1));
        rc2Work.setJournalTitle(new org.orcid.jaxb.model.v3.rc2.common.Title("Journal title"));
        rc2Work.setLanguageCode("en");
        rc2Work.setLastModifiedDate(new org.orcid.jaxb.model.v3.rc2.common.LastModifiedDate(gc2));
        rc2Work.setPath("/0000-0000-0000-0000/rcX/work/123");
        rc2Work.setPublicationDate(new org.orcid.jaxb.model.v3.rc2.common.PublicationDate(new org.orcid.jaxb.model.v3.rc2.common.Year(2018),
                new org.orcid.jaxb.model.v3.rc2.common.Month(1), new org.orcid.jaxb.model.v3.rc2.common.Day(1)));
        rc2Work.setPutCode(123L);
        rc2Work.setShortDescription("Short description");
        rc2Work.setSource(new org.orcid.jaxb.model.v3.rc2.common.Source("0000-0000-0000-0000"));
        rc2Work.setUrl(new org.orcid.jaxb.model.v3.rc2.common.Url("http://www.orcid.org"));
        rc2Work.setVisibility(org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED);
        rc2Work.setWorkCitation(
                new org.orcid.jaxb.model.v3.rc2.record.Citation("This is the citation", org.orcid.jaxb.model.v3.rc2.record.CitationType.FORMATTED_UNSPECIFIED));
        org.orcid.jaxb.model.v3.rc2.common.Contributor c = new org.orcid.jaxb.model.v3.rc2.common.Contributor();
        org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes ca = new org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes();
        ca.setContributorRole(org.orcid.jaxb.model.v3.rc2.common.ContributorRole.ASSIGNEE);
        ca.setContributorSequence(org.orcid.jaxb.model.v3.rc2.record.SequenceType.ADDITIONAL);
        c.setContributorAttributes(ca);
        c.setContributorEmail(new org.orcid.jaxb.model.v3.rc2.common.ContributorEmail("contributor@orcid.org"));
        c.setContributorOrcid(new org.orcid.jaxb.model.v3.rc2.common.ContributorOrcid("0000-0000-0000-0000"));
        c.setCreditName(new org.orcid.jaxb.model.v3.rc2.common.CreditName("Credit Name"));
        org.orcid.jaxb.model.v3.rc2.record.WorkContributors wc = new org.orcid.jaxb.model.v3.rc2.record.WorkContributors();
        wc.getContributor().add(c);
        rc2Work.setWorkContributors(wc);
        org.orcid.jaxb.model.v3.rc2.record.ExternalIDs extIds = new org.orcid.jaxb.model.v3.rc2.record.ExternalIDs();
        org.orcid.jaxb.model.v3.rc2.record.ExternalID extId1 = new org.orcid.jaxb.model.v3.rc2.record.ExternalID();
        extId1.setRelationship(org.orcid.jaxb.model.v3.rc2.record.Relationship.SELF);
        extId1.setType("type");
        extId1.setUrl(new org.orcid.jaxb.model.v3.rc2.common.Url("http://www.orcid.org"));
        extId1.setValue("extId1");
        extIds.getExternalIdentifier().add(extId1);
        org.orcid.jaxb.model.v3.rc2.record.ExternalID extId2 = new org.orcid.jaxb.model.v3.rc2.record.ExternalID();
        extId2.setRelationship(org.orcid.jaxb.model.v3.rc2.record.Relationship.PART_OF);
        extId2.setType("type");
        extId2.setUrl(new org.orcid.jaxb.model.v3.rc2.common.Url("http://www.orcid.org"));
        extId2.setValue("extId2");
        extIds.getExternalIdentifier().add(extId2);
        rc2Work.setWorkExternalIdentifiers(extIds);
        org.orcid.jaxb.model.v3.rc2.record.WorkTitle title = new org.orcid.jaxb.model.v3.rc2.record.WorkTitle();
        title.setSubtitle(new org.orcid.jaxb.model.v3.rc2.common.Subtitle("The subtitle"));
        title.setTitle(new org.orcid.jaxb.model.v3.rc2.common.Title("The title"));
        title.setTranslatedTitle(new org.orcid.jaxb.model.v3.rc2.common.TranslatedTitle("Translated", "en"));
        rc2Work.setWorkTitle(title);
        rc2Work.setWorkType(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS);
        
        // Map and test
        org.orcid.jaxb.model.v3.rc1.record.Work rc1Work = (org.orcid.jaxb.model.v3.rc1.record.Work) (v3VersionConverterChain
                .downgrade(new V3Convertible(rc2Work, "3.0_rc2"), "3.0_rc1")).getObjectToConvert();

        assertNotNull(rc1Work);
        assertEquals(org.orcid.jaxb.model.v3.rc1.common.Iso3166Country.US, rc1Work.getCountry().getValue());
        assertEquals(new org.orcid.jaxb.model.v3.rc1.common.CreatedDate(gc1), rc1Work.getCreatedDate());
        assertEquals("Journal title", rc1Work.getJournalTitle().getContent());
        assertEquals("en", rc1Work.getLanguageCode());
        assertEquals(new org.orcid.jaxb.model.v3.rc1.common.LastModifiedDate(gc2), rc1Work.getLastModifiedDate());
        assertEquals("/0000-0000-0000-0000/rcX/work/123", rc1Work.getPath());
        assertEquals("2018", rc1Work.getPublicationDate().getYear().getValue());
        assertEquals("01", rc1Work.getPublicationDate().getMonth().getValue());
        assertEquals("01", rc1Work.getPublicationDate().getDay().getValue());
        assertEquals(Long.valueOf(123), rc1Work.getPutCode());
        assertEquals("Short description", rc1Work.getShortDescription());
        assertEquals(new org.orcid.jaxb.model.v3.rc1.common.Source("0000-0000-0000-0000"), rc1Work.getSource());
        assertEquals("http://www.orcid.org", rc1Work.getUrl().getValue());
        assertEquals(org.orcid.jaxb.model.v3.rc1.common.Visibility.LIMITED, rc1Work.getVisibility());
        assertEquals("This is the citation", rc1Work.getWorkCitation().getCitation());
        assertEquals(org.orcid.jaxb.model.v3.rc1.record.CitationType.FORMATTED_UNSPECIFIED, rc1Work.getWorkCitation().getWorkCitationType());
        assertEquals(1, rc1Work.getWorkContributors().getContributor().size());

        assertEquals(org.orcid.jaxb.model.v3.rc1.common.ContributorRole.ASSIGNEE,
                rc1Work.getWorkContributors().getContributor().get(0).getContributorAttributes().getContributorRole());
        assertEquals(org.orcid.jaxb.model.v3.rc1.record.SequenceType.ADDITIONAL,
                rc1Work.getWorkContributors().getContributor().get(0).getContributorAttributes().getContributorSequence());

        assertEquals("contributor@orcid.org", rc1Work.getWorkContributors().getContributor().get(0).getContributorEmail().getValue());
        assertEquals("0000-0000-0000-0000", rc1Work.getWorkContributors().getContributor().get(0).getContributorOrcid().getPath());
        assertEquals("Credit Name", rc1Work.getWorkContributors().getContributor().get(0).getCreditName().getContent());
        assertEquals(2, rc1Work.getWorkExternalIdentifiers().getExternalIdentifier().size());
        assertEquals(org.orcid.jaxb.model.v3.rc1.record.Relationship.SELF, rc1Work.getWorkExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertEquals("type", rc1Work.getWorkExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("http://www.orcid.org", rc1Work.getWorkExternalIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals("extId1", rc1Work.getWorkExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        
        assertEquals(org.orcid.jaxb.model.v3.rc1.record.Relationship.PART_OF, rc1Work.getWorkExternalIdentifiers().getExternalIdentifier().get(1).getRelationship());
        assertEquals("type", rc1Work.getWorkExternalIdentifiers().getExternalIdentifier().get(1).getType());
        assertEquals("http://www.orcid.org", rc1Work.getWorkExternalIdentifiers().getExternalIdentifier().get(1).getUrl().getValue());
        assertEquals("extId2", rc1Work.getWorkExternalIdentifiers().getExternalIdentifier().get(1).getValue());        
        
        assertEquals("The subtitle", rc1Work.getWorkTitle().getSubtitle().getContent());
        assertEquals("The title", rc1Work.getWorkTitle().getTitle().getContent());
        assertEquals("Translated", rc1Work.getWorkTitle().getTranslatedTitle().getContent());
        assertEquals("en", rc1Work.getWorkTitle().getTranslatedTitle().getLanguageCode());

        assertEquals(org.orcid.jaxb.model.v3.rc1.record.WorkType.DISSERTATION, rc1Work.getWorkType());

        // Work summary test
        org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary rc2WorkSummary = new org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary();
        rc2WorkSummary.setCreatedDate(new org.orcid.jaxb.model.v3.rc2.common.CreatedDate(gc1));
        rc2WorkSummary.setDisplayIndex("1");
        rc2WorkSummary.setExternalIdentifiers(extIds);
        rc2WorkSummary.setJournalTitle(new org.orcid.jaxb.model.v3.rc2.common.Title("Journal title"));
        rc2WorkSummary.setLastModifiedDate(new org.orcid.jaxb.model.v3.rc2.common.LastModifiedDate(gc2));
        rc2WorkSummary.setPath("/0000-0000-0000-0000/rcX/work/123");
        rc2WorkSummary.setPublicationDate(new org.orcid.jaxb.model.v3.rc2.common.PublicationDate(new org.orcid.jaxb.model.v3.rc2.common.Year(2018),
                new org.orcid.jaxb.model.v3.rc2.common.Month(1), new org.orcid.jaxb.model.v3.rc2.common.Day(1)));
        rc2WorkSummary.setPutCode(123L);
        rc2WorkSummary.setSource(new org.orcid.jaxb.model.v3.rc2.common.Source("0000-0000-0000-0000"));
        rc2WorkSummary.setTitle(title);
        rc2WorkSummary.setType(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS);
        rc2WorkSummary.setUrl(new org.orcid.jaxb.model.v3.rc2.common.Url("http://www.orcid.org"));
        rc2WorkSummary.setVisibility(org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED);

        org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary rc1WorkSummary = (org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary) (v3VersionConverterChain
                .downgrade(new V3Convertible(rc2WorkSummary, "3.0_rc2"), "3.0_rc1")).getObjectToConvert();
        assertNotNull(rc1WorkSummary);
        assertEquals(new org.orcid.jaxb.model.v3.rc1.common.CreatedDate(gc1), rc1WorkSummary.getCreatedDate());
        assertEquals("1", rc1WorkSummary.getDisplayIndex());
        assertEquals(2, rc1WorkSummary.getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals(org.orcid.jaxb.model.v3.rc1.record.Relationship.SELF, rc1WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertEquals("type", rc1WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("http://www.orcid.org", rc1WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals("extId1", rc1WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        
        assertEquals(org.orcid.jaxb.model.v3.rc1.record.Relationship.PART_OF, rc1WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(1).getRelationship());
        assertEquals("type", rc1WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(1).getType());
        assertEquals("http://www.orcid.org", rc1WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(1).getUrl().getValue());
        assertEquals("extId2", rc1WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(1).getValue());        
        
        assertEquals("Journal title", rc1WorkSummary.getJournalTitle().getContent());
        assertEquals(new org.orcid.jaxb.model.v3.rc1.common.LastModifiedDate(gc2), rc1WorkSummary.getLastModifiedDate());
        assertEquals("/0000-0000-0000-0000/rcX/work/123", rc1WorkSummary.getPath());
        assertEquals("2018", rc1WorkSummary.getPublicationDate().getYear().getValue());
        assertEquals("01", rc1WorkSummary.getPublicationDate().getMonth().getValue());
        assertEquals("01", rc1WorkSummary.getPublicationDate().getDay().getValue());
        assertEquals(Long.valueOf(123), rc1WorkSummary.getPutCode());
        assertEquals(new org.orcid.jaxb.model.v3.rc1.common.Source("0000-0000-0000-0000"), rc1WorkSummary.getSource());
        assertEquals("The subtitle", rc1WorkSummary.getTitle().getSubtitle().getContent());
        assertEquals("The title", rc1WorkSummary.getTitle().getTitle().getContent());
        assertEquals("Translated", rc1WorkSummary.getTitle().getTranslatedTitle().getContent());
        assertEquals("en", rc1WorkSummary.getTitle().getTranslatedTitle().getLanguageCode());
        assertEquals(org.orcid.jaxb.model.v3.rc1.record.WorkType.DISSERTATION, rc1WorkSummary.getType());
        assertEquals("http://www.orcid.org", rc1WorkSummary.getUrl().getValue());
        assertEquals(org.orcid.jaxb.model.v3.rc1.common.Visibility.LIMITED, rc1WorkSummary.getVisibility());
    }
}