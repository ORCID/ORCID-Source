package org.orcid.record_2_0;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.core.version.V2Convertible;
import org.orcid.core.version.V2VersionConverter;
import org.orcid.jaxb.model.groupid_rc2.GroupIdRecords;
import org.orcid.jaxb.model.record.summary_rc2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc2.Educations;
import org.orcid.jaxb.model.record.summary_rc2.Employments;
import org.orcid.jaxb.model.record.summary_rc2.Fundings;
import org.orcid.jaxb.model.record.summary_rc2.PeerReviews;
import org.orcid.jaxb.model.record.summary_rc2.Works;
import org.orcid.jaxb.model.record_rc2.Addresses;

public class ConvertVrc2ToVrc3Test extends BaseTest {

    @Resource
    private V2VersionConverter versionConverterV2_0_rc2ToV2_0_rc3; 

    @Test
    public void upgradeActivitiesToVrc3Test() throws JAXBException {

        JAXBContext jaxbContext1 = JAXBContext.newInstance(ActivitiesSummary.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record.summary_rc3.ActivitiesSummary.class);
        Unmarshaller jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
        Unmarshaller jaxbUnmarshaller2 = jaxbContext2.createUnmarshaller();

        InputStream rc2Stream = ConvertVrc2ToVrc3Test.class.getClassLoader().getResourceAsStream("test-activities-2.0_rc2.xml");
        InputStream rc3Stream = ConvertVrc2ToVrc3Test.class.getClassLoader().getResourceAsStream("test-activities-2.0_rc3.xml");

        ActivitiesSummary rc2Activities = (ActivitiesSummary) jaxbUnmarshaller1.unmarshal(rc2Stream);

        org.orcid.jaxb.model.record.summary_rc3.ActivitiesSummary rc3Activities1 = 
                (org.orcid.jaxb.model.record.summary_rc3.ActivitiesSummary) jaxbUnmarshaller2.unmarshal(rc3Stream);

        V2Convertible result = versionConverterV2_0_rc2ToV2_0_rc3.upgrade(new V2Convertible(rc2Activities, "v2_rc2"));
        org.orcid.jaxb.model.record.summary_rc3.ActivitiesSummary rc3Activities2 = (org.orcid.jaxb.model.record.summary_rc3.ActivitiesSummary) result
                .getObjectToConvert();
        
        assertEquals(rc3Activities1, rc3Activities2); 
    }

