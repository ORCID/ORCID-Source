package org.orcid.frontend.web.controllers;

import javax.annotation.Resource;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.pojo.PIDPojo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = { "/identifiers" })
public class PIDController {

    @Resource
    private PIDNormalizationService normService;
    
    /** Fetch the normalized value and URL
     * 
     * Note, does not check for PID resolution.
     * 
     * @param idType
     * @param idValue
     * @return
     */
    @RequestMapping(value = "/norm/{type}", method = RequestMethod.GET)
    public ResponseEntity<PIDPojo> getNormalized(@PathVariable("type") String idType, @RequestParam("value") String idValue){
        PIDPojo p = new PIDPojo(idType,idValue,"","");
        p.setNormValue(normService.normalise(idType, idValue));
        if (!StringUtils.isEmpty(p.getNormValue())){
            p.setNormUrl(normService.generateNormalisedURL(idType, idValue));
        }                
        return new ResponseEntity<PIDPojo>(p, HttpStatus.OK);
    }
}
