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
import org.orcid.jaxb.model.groupid_rc3.GroupIdRecords;
import org.orcid.jaxb.model.record.summary_rc3.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc3.Educations;
import org.orcid.jaxb.model.record.summary_rc3.Employments;
import org.orcid.jaxb.model.record.summary_rc3.Fundings;
import org.orcid.jaxb.model.record.summary_rc3.PeerReviews;
import org.orcid.jaxb.model.record.summary_rc3.Works;
import org.orcid.jaxb.model.record_rc3.Addresses;

public class ConvertVrc3ToVrc4Test extends BaseTest {

    @Resource
    private V2VersionConverter versionConverterV2_0_rc3ToV2_0_rc4; 

    @Test
    public void upgradeActivitiesToVrc3Test() throws JAXBException {

        JAXBContext jaxbContext1 = JAXBContext.newInstance(ActivitiesSummary.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record.summary_rc4.ActivitiesSummary.class);
        Unmarshaller jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
        Unmarshaller jaxbUnmarshaller2 = jaxbContext2.createUnmarshaller();

        InputStream rc3Stream = ConvertVrc3ToVrc4Test.class.getClassLoader().getResourceAsStream("test-activities-2.0_rc3.xml");
        InputStream rc4Stream = ConvertVrc3ToVrc4Test.class.getClassLoader().getResourceAsStream("test-activities-2.0_rc4.xml");

        ActivitiesSummary rc3Activities = (ActivitiesSummary) jaxbUnmarshaller1.unmarshal(rc3Stream);

        org.orcid.jaxb.model.record.summary_rc4.ActivitiesSummary rc4Activities1 = 
                (org.orcid.jaxb.model.record.summary_rc4.ActivitiesSummary) jaxbUnmarshaller2.unmarshal(rc4Stream);

        V2Convertible result = versionConverterV2_0_rc3ToV2_0_rc4.upgrade(new V2Convertible(rc3Activities, "v2_rc3"));
        org.orcid.jaxb.model.record.summary_rc4.ActivitiesSummary rc4Activities2 = (org.orcid.jaxb.model.record.summary_rc4.ActivitiesSummary) result
                .getObjectToConvert();
        
        assertEquals(rc4Activities1, rc4Activities2); 
    }

