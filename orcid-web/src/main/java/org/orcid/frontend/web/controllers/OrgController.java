package org.orcid.frontend.web.controllers;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.manager.v3.OrgManager;
import org.orcid.pojo.OrgDisambiguated;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = { "/orgs" })
public class OrgController {

    @Resource(name = "orgManagerV3")
    private OrgManager orgManager;

    @Resource
    private OrgDisambiguatedManager orgDisambiguatedManager;
    
    @RequestMapping(value = "ambiguous", method = RequestMethod.GET, produces = "text/csv")
    public void getAmbiguousOrgs(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.addHeader("Content-Disposition", "attachment; filename=\"ambiguous_orgs.csv\"");
        orgManager.writeAmbiguousOrgs(response.getWriter());
    }
    
    @RequestMapping(value = "disambiguated", method = RequestMethod.GET, produces = "text/csv")
    public void getDisambiguatedOrgs(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.addHeader("Content-Disposition", "attachment; filename=\"disambiguated_orgs.csv\"");
        orgManager.writeDisambiguatedOrgs(response.getWriter());
    }
    
    @RequestMapping(value = "/disambiguated/{idType}", method = RequestMethod.GET)
    public ResponseEntity<OrgDisambiguated> getDisambiguatedOrg( @PathVariable("idType") String idType, @RequestParam("value") String idValue){
        OrgDisambiguated org = orgDisambiguatedManager.findInDB(idValue,idType);
        if (org == null)
            return new ResponseEntity<OrgDisambiguated>(HttpStatus.NOT_FOUND);
        return  new ResponseEntity<OrgDisambiguated>(org, HttpStatus.OK);
    }

}
