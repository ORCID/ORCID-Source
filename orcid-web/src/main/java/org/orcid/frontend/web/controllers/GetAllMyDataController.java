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

import org.orcid.core.manager.WorkEntityCacheManager;
import org.orcid.core.manager.v3.read_only.PersonDetailsManagerReadOnly;
import org.orcid.jaxb.model.v3.dev1.record.Person;
import org.orcid.persistence.jpa.entities.WorkLastModifiedEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("getAllMyDataController")
@RequestMapping(value = { "/get-my-data" })
public class GetAllMyDataController extends BaseController {
    
    @Resource(name = "personDetailsManagerV3")
    private PersonDetailsManagerReadOnly personDetailsManager;
    
    @Resource
    private WorkEntityCacheManager workEntityCacheManager;
    
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
        ZipOutputStream zipOut = new ZipOutputStream(result);
        ZipEntry zipEntry = new ZipEntry("Person.xml");
        zipOut.putNextEntry(zipEntry);        
        zipOut.write(baos.toByteArray());        
        zipOut.close();        
        
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setHeader("Content-Type", "application/zip");
        response.getOutputStream().write(result.toByteArray());
        response.flushBuffer();             
    }
        
    
    private void generateWorksData(String orcid, Long profileLastModified) {
        List<WorkLastModifiedEntity> elements = workEntityCacheManager.retrieveWorkLastModifiedList(orcid, profileLastModified);
        for(WorkLastModifiedEntity entity : elements) {
            entity.getId()
        }
    }
    
}