    @Test
    public void upgradeEducationsToVrc3Test() throws JAXBException {

        JAXBContext jaxbContext1 = JAXBContext.newInstance(Educations.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record.summary_rc4.Educations.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc3Stream = ConvertVrc3ToVrc4Test.class.getClassLoader().getResourceAsStream("test-educations-2.0_rc3.xml");
        InputStream rc4Stream = ConvertVrc3ToVrc4Test.class.getClassLoader().getResourceAsStream("test-educations-2.0_rc4.xml");

        Educations rc4Educations = (Educations) jaxbUnmarshaller.unmarshal(rc3Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.record.summary_rc4.Educations rc4Educations1 = (org.orcid.jaxb.model.record.summary_rc4.Educations) jaxbUnmarshaller.unmarshal(rc4Stream);

        V2Convertible result = versionConverterV2_0_rc3ToV2_0_rc4.upgrade(new V2Convertible(rc4Educations, "v2_rc3"));
        org.orcid.jaxb.model.record.summary_rc4.Educations rc4Educations2 = (org.orcid.jaxb.model.record.summary_rc4.Educations) result.getObjectToConvert();

        assertEquals(rc4Educations1, rc4Educations2);
    }

    @Test
    public void upgradeEmploymentsToVrc3Test() throws JAXBException {

        JAXBContext jaxbContext1 = JAXBContext.newInstance(Employments.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record.summary_rc4.Employments.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc3Stream = ConvertVrc3ToVrc4Test.class.getClassLoader().getResourceAsStream("test-employments-2.0_rc3.xml");
        InputStream rc4Stream = ConvertVrc3ToVrc4Test.class.getClassLoader().getResourceAsStream("test-employments-2.0_rc4.xml");

        Employments rc4Employments = (Employments) jaxbUnmarshaller.unmarshal(rc3Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.record.summary_rc4.Employments rc4Employments1 = (org.orcid.jaxb.model.record.summary_rc4.Employments) jaxbUnmarshaller.unmarshal(rc4Stream);

        V2Convertible result = versionConverterV2_0_rc3ToV2_0_rc4.upgrade(new V2Convertible(rc4Employments, "v2_rc3"));
        org.orcid.jaxb.model.record.summary_rc4.Employments rc4Employments2 = (org.orcid.jaxb.model.record.summary_rc4.Employments) result.getObjectToConvert();

        assertEquals(rc4Employments1, rc4Employments2);
    }

    @Test
    public void upgradeFundingsToVrc3Test() throws JAXBException {

        JAXBContext jaxbContext1 = JAXBContext.newInstance(Fundings.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record.summary_rc4.Fundings.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc3Stream = ConvertVrc3ToVrc4Test.class.getClassLoader().getResourceAsStream("test-fundings-2.0_rc3.xml");
        InputStream rc4Stream = ConvertVrc3ToVrc4Test.class.getClassLoader().getResourceAsStream("test-fundings-2.0_rc4.xml");

        Fundings rc4Fundings = (Fundings) jaxbUnmarshaller.unmarshal(rc3Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.record.summary_rc4.Fundings rc4Fundings1 = (org.orcid.jaxb.model.record.summary_rc4.Fundings) jaxbUnmarshaller.unmarshal(rc4Stream);

        V2Convertible result = versionConverterV2_0_rc3ToV2_0_rc4.upgrade(new V2Convertible(rc4Fundings, "v2_rc3"));
        org.orcid.jaxb.model.record.summary_rc4.Fundings rc4Fundings2 = (org.orcid.jaxb.model.record.summary_rc4.Fundings) result.getObjectToConvert();

        assertEquals(rc4Fundings1, rc4Fundings2);
    }

    @Test
    public void upgradePeerReviewsToVrc3Test() throws JAXBException {
        JAXBContext jaxbContext1 = JAXBContext.newInstance(PeerReviews.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record.summary_rc4.PeerReviews.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc3Stream = ConvertVrc3ToVrc4Test.class.getClassLoader().getResourceAsStream("test-peer-reviews-2.0_rc3.xml");
        InputStream rc4Stream = ConvertVrc3ToVrc4Test.class.getClassLoader().getResourceAsStream("test-peer-reviews-2.0_rc4.xml");

        PeerReviews rc4PeerReviews = (PeerReviews) jaxbUnmarshaller.unmarshal(rc3Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.record.summary_rc4.PeerReviews rc4PeerReviews1 = (org.orcid.jaxb.model.record.summary_rc4.PeerReviews) jaxbUnmarshaller.unmarshal(rc4Stream);

        V2Convertible result = versionConverterV2_0_rc3ToV2_0_rc4.upgrade(new V2Convertible(rc4PeerReviews, "v2_rc3"));
        org.orcid.jaxb.model.record.summary_rc4.PeerReviews rc4PeerReviews2 = (org.orcid.jaxb.model.record.summary_rc4.PeerReviews) result.getObjectToConvert();

        assertEquals(rc4PeerReviews1, rc4PeerReviews2);
    }

    @Test
    public void upgradeWorksToVrc3Test() throws JAXBException {

        JAXBContext jaxbContext1 = JAXBContext.newInstance(Works.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record.summary_rc4.Works.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc3Stream = ConvertVrc3ToVrc4Test.class.getClassLoader().getResourceAsStream("test-works-2.0_rc3.xml");
        InputStream rc4Stream = ConvertVrc3ToVrc4Test.class.getClassLoader().getResourceAsStream("test-works-2.0_rc4.xml");

        Works rc4Works = (Works) jaxbUnmarshaller.unmarshal(rc3Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.record.summary_rc4.Works rc4Works1 = (org.orcid.jaxb.model.record.summary_rc4.Works) jaxbUnmarshaller.unmarshal(rc4Stream);

        V2Convertible result = versionConverterV2_0_rc3ToV2_0_rc4.upgrade(new V2Convertible(rc4Works, "v2_rc3"));
        org.orcid.jaxb.model.record.summary_rc4.Works rc4Works2 = (org.orcid.jaxb.model.record.summary_rc4.Works) result.getObjectToConvert();

        assertEquals(rc4Works1, rc4Works2);
    }
    
    @Test
    public void upgradeGroupIdToVrc3Test() throws JAXBException {
        JAXBContext jaxbContext1 = JAXBContext.newInstance(GroupIdRecords.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.groupid_rc4.GroupIdRecords.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc3Stream = ConvertVrc3ToVrc4Test.class.getClassLoader().getResourceAsStream("test-group-id-2.0_rc3.xml");
        InputStream rc4Stream = ConvertVrc3ToVrc4Test.class.getClassLoader().getResourceAsStream("test-group-id-2.0_rc4.xml");

        GroupIdRecords rc4Group = (GroupIdRecords) jaxbUnmarshaller.unmarshal(rc3Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.groupid_rc4.GroupIdRecords rc4GroupId1 = (org.orcid.jaxb.model.groupid_rc4.GroupIdRecords) jaxbUnmarshaller.unmarshal(rc4Stream);

        V2Convertible result = versionConverterV2_0_rc3ToV2_0_rc4.upgrade(new V2Convertible(rc4Group, "v2_rc3"));
        org.orcid.jaxb.model.groupid_rc4.GroupIdRecords rc4GroupId2 = (org.orcid.jaxb.model.groupid_rc4.GroupIdRecords) result.getObjectToConvert();

        assertEquals(rc4GroupId1, rc4GroupId2);
    }
    
    @Test
    public void upgradeAddressesToVrc3Test() throws JAXBException {
        JAXBContext jaxbContext1 = JAXBContext.newInstance(Addresses.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record_rc4.Addresses.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc3Stream = ConvertVrc3ToVrc4Test.class.getClassLoader().getResourceAsStream("test-addresses-2.0_rc3.xml");
        InputStream rc4Stream = ConvertVrc3ToVrc4Test.class.getClassLoader().getResourceAsStream("test-addresses-2.0_rc4.xml");

        Addresses rc4Element = (Addresses) jaxbUnmarshaller.unmarshal(rc3Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.record_rc4.Addresses rc4Element1 = (org.orcid.jaxb.model.record_rc4.Addresses) jaxbUnmarshaller.unmarshal(rc4Stream);

        V2Convertible result = versionConverterV2_0_rc3ToV2_0_rc4.upgrade(new V2Convertible(rc4Element, "v2_rc3"));
        org.orcid.jaxb.model.record_rc4.Addresses rc4Element2 = (org.orcid.jaxb.model.record_rc4.Addresses) result.getObjectToConvert();

        assertEquals(rc4Element1, rc4Element2);
    }        
}
