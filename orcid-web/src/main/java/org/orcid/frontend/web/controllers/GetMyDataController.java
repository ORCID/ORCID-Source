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
package org.orcid.frontend.web.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.collections4.ListUtils;
import org.orcid.core.manager.WorkEntityCacheManager;
import org.orcid.core.manager.v3.read_only.AffiliationsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.PeerReviewManagerReadOnly;
import org.orcid.core.manager.v3.read_only.PersonDetailsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileFundingManagerReadOnly;
import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.jaxb.model.v3.release.record.Affiliation;
import org.orcid.jaxb.model.v3.release.record.Distinction;
import org.orcid.jaxb.model.v3.release.record.Education;
import org.orcid.jaxb.model.v3.release.record.Employment;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.InvitedPosition;
import org.orcid.jaxb.model.v3.release.record.Membership;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.jaxb.model.v3.release.record.Person;
import org.orcid.jaxb.model.v3.release.record.Qualification;
import org.orcid.jaxb.model.v3.release.record.Service;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.persistence.jpa.entities.WorkLastModifiedEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller("getMyDataController")
@RequestMapping(value = { "/get-my-data" })
public class GetMyDataController extends BaseController {

    private static final String DISTINCTIONS_DIR_NAME = "affiliations/distinctions";
    private static final String EDUCATIONS_DIR_NAME = "affiliations/educations";
    private static final String EMPLOYMENTS_DIR_NAME = "affiliations/employments";
    private static final String INVITED_POSITIONS_DIR_NAME = "affiliations/invited_positions";
    private static final String MEMBERSHIPS_DIR_NAME = "affiliations/memberships";
    private static final String QUALIFICATIONS_DIR_NAME = "affiliations/qualifications";
    private static final String SERVICES_DIR_NAME = "affiliations/services";
    private static final String FUNDINGS_DIR_NAME = "fundings";
    private static final String PEER_REVIEWS_DIR_NAME = "peer_reviews";
    private static final String WORKS_DIR_NAME = "works";

    private final Marshaller marshaller;

    public GetMyDataController() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Person.class, Distinction.class, Education.class, Employment.class, InvitedPosition.class, Membership.class,
                Qualification.class, Service.class, Funding.class, PeerReview.class, Work.class);
        marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    }

    @Value("${org.orcid.download.activities.batch_size:50}")
    private Integer batchSize;

    @Resource(name = "personDetailsManagerV3")
    private PersonDetailsManagerReadOnly personDetailsManager;

    @Resource
    private WorkEntityCacheManager workEntityCacheManager;

    @Resource(name = "affiliationsManagerReadOnlyV3")
    private AffiliationsManagerReadOnly affiliationManagerReadOnly;

    @Resource(name = "profileFundingManagerReadOnlyV3")
    private ProfileFundingManagerReadOnly profileFundingManagerReadOnly;

    @Resource(name = "peerReviewManagerReadOnlyV3")
    private PeerReviewManagerReadOnly peerReviewManagerReadOnly;

    @Resource(name = "workManagerReadOnlyV3")
    private WorkManagerReadOnly workManagerReadOnly;           
    
    @RequestMapping(method = {RequestMethod.POST},  produces = MediaType.APPLICATION_OCTET_STREAM)
    public void getMyData(HttpServletResponse response) throws JAXBException, IOException {
        String currentUserOrcid = getCurrentUserOrcid();
        String fileName = currentUserOrcid + ".zip";
        
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(result);

        generatePersonData(currentUserOrcid, zip);
        generateAffiliationsData(currentUserOrcid, zip);
        generateFundingData(currentUserOrcid, zip);
        generatePeerReviewData(currentUserOrcid, zip);
        generateWorksData(currentUserOrcid, zip);
        zip.close();
                   
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setHeader("Content-Type", "application/zip");
        response.setHeader("filename", fileName);
        response.getOutputStream().write(result.toByteArray());
        response.flushBuffer();
    }

    private void generatePersonData(String orcid, ZipOutputStream zip) throws JAXBException, IOException {
        Person person = personDetailsManager.getPersonDetails(orcid);
        writeElement(toByteArray(person), "person.xml", zip);
    }

    private void generateAffiliationsData(String orcid, ZipOutputStream zip) throws JAXBException, IOException {
        List<Affiliation> affiliations = affiliationManagerReadOnly.getAffiliations(orcid);

        for (Affiliation affiliation : affiliations) {
            String elementName = null;
            if (affiliation instanceof Distinction) {
                elementName = DISTINCTIONS_DIR_NAME + '/' + affiliation.getPutCode() + ".xml";
            } else if (affiliation instanceof Education) {
                elementName = EDUCATIONS_DIR_NAME + '/' + affiliation.getPutCode() + ".xml";
            } else if (affiliation instanceof Employment) {
                elementName = EMPLOYMENTS_DIR_NAME + '/' + affiliation.getPutCode() + ".xml";
            } else if (affiliation instanceof InvitedPosition) {
                elementName = INVITED_POSITIONS_DIR_NAME + '/' + affiliation.getPutCode() + ".xml";
            } else if (affiliation instanceof Membership) {
                elementName = MEMBERSHIPS_DIR_NAME + '/' + affiliation.getPutCode() + ".xml";
            } else if (affiliation instanceof Qualification) {
                elementName = QUALIFICATIONS_DIR_NAME + '/' + affiliation.getPutCode() + ".xml";
            } else if (affiliation instanceof Service) {
                elementName = SERVICES_DIR_NAME + '/' + affiliation.getPutCode() + ".xml";
            } else {
                throw new IllegalArgumentException("Invalid affiliation type: " + affiliation.getClass().getName());
            }
            writeElement(toByteArray(affiliation), elementName, zip);
        }
    }

    private void generateFundingData(String orcid, ZipOutputStream zip) throws JAXBException, IOException {
        List<Funding> fundings = profileFundingManagerReadOnly.getFundingList(orcid);
        for (Funding funding : fundings) {
            writeElement(toByteArray(funding), (FUNDINGS_DIR_NAME + '/' + funding.getPutCode() + ".xml"), zip);
        }
    }

    private void generatePeerReviewData(String orcid, ZipOutputStream zip) throws JAXBException, IOException {
        List<PeerReview> peerReviews = peerReviewManagerReadOnly.findPeerReviews(orcid);
        for (PeerReview peerReview : peerReviews) {
            writeElement(toByteArray(peerReview), (PEER_REVIEWS_DIR_NAME + '/' + peerReview.getPutCode() + ".xml"), zip);
        }
    }

    private void generateWorksData(String orcid, ZipOutputStream zip) throws JAXBException, IOException {
        List<WorkLastModifiedEntity> elements = workEntityCacheManager.retrieveWorkLastModifiedList(orcid, workManagerReadOnly.getLastModified(orcid));
        for (List<WorkLastModifiedEntity> list : ListUtils.partition(elements, this.batchSize)) {
            List<Work> works = workManagerReadOnly.findWorks(orcid, list);
            for (Work work : works) {
                writeElement(toByteArray(work), (WORKS_DIR_NAME + '/' + work.getPutCode() + ".xml"), zip);
            }
        }
    }

    private byte[] toByteArray(Object o) throws JAXBException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshaller.marshal(o, baos);
        return baos.toByteArray();
    }

    private void writeElement(byte[] data, String name, ZipOutputStream zip) throws IOException {
        ZipEntry zipEntry = new ZipEntry(name);
        zip.putNextEntry(zipEntry);
        zip.write(data);
    }

}
