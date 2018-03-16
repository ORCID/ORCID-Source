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
import org.orcid.jaxb.model.groupid_v2.GroupIdRecords;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_v2.Educations;
import org.orcid.jaxb.model.record.summary_v2.Employments;
import org.orcid.jaxb.model.record.summary_v2.Fundings;
import org.orcid.jaxb.model.record.summary_v2.PeerReviews;
import org.orcid.jaxb.model.record.summary_v2.Works;
import org.orcid.jaxb.model.record_v2.Addresses;

public class ConvertV2ToV2_1Test extends BaseTest {

    @Resource
    private V2VersionConverter versionConverterV2_0ToV2_1;

    @Test
    public void upgradeActivitiesToV21Test() throws JAXBException {
        JAXBContext jaxbContext1 = JAXBContext.newInstance(ActivitiesSummary.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(ActivitiesSummary.class);
        Unmarshaller jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
        Unmarshaller jaxbUnmarshaller2 = jaxbContext2.createUnmarshaller();

        InputStream v20Stream = ConvertV2ToV2_1Test.class.getClassLoader().getResourceAsStream("test-activities-2.0.xml");
        InputStream v21Stream = ConvertV2ToV2_1Test.class.getClassLoader().getResourceAsStream("test-activities-2.1.xml");

        ActivitiesSummary v20Activities = (ActivitiesSummary) jaxbUnmarshaller1.unmarshal(v20Stream);

        ActivitiesSummary v21Activities1 = (ActivitiesSummary) jaxbUnmarshaller2.unmarshal(v21Stream);

        V2Convertible result = versionConverterV2_0ToV2_1.upgrade(new V2Convertible(v20Activities, "v2.1"));
        ActivitiesSummary v21Activities2 = (ActivitiesSummary) result.getObjectToConvert();

        assertEquals(v21Activities1, v21Activities2);
    }

    @Test
    public void upgradeEducationsToV21Test() throws JAXBException {
        JAXBContext jaxbContext1 = JAXBContext.newInstance(Educations.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(Educations.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream v20Stream = ConvertV2ToV2_1Test.class.getClassLoader().getResourceAsStream("test-educations-2.0.xml");
        InputStream v21Stream = ConvertV2ToV2_1Test.class.getClassLoader().getResourceAsStream("test-educations-2.1.xml");

        Educations v20Educations = (Educations) jaxbUnmarshaller.unmarshal(v20Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        Educations v21Educations1 = (Educations) jaxbUnmarshaller.unmarshal(v21Stream);

        V2Convertible result = versionConverterV2_0ToV2_1.upgrade(new V2Convertible(v20Educations, "v2.1"));
        Educations v21Educations2 = (Educations) result.getObjectToConvert();

        assertEquals(v21Educations1, v21Educations2);
    }

    @Test
    public void upgradeEmploymentsToV21Test() throws JAXBException {
        JAXBContext jaxbContext1 = JAXBContext.newInstance(Employments.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(Employments.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream v20Stream = ConvertV2ToV2_1Test.class.getClassLoader().getResourceAsStream("test-employments-2.0.xml");
        InputStream v21Stream = ConvertV2ToV2_1Test.class.getClassLoader().getResourceAsStream("test-employments-2.1.xml");

        Employments v20Employments = (Employments) jaxbUnmarshaller.unmarshal(v20Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        Employments v21Employments1 = (Employments) jaxbUnmarshaller.unmarshal(v21Stream);

        V2Convertible result = versionConverterV2_0ToV2_1.upgrade(new V2Convertible(v20Employments, "v2.1"));
        Employments v21Employments2 = (Employments) result.getObjectToConvert();

        assertEquals(v21Employments1, v21Employments2);
    }

    @Test
    public void upgradeFundingsToV21Test() throws JAXBException {
        JAXBContext jaxbContext1 = JAXBContext.newInstance(Fundings.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(Fundings.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream v20Stream = ConvertV2ToV2_1Test.class.getClassLoader().getResourceAsStream("test-fundings-2.0.xml");
        InputStream v21Stream = ConvertV2ToV2_1Test.class.getClassLoader().getResourceAsStream("test-fundings-2.1.xml");

        Fundings v20Fundings = (Fundings) jaxbUnmarshaller.unmarshal(v20Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        Fundings v21Fundings1 = (Fundings) jaxbUnmarshaller.unmarshal(v21Stream);

        V2Convertible result = versionConverterV2_0ToV2_1.upgrade(new V2Convertible(v20Fundings, "v2.1"));
        Fundings v21Fundings2 = (Fundings) result.getObjectToConvert();

        assertEquals(v21Fundings1, v21Fundings2);
    }

    @Test
    public void upgradePeerReviewsToV21Test() throws JAXBException {
        JAXBContext jaxbContext1 = JAXBContext.newInstance(PeerReviews.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(PeerReviews.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream v20Stream = ConvertV2ToV2_1Test.class.getClassLoader().getResourceAsStream("test-peer-reviews-2.0.xml");
        InputStream v21Stream = ConvertV2ToV2_1Test.class.getClassLoader().getResourceAsStream("test-peer-reviews-2.1.xml");

        PeerReviews v20PeerReviews = (PeerReviews) jaxbUnmarshaller.unmarshal(v20Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        PeerReviews v21PeerReviews1 = (PeerReviews) jaxbUnmarshaller.unmarshal(v21Stream);

        V2Convertible result = versionConverterV2_0ToV2_1.upgrade(new V2Convertible(v20PeerReviews, "v2.1"));
        PeerReviews v21PeerReviews2 = (PeerReviews) result.getObjectToConvert();

        assertEquals(v21PeerReviews1, v21PeerReviews2);
    }

    @Test
    public void upgradeWorksToV21Test() throws JAXBException {
        JAXBContext jaxbContext1 = JAXBContext.newInstance(Works.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(Works.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream v20Stream = ConvertV2ToV2_1Test.class.getClassLoader().getResourceAsStream("test-works-2.0.xml");
        InputStream v21Stream = ConvertV2ToV2_1Test.class.getClassLoader().getResourceAsStream("test-works-2.1.xml");

        Works v20Works = (Works) jaxbUnmarshaller.unmarshal(v20Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        Works v21Works1 = (Works) jaxbUnmarshaller.unmarshal(v21Stream);

        V2Convertible result = versionConverterV2_0ToV2_1.upgrade(new V2Convertible(v20Works, "v2.1"));
        Works v21Works2 = (Works) result.getObjectToConvert();

        assertEquals(v21Works1, v21Works2);
    }

    @Test
    public void upgradeGroupIdToV21Test() throws JAXBException {
        JAXBContext jaxbContext1 = JAXBContext.newInstance(GroupIdRecords.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(GroupIdRecords.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream v20Stream = ConvertV2ToV2_1Test.class.getClassLoader().getResourceAsStream("test-group-id-2.0.xml");
        InputStream v21Stream = ConvertV2ToV2_1Test.class.getClassLoader().getResourceAsStream("test-group-id-2.1.xml");

        GroupIdRecords v20Group = (GroupIdRecords) jaxbUnmarshaller.unmarshal(v20Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        GroupIdRecords v21GroupId1 = (GroupIdRecords) jaxbUnmarshaller.unmarshal(v21Stream);

        V2Convertible result = versionConverterV2_0ToV2_1.upgrade(new V2Convertible(v20Group, "v2.1"));
        GroupIdRecords v21GroupId2 = (GroupIdRecords) result.getObjectToConvert();

        assertEquals(v21GroupId1, v21GroupId2);
    }

    @Test
    public void upgradeAddressesToV21Test() throws JAXBException {
        JAXBContext jaxbContext1 = JAXBContext.newInstance(Addresses.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(Addresses.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream v20Stream = ConvertV2ToV2_1Test.class.getClassLoader().getResourceAsStream("test-addresses-2.0.xml");
        InputStream v21Stream = ConvertV2ToV2_1Test.class.getClassLoader().getResourceAsStream("test-addresses-2.1.xml");

        Addresses v20Element = (Addresses) jaxbUnmarshaller.unmarshal(v20Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        Addresses v21Element1 = (Addresses) jaxbUnmarshaller.unmarshal(v21Stream);

        V2Convertible result = versionConverterV2_0ToV2_1.upgrade(new V2Convertible(v20Element, "v2.1"));
        Addresses v21Element2 = (Addresses) result.getObjectToConvert();

        assertEquals(v21Element1, v21Element2);
    }
}
