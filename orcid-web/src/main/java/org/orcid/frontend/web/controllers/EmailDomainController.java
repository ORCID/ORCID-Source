package org.orcid.frontend.web.controllers;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

import org.orcid.core.common.manager.EmailDomainManager;
import org.orcid.core.utils.OrcidStringUtils;
import org.orcid.persistence.jpa.entities.EmailDomainEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller("emailDomainController")
@RequestMapping(value = { "/email-domain" })
public class EmailDomainController {
    
    @Resource
    private EmailDomainManager emailDomainManager;
    
    @RequestMapping(value = "/find-category", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public @ResponseBody ObjectNode findCategory(@RequestParam("domain") String domain) {        
        ObjectMapper mapper = new ObjectMapper();
        if(domain == null || domain.isBlank() || domain.length() > 254) {
            ObjectNode response = mapper.createObjectNode();
            response.put("error", "Domain lenght too long, empty or invalid");
            return response;
        }
        domain = OrcidStringUtils.stripHtml(domain);
        EmailDomainEntity ede = emailDomainManager.findByEmailDoman(domain);        
        if(ede == null) {
            ObjectNode response = mapper.createObjectNode();
            response.put("category", EmailDomainEntity.DomainCategory.UNDEFINED.name());
            return response;
        } else {
            ObjectNode response = mapper.createObjectNode();
            response.put("category", ede.getCategory().name());
            return response;
        }
    }
}
