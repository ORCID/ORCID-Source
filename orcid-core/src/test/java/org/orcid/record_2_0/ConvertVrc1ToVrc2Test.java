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
import org.orcid.jaxb.model.groupid_rc1.GroupIdRecords;
import org.orcid.jaxb.model.record.summary_rc1.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc1.Educations;
import org.orcid.jaxb.model.record.summary_rc1.Employments;
import org.orcid.jaxb.model.record.summary_rc1.Fundings;
import org.orcid.jaxb.model.record.summary_rc1.PeerReviews;
import org.orcid.jaxb.model.record.summary_rc1.Works;

public class ConvertVrc1ToVrc2Test extends BaseTest {

    @Resource
    private V2VersionConverter versionConverterV2_0_rc1ToV2_0_rc2; 

    @Test
    public void upgradeToVrc2Test() throws JAXBException {

        JAXBContext jaxbContext1 = JAXBContext.newInstance(ActivitiesSummary.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record.summary_rc2.ActivitiesSummary.class);
        Unmarshaller jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
        Unmarshaller jaxbUnmarshaller2 = jaxbContext2.createUnmarshaller();

        InputStream rc1Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-activities-2.0_rc1.xml");
        InputStream rc2Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-activities-2.0_rc2.xml");

        ActivitiesSummary rc1Activities = (ActivitiesSummary) jaxbUnmarshaller1.unmarshal(rc1Stream);

        org.orcid.jaxb.model.record.summary_rc2.ActivitiesSummary rc2Activities1 = 
                (org.orcid.jaxb.model.record.summary_rc2.ActivitiesSummary) jaxbUnmarshaller2.unmarshal(rc2Stream);

        V2Convertible result = versionConverterV2_0_rc1ToV2_0_rc2.upgrade(new V2Convertible(rc1Activities, "v2_rc1"));
        org.orcid.jaxb.model.record.summary_rc2.ActivitiesSummary rc2Activities2 = (org.orcid.jaxb.model.record.summary_rc2.ActivitiesSummary) result
                .getObjectToConvert();
        //assertEquals(1,rc2Activities2.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        //assertEquals("common:external-id-value",rc2Activities2.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals(rc2Activities1.toString(), rc2Activities2.toString()); 
    }

