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
package org.orcid.record_2_0_rc2;

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
import org.orcid.jaxb.model.record_2_rc1.summary.ActivitiesSummary;
import org.orcid.jaxb.model.record_2_rc1.summary.Educations;
import org.orcid.jaxb.model.record_2_rc1.summary.Employments;
import org.orcid.jaxb.model.record_2_rc1.summary.Fundings;
import org.orcid.jaxb.model.record_2_rc1.summary.PeerReviews;
import org.orcid.jaxb.model.record_2_rc1.summary.Works;

public class ConvertVrc1ToVrc2Test extends BaseTest {

    @Resource
    private V2VersionConverter versionConverterV2_0_rc1ToV2_0rc2;

    @Test
    public void upgradeToVrc2Test() throws JAXBException {

        JAXBContext jaxbContext1 = JAXBContext.newInstance(ActivitiesSummary.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record_2_rc2.summary.ActivitiesSummary.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc1Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-activities-2.0_rc1.xml");
        InputStream rc2Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-activities-2.0_rc2.xml");

        ActivitiesSummary rc1Activities = (ActivitiesSummary) jaxbUnmarshaller.unmarshal(rc1Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.record_2_rc2.summary.ActivitiesSummary rc2Activities1 = 
        		(org.orcid.jaxb.model.record_2_rc2.summary.ActivitiesSummary) jaxbUnmarshaller
                .unmarshal(rc2Stream);

        org.orcid.jaxb.model.record_2_rc2.summary.ActivitiesSummary rc2Activities2 = 
        		new org.orcid.jaxb.model.record_2_rc2.summary.ActivitiesSummary();
        		versionConverterV2_0_rc1ToV2_0rc2.upgrade(rc2Activities2, new V2Convertible(rc1Activities, "v2_rc1"));

       assertEquals(rc2Activities1.toString(), rc2Activities2.toString());
    }
    
    @Test
    public void upgradeEducationsToVrc2Test() throws JAXBException {

        JAXBContext jaxbContext1 = JAXBContext.newInstance(Educations.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record_2_rc2.summary.Educations.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc1Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-educations-2.0_rc1.xml");
        InputStream rc2Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-educations-2.0_rc2.xml");

        Educations rc1Educations = (Educations) jaxbUnmarshaller.unmarshal(rc1Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.record_2_rc2.summary.Educations rc2Educations1 = 
        		(org.orcid.jaxb.model.record_2_rc2.summary.Educations) jaxbUnmarshaller
                .unmarshal(rc2Stream);

        org.orcid.jaxb.model.record_2_rc2.summary.Educations rc2Educations2 = 
        		new org.orcid.jaxb.model.record_2_rc2.summary.Educations();
        		versionConverterV2_0_rc1ToV2_0rc2.upgrade(rc2Educations2, new V2Convertible(rc1Educations, "v2_rc1"));

       assertEquals(rc2Educations1.getLastModifiedDate(), rc2Educations2.getLastModifiedDate());
    }
    
    @Test
    public void upgradeEmploymentsToVrc2Test() throws JAXBException {

        JAXBContext jaxbContext1 = JAXBContext.newInstance(Employments.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record_2_rc2.summary.Employments.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc1Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-employments-2.0_rc1.xml");
        InputStream rc2Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-employments-2.0_rc2.xml");

        Employments rc1Employments = (Employments) jaxbUnmarshaller.unmarshal(rc1Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.record_2_rc2.summary.Employments rc2Employments1 = 
        		(org.orcid.jaxb.model.record_2_rc2.summary.Employments) jaxbUnmarshaller
                .unmarshal(rc2Stream);

        org.orcid.jaxb.model.record_2_rc2.summary.Employments rc2Employments2 = 
        		new org.orcid.jaxb.model.record_2_rc2.summary.Employments();
        		versionConverterV2_0_rc1ToV2_0rc2.upgrade(rc2Employments2, new V2Convertible(rc1Employments, "v2_rc1"));

       assertEquals(rc2Employments1.getLastModifiedDate(), rc2Employments2.getLastModifiedDate());
    }
    
    @Test
    public void upgradeFundingsToVrc2Test() throws JAXBException {

        JAXBContext jaxbContext1 = JAXBContext.newInstance(Fundings.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record_2_rc2.summary.Fundings.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc1Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-fundings-2.0_rc1.xml");
        InputStream rc2Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-fundings-2.0_rc2.xml");

        Fundings rc1Fundings = (Fundings) jaxbUnmarshaller.unmarshal(rc1Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.record_2_rc2.summary.Fundings rc2Fundings1 = 
        		(org.orcid.jaxb.model.record_2_rc2.summary.Fundings) jaxbUnmarshaller
                .unmarshal(rc2Stream);

        org.orcid.jaxb.model.record_2_rc2.summary.Fundings rc2Fundings2 = 
        		new org.orcid.jaxb.model.record_2_rc2.summary.Fundings();
        		versionConverterV2_0_rc1ToV2_0rc2.upgrade(rc2Fundings2, new V2Convertible(rc1Fundings, "v2_rc1"));

       assertEquals(rc2Fundings1.getLastModifiedDate(), rc2Fundings2.getLastModifiedDate());
    }
    
    @Test
    public void upgradePeerReviewsToVrc2Test() throws JAXBException {

        JAXBContext jaxbContext1 = JAXBContext.newInstance(PeerReviews.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record_2_rc2.summary.PeerReviews.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc1Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-peer-reviews-2.0_rc1.xml");
        InputStream rc2Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-peer-reviews-2.0_rc2.xml");

        PeerReviews rc1PeerReviews = (PeerReviews) jaxbUnmarshaller.unmarshal(rc1Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.record_2_rc2.summary.PeerReviews rc2PeerReviews1 = 
        		(org.orcid.jaxb.model.record_2_rc2.summary.PeerReviews) jaxbUnmarshaller
                .unmarshal(rc2Stream);

        org.orcid.jaxb.model.record_2_rc2.summary.PeerReviews rc2PeerReviews2 = 
        		new org.orcid.jaxb.model.record_2_rc2.summary.PeerReviews();
        		versionConverterV2_0_rc1ToV2_0rc2.upgrade(rc2PeerReviews2, new V2Convertible(rc1PeerReviews, "v2_rc1"));

       assertEquals(rc2PeerReviews1.getLastModifiedDate(), rc2PeerReviews2.getLastModifiedDate());
    }
    
    @Test
    public void upgradeWorksToVrc2Test() throws JAXBException {

        JAXBContext jaxbContext1 = JAXBContext.newInstance(Works.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record_2_rc2.summary.Works.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc1Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-works-2.0_rc1.xml");
        InputStream rc2Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-works-2.0_rc2.xml");

        Works rc1Works = (Works) jaxbUnmarshaller.unmarshal(rc1Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.record_2_rc2.summary.Works rc2Works1 = 
        		(org.orcid.jaxb.model.record_2_rc2.summary.Works) jaxbUnmarshaller
                .unmarshal(rc2Stream);

        org.orcid.jaxb.model.record_2_rc2.summary.Works rc2Works2 = 
        		new org.orcid.jaxb.model.record_2_rc2.summary.Works();
        		versionConverterV2_0_rc1ToV2_0rc2.upgrade(rc2Works2, new V2Convertible(rc1Works, "v2_rc1"));

       assertEquals(rc2Works1.getLastModifiedDate(), rc2Works2.getLastModifiedDate());
    }
}
