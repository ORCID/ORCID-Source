package org.orcid.record_2_0_rc2;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.junit.Test;
import org.orcid.jaxb.model.record_2_rc1.summary.ActivitiesSummary;

public class ConvertVrc1ToVrc2Test {

	private final static MapperFacade mapper;
	
	static  
	   {  
	      final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();  
	      
	      mapperFactory.classMap(ActivitiesSummary.class, org.orcid.jaxb.model.record_2_rc2.summary.ActivitiesSummary.class)
			.field("educations", "educations")
			.field("employments", "employments")
			.field("fundings.fundingGroup{identifiers}", "fundings.fundingGroup{identifiers}")
			.field("fundings.fundingGroup{fundingSummary}", "fundings.fundingGroup{fundingSummary}")
			.field("peerReviews.peerReviewGroup{identifiers}", "peerReviews.peerReviewGroup{identifiers}")
			.field("peerReviews.peerReviewGroup{peerReviewSummary}", "peerReviews.peerReviewGroup{peerReviewSummary}")
			.field("works.workGroup{identifiers}", "works.workGroup{identifiers}")
			.field("works.workGroup{workSummary}", "works.workGroup{workSummary}")
			.byDefault()
			.register();
	      mapper = mapperFactory.getMapperFacade();  
	   }  
	
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
				(org.orcid.jaxb.model.record_2_rc2.summary.ActivitiesSummary) jaxbUnmarshaller.unmarshal(rc2Stream);
		
		org.orcid.jaxb.model.record_2_rc2.summary.ActivitiesSummary rc2Activities2 = new org.orcid.jaxb.model.record_2_rc2.summary.ActivitiesSummary();
		
		mapper.map(rc1Activities, rc2Activities2);
		//Set max last modified date values
		
		Marshaller jaxbMarshaller = jaxbContext2.createMarshaller();
		jaxbMarshaller.marshal(rc2Activities2, new File("D://abc.xml"));
		
		//Compare rc2Activities1(Converted with the mapper) and rc2Activities2(Given XML)
		assertTrue(rc2Activities2.equals(rc2Activities1));
	}
}
