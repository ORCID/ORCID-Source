package org.orcid.frontend.web.controllers;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.manager.v3.OrgManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = { "/orgs" })
public class OrgController {

    @Resource(name = "orgManagerV3")
    private OrgManager orgManager;

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

}
