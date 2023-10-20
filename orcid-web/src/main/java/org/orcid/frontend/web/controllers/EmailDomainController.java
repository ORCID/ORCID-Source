package org.orcid.frontend.web.controllers;

import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

import org.orcid.core.common.manager.EmailDomainManager;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.utils.OrcidStringUtils;
import org.orcid.persistence.jpa.entities.EmailDomainEntity;
import org.orcid.pojo.OrgDisambiguated;
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
    
    @Resource
    private OrgDisambiguatedManager orgDisambiguatedManager;
    
    @RequestMapping(value = "/find-category", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public @ResponseBody ObjectNode findCategory(@RequestParam("domain") String domain) {        
        ObjectMapper mapper = new ObjectMapper();
        if(domain == null || domain.isBlank() || domain.length() > 254) {
            ObjectNode response = mapper.createObjectNode();
            response.put("error", "Domain length too short, empty or invalid");
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
    
    @RequestMapping(value = "/find-org", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public @ResponseBody ObjectNode findOrgInfo(@RequestParam("domain") String domain) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        if(domain == null || domain.isBlank() || domain.length() > 254) {
            response.put("error", "Domain length too short, empty or invalid");
            return response;
        }
        domain = OrcidStringUtils.stripHtml(domain);
        
        EmailDomainEntity ede = emailDomainManager.findByEmailDoman(domain);  
        if(ede != null) {
            String emailDomain = ede.getEmailDomain();
            if(emailDomain != null && !emailDomain.isBlank()) {
                // Escape the : on the email domain to be able to search in solr
                emailDomain = emailDomain.replace(":", "\\:");
                String searchTerm = "org-disambiguated-id-from-source:" + emailDomain;
                List<OrgDisambiguated> orgsInfo = orgDisambiguatedManager.searchOrgsFromSolr(searchTerm, 0, 1, false);
                if(orgsInfo != null && !orgsInfo.isEmpty()) {
                    // Pick the first result 
                    OrgDisambiguated firstOrg = orgsInfo.get(0);
                    response.put("ROR", domain);
                    response.put("Org Name", firstOrg.getValue());
                    response.put("Country", firstOrg.getCountry());
                    response.put("City", firstOrg.getCity());            
                }   
            }
        }        
        return response;
    }
}
