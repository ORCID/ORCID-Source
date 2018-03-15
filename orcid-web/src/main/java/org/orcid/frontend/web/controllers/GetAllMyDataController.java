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
import org.orcid.core.manager.v3.read_only.PersonDetailsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.jaxb.model.v3.dev1.record.Affiliation;
import org.orcid.jaxb.model.v3.dev1.record.Distinction;
import org.orcid.jaxb.model.v3.dev1.record.Education;
import org.orcid.jaxb.model.v3.dev1.record.Employment;
import org.orcid.jaxb.model.v3.dev1.record.InvitedPosition;
import org.orcid.jaxb.model.v3.dev1.record.Membership;
import org.orcid.jaxb.model.v3.dev1.record.Person;
import org.orcid.jaxb.model.v3.dev1.record.Qualification;
import org.orcid.jaxb.model.v3.dev1.record.Service;
import org.orcid.jaxb.model.v3.dev1.record.Work;
import org.orcid.persistence.jpa.entities.WorkLastModifiedEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("getAllMyDataController")
@RequestMapping(value = { "/get-my-data" })
public class GetAllMyDataController extends BaseController {

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

    @Value("${org.orcid.download.activities.batch_size:50}")
    private Integer batchSize;

    @Resource(name = "personDetailsManagerV3")
    private PersonDetailsManagerReadOnly personDetailsManager;

    @Resource
    private WorkEntityCacheManager workEntityCacheManager;

    @Resource(name = "affiliationManagerReadOnlyV3")
    private AffiliationsManagerReadOnly affiliationManagerReadOnly;

    @Resource(name = "workManagerReadOnlyV3")
    private WorkManagerReadOnly workManagerReadOnly;

    @GetMapping(produces = MediaType.APPLICATION_OCTET_STREAM)
    public void getMyData(HttpServletResponse response) throws JAXBException, IOException {
        String currentUserOrcid = getCurrentUserOrcid();
        String fileName = currentUserOrcid + ".zip";
        Person person = personDetailsManager.getPersonDetails(currentUserOrcid);

        JAXBContext context = JAXBContext.newInstance(person.getClass());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(person, baos);

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(result);
        ZipEntry zipEntry = new ZipEntry("Person.xml");
        zip.putNextEntry(zipEntry);
        zip.write(baos.toByteArray());

        generateAffiliationsData(currentUserOrcid, zip);
        generateFundingData(currentUserOrcid, zip);
        generatePeerReviewData(currentUserOrcid, zip);
        generateWorksData(currentUserOrcid, zip);

        zip.close();

        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setHeader("Content-Type", "application/zip");
        response.getOutputStream().write(result.toByteArray());
        response.flushBuffer();
    }

    private void generateAffiliationsData(String orcid, ZipOutputStream zip) throws JAXBException, IOException {
        List<Affiliation> affiliations = affiliationManagerReadOnly.getAffiliations(orcid);
        JAXBContext context = JAXBContext.newInstance(Distinction.class, Education.class, Employment.class, InvitedPosition.class, Membership.class, Qualification.class,
                Service.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        for (Affiliation affiliation : affiliations) {
            ByteArrayOutputStream workBaos = new ByteArrayOutputStream();
            marshaller.marshal(affiliation, workBaos);
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
            ZipEntry zipEntry = new ZipEntry(elementName);
            zip.putNextEntry(zipEntry);
            zip.write(workBaos.toByteArray());
        }
    }

    private void generateFundingData(String currentUserOrcid, ZipOutputStream zip) throws JAXBException, IOException {
    }

    private void generatePeerReviewData(String currentUserOrcid, ZipOutputStream zip) throws JAXBException, IOException {
    }

    private void generateWorksData(String orcid, ZipOutputStream zip) throws JAXBException, IOException {
        List<WorkLastModifiedEntity> elements = workEntityCacheManager.retrieveWorkLastModifiedList(orcid, workManagerReadOnly.getLastModified(orcid));
        JAXBContext context = JAXBContext.newInstance(Work.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        for (List<WorkLastModifiedEntity> list : ListUtils.partition(elements, this.batchSize)) {
            List<Work> works = workManagerReadOnly.findWorks(orcid, list);
            for (Work w : works) {
                ByteArrayOutputStream workBaos = new ByteArrayOutputStream();
                marshaller.marshal(w, workBaos);
                ZipEntry zipEntry = new ZipEntry(WORKS_DIR_NAME + '/' + w.getPutCode() + ".xml");
                zip.putNextEntry(zipEntry);
                zip.write(workBaos.toByteArray());
            }
        }
    }

}