    @Test
    public void upgradeEducationsToVrc3Test() throws JAXBException {

        JAXBContext jaxbContext1 = JAXBContext.newInstance(Educations.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record.summary_rc3.Educations.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc2Stream = ConvertVrc2ToVrc3Test.class.getClassLoader().getResourceAsStream("test-educations-2.0_rc2.xml");
        InputStream rc3Stream = ConvertVrc2ToVrc3Test.class.getClassLoader().getResourceAsStream("test-educations-2.0_rc3.xml");

        Educations rc2Educations = (Educations) jaxbUnmarshaller.unmarshal(rc2Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.record.summary_rc3.Educations rc3Educations1 = (org.orcid.jaxb.model.record.summary_rc3.Educations) jaxbUnmarshaller.unmarshal(rc3Stream);

        V2Convertible result = versionConverterV2_0_rc2ToV2_0_rc3.upgrade(new V2Convertible(rc2Educations, "v2_rc2"));
        org.orcid.jaxb.model.record.summary_rc3.Educations rc3Educations2 = (org.orcid.jaxb.model.record.summary_rc3.Educations) result.getObjectToConvert();

        assertEquals(rc3Educations1, rc3Educations2);
    }

    @Test
    public void upgradeEmploymentsToVrc3Test() throws JAXBException {

        JAXBContext jaxbContext1 = JAXBContext.newInstance(Employments.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record.summary_rc3.Employments.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc2Stream = ConvertVrc2ToVrc3Test.class.getClassLoader().getResourceAsStream("test-employments-2.0_rc2.xml");
        InputStream rc3Stream = ConvertVrc2ToVrc3Test.class.getClassLoader().getResourceAsStream("test-employments-2.0_rc3.xml");

        Employments rc2Employments = (Employments) jaxbUnmarshaller.unmarshal(rc2Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.record.summary_rc3.Employments rc3Employments1 = (org.orcid.jaxb.model.record.summary_rc3.Employments) jaxbUnmarshaller.unmarshal(rc3Stream);

        V2Convertible result = versionConverterV2_0_rc2ToV2_0_rc3.upgrade(new V2Convertible(rc2Employments, "v2_rc2"));
        org.orcid.jaxb.model.record.summary_rc3.Employments rc3Employments2 = (org.orcid.jaxb.model.record.summary_rc3.Employments) result.getObjectToConvert();

        assertEquals(rc3Employments1, rc3Employments2);
    }

    @Test
    public void upgradeFundingsToVrc3Test() throws JAXBException {

        JAXBContext jaxbContext1 = JAXBContext.newInstance(Fundings.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record.summary_rc3.Fundings.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc2Stream = ConvertVrc2ToVrc3Test.class.getClassLoader().getResourceAsStream("test-fundings-2.0_rc2.xml");
        InputStream rc3Stream = ConvertVrc2ToVrc3Test.class.getClassLoader().getResourceAsStream("test-fundings-2.0_rc3.xml");

        Fundings rc2Fundings = (Fundings) jaxbUnmarshaller.unmarshal(rc2Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.record.summary_rc3.Fundings rc3Fundings1 = (org.orcid.jaxb.model.record.summary_rc3.Fundings) jaxbUnmarshaller.unmarshal(rc3Stream);

        V2Convertible result = versionConverterV2_0_rc2ToV2_0_rc3.upgrade(new V2Convertible(rc2Fundings, "v2_rc2"));
        org.orcid.jaxb.model.record.summary_rc3.Fundings rc3Fundings2 = (org.orcid.jaxb.model.record.summary_rc3.Fundings) result.getObjectToConvert();

        assertEquals(rc3Fundings1, rc3Fundings2);
    }

    @Test
    public void upgradePeerReviewsToVrc3Test() throws JAXBException {
        JAXBContext jaxbContext1 = JAXBContext.newInstance(PeerReviews.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record.summary_rc3.PeerReviews.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc2Stream = ConvertVrc2ToVrc3Test.class.getClassLoader().getResourceAsStream("test-peer-reviews-2.0_rc2.xml");
        InputStream rc3Stream = ConvertVrc2ToVrc3Test.class.getClassLoader().getResourceAsStream("test-peer-reviews-2.0_rc3.xml");

        PeerReviews rc2PeerReviews = (PeerReviews) jaxbUnmarshaller.unmarshal(rc2Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.record.summary_rc3.PeerReviews rc3PeerReviews1 = (org.orcid.jaxb.model.record.summary_rc3.PeerReviews) jaxbUnmarshaller.unmarshal(rc3Stream);

        V2Convertible result = versionConverterV2_0_rc2ToV2_0_rc3.upgrade(new V2Convertible(rc2PeerReviews, "v2_rc2"));
        org.orcid.jaxb.model.record.summary_rc3.PeerReviews rc3PeerReviews2 = (org.orcid.jaxb.model.record.summary_rc3.PeerReviews) result.getObjectToConvert();

        assertEquals(rc3PeerReviews1, rc3PeerReviews2);
    }

    @Test
    public void upgradeWorksToVrc3Test() throws JAXBException {

        JAXBContext jaxbContext1 = JAXBContext.newInstance(Works.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record.summary_rc3.Works.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc2Stream = ConvertVrc2ToVrc3Test.class.getClassLoader().getResourceAsStream("test-works-2.0_rc2.xml");
        InputStream rc3Stream = ConvertVrc2ToVrc3Test.class.getClassLoader().getResourceAsStream("test-works-2.0_rc3.xml");

        Works rc2Works = (Works) jaxbUnmarshaller.unmarshal(rc2Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.record.summary_rc3.Works rc3Works1 = (org.orcid.jaxb.model.record.summary_rc3.Works) jaxbUnmarshaller.unmarshal(rc3Stream);

        V2Convertible result = versionConverterV2_0_rc2ToV2_0_rc3.upgrade(new V2Convertible(rc2Works, "v2_rc2"));
        org.orcid.jaxb.model.record.summary_rc3.Works rc3Works2 = (org.orcid.jaxb.model.record.summary_rc3.Works) result.getObjectToConvert();

        assertEquals(rc3Works1, rc3Works2);
    }
    
    @Test
    public void upgradeGroupIdToVrc3Test() throws JAXBException {
        JAXBContext jaxbContext1 = JAXBContext.newInstance(GroupIdRecords.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.groupid_rc3.GroupIdRecords.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc2Stream = ConvertVrc2ToVrc3Test.class.getClassLoader().getResourceAsStream("test-group-id-2.0_rc2.xml");
        InputStream rc3Stream = ConvertVrc2ToVrc3Test.class.getClassLoader().getResourceAsStream("test-group-id-2.0_rc3.xml");

        GroupIdRecords rc2Group = (GroupIdRecords) jaxbUnmarshaller.unmarshal(rc2Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.groupid_rc3.GroupIdRecords rc3GroupId1 = (org.orcid.jaxb.model.groupid_rc3.GroupIdRecords) jaxbUnmarshaller.unmarshal(rc3Stream);

        V2Convertible result = versionConverterV2_0_rc2ToV2_0_rc3.upgrade(new V2Convertible(rc2Group, "v2_rc2"));
        org.orcid.jaxb.model.groupid_rc3.GroupIdRecords rc3GroupId2 = (org.orcid.jaxb.model.groupid_rc3.GroupIdRecords) result.getObjectToConvert();

        assertEquals(rc3GroupId1, rc3GroupId2);
    }
    
    @Test
    public void upgradeAddressesToVrc3Test() throws JAXBException {
        JAXBContext jaxbContext1 = JAXBContext.newInstance(Addresses.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record_rc3.Addresses.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc2Stream = ConvertVrc2ToVrc3Test.class.getClassLoader().getResourceAsStream("test-addresses-2.0_rc2.xml");
        InputStream rc3Stream = ConvertVrc2ToVrc3Test.class.getClassLoader().getResourceAsStream("test-addresses-2.0_rc3.xml");

        Addresses rc2Element = (Addresses) jaxbUnmarshaller.unmarshal(rc2Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.record_rc3.Addresses rc3Element1 = (org.orcid.jaxb.model.record_rc3.Addresses) jaxbUnmarshaller.unmarshal(rc3Stream);

        V2Convertible result = versionConverterV2_0_rc2ToV2_0_rc3.upgrade(new V2Convertible(rc2Element, "v2_rc2"));
        org.orcid.jaxb.model.record_rc3.Addresses rc3Element2 = (org.orcid.jaxb.model.record_rc3.Addresses) result.getObjectToConvert();

        assertEquals(rc3Element1, rc3Element2);
    }
        
}
