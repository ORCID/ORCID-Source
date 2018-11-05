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
        
        org.orcid.jaxb.model.v3.rc1.record.Work rc1Work = new org.orcid.jaxb.model.v3.rc1.record.Work();
        // Set work type
        rc1Work.setWorkType(org.orcid.jaxb.model.v3.rc1.record.WorkType.DISSERTATION);
        rc1Work.setCountry(new org.orcid.jaxb.model.v3.rc1.common.Country(org.orcid.jaxb.model.v3.rc1.common.Iso3166Country.US));                
        rc1Work.setCreatedDate(new org.orcid.jaxb.model.v3.rc1.common.CreatedDate(gc1));        
        rc1Work.setJournalTitle(new org.orcid.jaxb.model.v3.rc1.common.Title("Journal title"));
        rc1Work.setLanguageCode("EN");
        rc1Work.setLastModifiedDate(new org.orcid.jaxb.model.v3.rc1.common.LastModifiedDate(gc2));
        rc1Work.setPath("/0000-0000-0000-0000/rcX/work/123");
        rc1Work.setPublicationDate(new org.orcid.jaxb.model.v3.rc1.common.PublicationDate(new org.orcid.jaxb.model.v3.rc1.common.Year(2018), new org.orcid.jaxb.model.v3.rc1.common.Month(1), new org.orcid.jaxb.model.v3.rc1.common.Day(1)));
        rc1Work.setPutCode(123L);
        rc1Work.setShortDescription("Short description");
        rc1Work.setSource(new org.orcid.jaxb.model.v3.rc1.common.Source("0000-0000-0000-0000"));
        rc1Work.setUrl(new org.orcid.jaxb.model.v3.rc1.common.Url("http://www.orcid.org"));
        rc1Work.setVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility.LIMITED);
        rc1Work.setWorkCitation(new org.orcid.jaxb.model.v3.rc1.record.Citation("This is the citation", org.orcid.jaxb.model.v3.rc1.record.CitationType.FORMATTED_UNSPECIFIED));
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
        
        rc1Work.setWorkExternalIdentifiers();
        rc1Work.setWorkTitle();
        rc1Work.setWorkType();
        
        // TODO Set external identifiers        
        
        
        org.orcid.jaxb.model.v3.rc2.record.Work rc2Work = (org.orcid.jaxb.model.v3.rc2.record.Work)(v3VersionConverterChain.upgrade(new V3Convertible(rc1Work, "3.0_rc1"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2Work);
        // Verify work type
        assertEquals(org.orcid.jaxb.model.v3.rc2.record.WorkType.DISSERTATION_THESIS, rc2Work.getWorkType());
        // TODO Verify external identifiers
        
        
        org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary rc1WorkSummary = new org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary();
        // Set work type
        rc1WorkSummary.setType(org.orcid.jaxb.model.v3.rc1.record.WorkType.DISSERTATION);
        // TODO set external identifiers
        org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary rc2WorkSummary = (org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary)(v3VersionConverterChain.upgrade(new V3Convertible(rc1WorkSummary, "3.0_rc1"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2WorkSummary);
        // Verify work type
        assertEquals(org.orcid.jaxb.model.v3.rc2.record.WorkType.DISSERTATION_THESIS, rc2WorkSummary.getType());
        //TODO verify external identifiers
        
    }
    
    @Test
    public void downgradeRC2ToRC1Test() {
        org.orcid.jaxb.model.v3.rc2.record.Work rc2Work = new org.orcid.jaxb.model.v3.rc2.record.Work();
        rc2Work.setWorkType(org.orcid.jaxb.model.v3.rc2.record.WorkType.DISSERTATION_THESIS);
        org.orcid.jaxb.model.v3.rc1.record.Work rc1Work = (org.orcid.jaxb.model.v3.rc1.record.Work)(v3VersionConverterChain.downgrade(new V3Convertible(rc2Work, "3.0_rc2"), "3.0_rc1")).getObjectToConvert();
        assertNotNull(rc1Work);
        assertEquals(org.orcid.jaxb.model.v3.rc1.record.WorkType.DISSERTATION, rc1Work.getWorkType());
    
        org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary rc2WorkSummary = new org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary();
        rc2WorkSummary.setType(org.orcid.jaxb.model.v3.rc2.record.WorkType.DISSERTATION_THESIS);
        org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary rc1WorkSummary = (org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary)(v3VersionConverterChain.downgrade(new V3Convertible(rc2WorkSummary, "3.0_rc2"), "3.0_rc1")).getObjectToConvert();
        assertNotNull(rc1WorkSummary);
        assertEquals(org.orcid.jaxb.model.v3.rc1.record.WorkType.DISSERTATION, rc1WorkSummary.getType());
    }
}
