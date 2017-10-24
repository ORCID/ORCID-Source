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
package org.orcid.api.member.server.validator;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.orcid.core.exception.ApplicationException;
import org.orcid.core.exception.InvalidJSONException;
import org.orcid.jaxb.model.v3.dev1.common.Day;
import org.orcid.jaxb.model.v3.dev1.common.FuzzyDate;
import org.orcid.jaxb.model.v3.dev1.common.Month;
import org.orcid.jaxb.model.v3.dev1.common.Year;
import org.orcid.jaxb.model.v3.dev1.record.Education;
import org.orcid.jaxb.model.v3.dev1.record.Employment;
import org.orcid.jaxb.model.v3.dev1.record.PeerReview;
import org.orcid.jaxb.model.v3.dev1.record.Work;
import org.xml.sax.SAXException;

public class JSONInputValidatorTest {

    private JSONInputValidator validator = new JSONInputValidator();

    @Test
    public void testValidateJSONInputForValidV3Education() throws JAXBException, SAXException, IOException {
        Education education = org.orcid.test.helper.v3.Utils.getEducation();
        validator.validateJSONInput(education, Education.class);
    }

    @Test(expected = ApplicationException.class)
    public void testValidateJSONValidInputForInvalidV3Education() throws JAXBException, SAXException, IOException {
        Education education = org.orcid.test.helper.v3.Utils.getEducation();
        education.setStartDate(null);
        validator.validateJSONInput(education, Education.class);
    }

    @Test
    public void testValidateJSONInputForValidV2Education() throws JAXBException, SAXException, IOException {
        org.orcid.jaxb.model.record_v2.Education education = org.orcid.test.helper.Utils.getEducation();
        validator.validateJSONInput(education, org.orcid.jaxb.model.record_v2.Education.class);
    }

    @Test(expected = ApplicationException.class)
    public void testValidateJSONValidInputForInvalidEducation() throws JAXBException, SAXException, IOException {
        org.orcid.jaxb.model.record_v2.Education education = org.orcid.test.helper.Utils.getEducation();
        education.setOrganization(null);
        validator.validateJSONInput(education, org.orcid.jaxb.model.record_v2.Education.class);
    }

    @Test
    public void testValidateJSONInputForValidV3Employment() throws JAXBException, SAXException, IOException {
        Employment employment = org.orcid.test.helper.v3.Utils.getEmployment();
        validator.validateJSONInput(employment, Employment.class);
    }

    @Test(expected = InvalidJSONException.class)
    public void testValidateJSONValidInputForInvalidV3Employment() throws JAXBException, SAXException, IOException {
        Employment employment = org.orcid.test.helper.v3.Utils.getEmployment();
        employment.setStartDate(null);
        validator.validateJSONInput(employment, Employment.class);
    }

    @Test
    public void testValidateJSONInputForValidV2Employment() throws JAXBException, SAXException, IOException {
        org.orcid.jaxb.model.record_v2.Employment employment = org.orcid.test.helper.Utils.getEmployment();
        validator.validateJSONInput(employment, org.orcid.jaxb.model.record_v2.Employment.class);
    }

    @Test(expected = InvalidJSONException.class)
    public void testValidateJSONValidInputForInvalidEmployment() throws JAXBException, SAXException, IOException {
        org.orcid.jaxb.model.record_v2.Employment employment = org.orcid.test.helper.Utils.getEmployment();
        employment.setOrganization(null);
        validator.validateJSONInput(employment, org.orcid.jaxb.model.record_v2.Employment.class);
    }

    @Test
    public void testValidateJSONInputForValidV3Work() throws JAXBException, SAXException, IOException {
        Work work = org.orcid.test.helper.v3.Utils.getWork("title");
        validator.validateJSONInput(work, Work.class);
    }

    @Test(expected = InvalidJSONException.class)
    public void testValidateJSONValidInputForInvalidV3Work() throws JAXBException, SAXException, IOException {
        Work work = org.orcid.test.helper.v3.Utils.getWork(null);
        validator.validateJSONInput(work, Work.class);
    }

    @Test
    public void testValidateJSONInputForValidV2Work() throws JAXBException, SAXException, IOException {
        org.orcid.jaxb.model.record_v2.Work work = org.orcid.test.helper.Utils.getWork("title");
        validator.validateJSONInput(work, org.orcid.jaxb.model.record_v2.Work.class);
    }

    @Test(expected = InvalidJSONException.class)
    public void testValidateJSONValidInputForInvalidWork() throws JAXBException, SAXException, IOException {
        org.orcid.jaxb.model.record_v2.Work work = org.orcid.test.helper.Utils.getWork(null);
        validator.validateJSONInput(work, org.orcid.jaxb.model.record_v2.Work.class);
    }

    @Test
    public void testValidateJSONInputForValidV3PeerReview() throws JAXBException, SAXException, IOException {
        PeerReview peerReview = org.orcid.test.helper.v3.Utils.getPeerReview();
        peerReview.setCompletionDate(new FuzzyDate(new Year(2017), new Month(1), new Day(1)));
        validator.validateJSONInput(peerReview, PeerReview.class);
    }

    @Test(expected = InvalidJSONException.class)
    public void testValidateJSONValidInputForInvalidV3PeerReview() throws JAXBException, SAXException, IOException {
        PeerReview peerReview = org.orcid.test.helper.v3.Utils.getPeerReview();
        peerReview.setCompletionDate(new FuzzyDate(new Year(2017), new Month(1), new Day(1)));
        peerReview.setRole(null);
        validator.validateJSONInput(peerReview, PeerReview.class);
    }

    @Test
    public void testValidateJSONInputForValidV2PeerReview() throws JAXBException, SAXException, IOException {
        org.orcid.jaxb.model.record_v2.PeerReview peerReview = org.orcid.test.helper.Utils.getPeerReview();
        peerReview.setCompletionDate(new org.orcid.jaxb.model.common_v2.FuzzyDate(new org.orcid.jaxb.model.common_v2.Year(2017),
                new org.orcid.jaxb.model.common_v2.Month(1), new org.orcid.jaxb.model.common_v2.Day(1)));
        validator.validateJSONInput(peerReview, org.orcid.jaxb.model.record_v2.PeerReview.class);
    }

    @Test(expected = InvalidJSONException.class)
    public void testValidateJSONValidInputForInvalidPeerReview() throws JAXBException, SAXException, IOException {
        org.orcid.jaxb.model.record_v2.PeerReview peerReview = org.orcid.test.helper.Utils.getPeerReview();
        peerReview.setCompletionDate(new org.orcid.jaxb.model.common_v2.FuzzyDate(new org.orcid.jaxb.model.common_v2.Year(2017),
                new org.orcid.jaxb.model.common_v2.Month(1), new org.orcid.jaxb.model.common_v2.Day(1)));
        peerReview.setRole(null);
        validator.validateJSONInput(peerReview, org.orcid.jaxb.model.record_v2.PeerReview.class);
    }

}
