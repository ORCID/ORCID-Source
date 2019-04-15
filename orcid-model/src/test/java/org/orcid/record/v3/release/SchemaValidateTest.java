package org.orcid.record.v3.release;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Fundings;
import org.orcid.jaxb.model.v3.release.record.summary.Works;
import org.orcid.jaxb.test.utils.OrcidTranslator;
import org.orcid.jaxb.test.utils.OrcidTranslator.SchemaVersion;

public class SchemaValidateTest {

  //checks our samples match our schema
    @Test
    public void testReadWorkFullSchemaValidate() throws JAXBException{
        OrcidTranslator<Work> t = OrcidTranslator.forSchema(SchemaVersion.V3_0_WORK);
        Reader reader = new InputStreamReader(getClass().getResourceAsStream("/record_3.0/samples/read_samples/work-full-3.0.xml"));
        Work w = t.readXmlRecord(reader);
        assertEquals("http://tempuri.org",w.getUrl().getValue());
        
        reader = new InputStreamReader(getClass().getResourceAsStream("/record_3.0/samples/write_samples/work-full-3.0.xml"));
        w = t.readXmlRecord(reader);
        assertEquals("http://alt-uri.org",w.getUrl().getValue());
    }
    
    //checks our samples match our schema
    @Test
    public void testReadFundingFullSchemaValidate() throws JAXBException{
        OrcidTranslator<Funding> t = OrcidTranslator.forSchema(SchemaVersion.V3_0_FUNDING);
        Reader reader = new InputStreamReader(getClass().getResourceAsStream("/record_3.0/samples/read_samples/funding-full-3.0.xml"));
        Funding w = t.readXmlRecord(reader);
        assertEquals("http://tempuri.org",w.getUrl().getValue());
        
        reader = new InputStreamReader(getClass().getResourceAsStream("/record_3.0/samples/write_samples/funding-3.0.xml"));
        w = t.readXmlRecord(reader);
        assertEquals("https://alt-url.org",w.getUrl().getValue());
    }
    
    //checks our samples match our schema
    @Test
    public void testReadWorksSchemaValidate() throws JAXBException{
        OrcidTranslator<Works> t = OrcidTranslator.forSchema(SchemaVersion.V3_0_WORKS);
        Reader reader = new InputStreamReader(getClass().getResourceAsStream("/record_3.0/samples/read_samples/works-3.0.xml"));
        Works w = t.readXmlRecord(reader);
        assertEquals("http://tempuri.org",w.getWorkGroup().get(0).getWorkSummary().get(0).getUrl().getValue());
    }
    
    //checks our samples match our schema
    @Test
    public void testReadFundingsSchemaValidate() throws JAXBException{
        OrcidTranslator<Fundings> t = OrcidTranslator.forSchema(SchemaVersion.V3_0_FUNDINGS);
        Reader reader = new InputStreamReader(getClass().getResourceAsStream("/record_3.0/samples/read_samples/fundings-3.0.xml"));
        Fundings w = t.readXmlRecord(reader);
        assertEquals("http://tempuri.org",w.getFundingGroup().get(0).getFundingSummary().get(0).getUrl().getValue());
    } 
    
    //checks our samples match our schema
    @Test
    public void testReadActivitiesSchemaValidate() throws JAXBException{
        OrcidTranslator<ActivitiesSummary> t = OrcidTranslator.forSchema(SchemaVersion.V3_0_ACTIVITIES);
        Reader reader = new InputStreamReader(getClass().getResourceAsStream("/record_3.0/samples/read_samples/activities-3.0.xml"));
        ActivitiesSummary w = t.readXmlRecord(reader);
        assertEquals("http://tempuri.org",w.getDistinctions().getDistinctionGroups().iterator().next().getActivities().get(0).getUrl().getValue());
        assertEquals("external-id-value",w.getEducations().getEducationGroups().iterator().next().getActivities().get(0).getExternalIDs().getExternalIdentifier().get(0).getValue());
    } 
    
    @Test
    public void testReadPeerReviewSchemaValidate() throws JAXBException {
        OrcidTranslator<PeerReview> t = OrcidTranslator.forSchema(SchemaVersion.V3_0_PEER_REVIEW);
        Reader reader = new InputStreamReader(getClass().getResourceAsStream("/record_3.0/samples/read_samples/peer-review-3.0.xml"));
        PeerReview p = t.readXmlRecord(reader);
        assertNotNull(p);
    }
}
