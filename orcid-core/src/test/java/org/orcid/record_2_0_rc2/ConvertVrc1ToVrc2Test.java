package org.orcid.record_2_0_rc2;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.core.version.V2VersionConverter;
import org.orcid.jaxb.model.record_2_rc1.summary.ActivitiesSummary;

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
        		versionConverterV2_0_rc1ToV2_0rc2.upgrade(rc2Activities2, rc1Activities);

        // Compare rc2Activities2(Converted with the mapper) and
        // rc2Activities1 (Given XML)
        assertEquals(rc2Activities1.toString(), rc2Activities2.toString());
    }
}