    @Test
    public void upgradeEducationsToVrc2Test() throws JAXBException {

        JAXBContext jaxbContext1 = JAXBContext.newInstance(Educations.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record.summary_rc2.Educations.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc1Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-educations-2.0_rc1.xml");
        InputStream rc2Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-educations-2.0_rc2.xml");

        Educations rc1Educations = (Educations) jaxbUnmarshaller.unmarshal(rc1Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.record.summary_rc2.Educations rc2Educations1 = (org.orcid.jaxb.model.record.summary_rc2.Educations) jaxbUnmarshaller.unmarshal(rc2Stream);

        V2Convertible result = versionConverterV2_0_rc1ToV2_0_rc2.upgrade(new V2Convertible(rc1Educations, "v2_rc1"));
        org.orcid.jaxb.model.record.summary_rc2.Educations rc2Educations2 = (org.orcid.jaxb.model.record.summary_rc2.Educations) result.getObjectToConvert();

        assertEquals(rc2Educations1.getLastModifiedDate(), rc2Educations2.getLastModifiedDate());
    }

    @Test
    public void upgradeEmploymentsToVrc2Test() throws JAXBException {

        JAXBContext jaxbContext1 = JAXBContext.newInstance(Employments.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record.summary_rc2.Employments.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc1Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-employments-2.0_rc1.xml");
        InputStream rc2Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-employments-2.0_rc2.xml");

        Employments rc1Employments = (Employments) jaxbUnmarshaller.unmarshal(rc1Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.record.summary_rc2.Employments rc2Employments1 = (org.orcid.jaxb.model.record.summary_rc2.Employments) jaxbUnmarshaller.unmarshal(rc2Stream);

        V2Convertible result = versionConverterV2_0_rc1ToV2_0_rc2.upgrade(new V2Convertible(rc1Employments, "v2_rc1"));
        org.orcid.jaxb.model.record.summary_rc2.Employments rc2Employments2 = (org.orcid.jaxb.model.record.summary_rc2.Employments) result.getObjectToConvert();

        assertEquals(rc2Employments1.getLastModifiedDate(), rc2Employments2.getLastModifiedDate());
    }

    @Test
    public void upgradeFundingsToVrc2Test() throws JAXBException {

        JAXBContext jaxbContext1 = JAXBContext.newInstance(Fundings.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record.summary_rc2.Fundings.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc1Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-fundings-2.0_rc1.xml");
        InputStream rc2Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-fundings-2.0_rc2.xml");

        Fundings rc1Fundings = (Fundings) jaxbUnmarshaller.unmarshal(rc1Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.record.summary_rc2.Fundings rc2Fundings1 = (org.orcid.jaxb.model.record.summary_rc2.Fundings) jaxbUnmarshaller.unmarshal(rc2Stream);

        V2Convertible result = versionConverterV2_0_rc1ToV2_0_rc2.upgrade(new V2Convertible(rc1Fundings, "v2_rc1"));
        org.orcid.jaxb.model.record.summary_rc2.Fundings rc2Fundings2 = (org.orcid.jaxb.model.record.summary_rc2.Fundings) result.getObjectToConvert();

        assertEquals(rc2Fundings1.getLastModifiedDate(), rc2Fundings2.getLastModifiedDate());
    }

    @Test
    public void upgradePeerReviewsToVrc2Test() throws JAXBException {

        JAXBContext jaxbContext1 = JAXBContext.newInstance(PeerReviews.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record.summary_rc2.PeerReviews.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc1Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-peer-reviews-2.0_rc1.xml");
        InputStream rc2Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-peer-reviews-2.0_rc2.xml");

        PeerReviews rc1PeerReviews = (PeerReviews) jaxbUnmarshaller.unmarshal(rc1Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.record.summary_rc2.PeerReviews rc2PeerReviews1 = (org.orcid.jaxb.model.record.summary_rc2.PeerReviews) jaxbUnmarshaller.unmarshal(rc2Stream);

        V2Convertible result = versionConverterV2_0_rc1ToV2_0_rc2.upgrade(new V2Convertible(rc1PeerReviews, "v2_rc1"));
        org.orcid.jaxb.model.record.summary_rc2.PeerReviews rc2PeerReviews2 = (org.orcid.jaxb.model.record.summary_rc2.PeerReviews) result.getObjectToConvert();

        assertEquals(rc2PeerReviews1.getLastModifiedDate(), rc2PeerReviews2.getLastModifiedDate());
    }

    @Test
    public void upgradeWorksToVrc2Test() throws JAXBException {

        JAXBContext jaxbContext1 = JAXBContext.newInstance(Works.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record.summary_rc2.Works.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc1Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-works-2.0_rc1.xml");
        InputStream rc2Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-works-2.0_rc2.xml");

        Works rc1Works = (Works) jaxbUnmarshaller.unmarshal(rc1Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.record.summary_rc2.Works rc2Works1 = (org.orcid.jaxb.model.record.summary_rc2.Works) jaxbUnmarshaller.unmarshal(rc2Stream);

        V2Convertible result = versionConverterV2_0_rc1ToV2_0_rc2.upgrade(new V2Convertible(rc1Works, "v2_rc1"));
        org.orcid.jaxb.model.record.summary_rc2.Works rc2Works2 = (org.orcid.jaxb.model.record.summary_rc2.Works) result.getObjectToConvert();

        assertEquals(rc2Works1.getLastModifiedDate(), rc2Works2.getLastModifiedDate());
    }
    
    @Test
    public void upgradeGroupIdToVrc2Test() throws JAXBException {

        JAXBContext jaxbContext1 = JAXBContext.newInstance(GroupIdRecords.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.groupid_rc2.GroupIdRecords.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc1Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-group-id-2.0_rc1.xml");
        InputStream rc2Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-group-id-2.0_rc2.xml");

        GroupIdRecords rc1Works = (GroupIdRecords) jaxbUnmarshaller.unmarshal(rc1Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.groupid_rc2.GroupIdRecords rc2GroupId1 = (org.orcid.jaxb.model.groupid_rc2.GroupIdRecords) jaxbUnmarshaller.unmarshal(rc2Stream);

        V2Convertible result = versionConverterV2_0_rc1ToV2_0_rc2.upgrade(new V2Convertible(rc1Works, "v2_rc1"));
        org.orcid.jaxb.model.groupid_rc2.GroupIdRecords rc2GroupId2 = (org.orcid.jaxb.model.groupid_rc2.GroupIdRecords) result.getObjectToConvert();

        assertEquals(rc2GroupId1.getLastModifiedDate(), rc2GroupId2.getLastModifiedDate());
    }
}
