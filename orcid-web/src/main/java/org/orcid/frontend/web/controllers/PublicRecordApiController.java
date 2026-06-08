package org.orcid.frontend.web.controllers;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.orcid.api.common.util.v3.PublicRecordUtils;
import org.orcid.api.publicV3.server.security.PublicAPISecurityManagerV3;
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.read_only.AddressManagerReadOnly;
import org.orcid.core.manager.v3.read_only.RecordManagerReadOnly;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.v3.release.common.Country;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.Addresses;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PublicRecordApiController {

    @Resource(name = "recordManagerReadOnlyV3")
    private RecordManagerReadOnly recordManagerReadOnly;

    @Resource(name = "orcidSecurityManagerV3")
    private OrcidSecurityManager orcidSecurityManager;

    @Resource(name = "publicAPISecurityManagerV3")
    private PublicAPISecurityManagerV3 publicAPISecurityManagerV3;

    private ObjectMapper mapper;

    private final boolean filterVersionOfIdentifiers = false;

    public PublicRecordApiController() {
        this.mapper = new ObjectMapper();
        mapper.registerModule(new JaxbAnnotationModule());
    }

    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/record", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE,
            OrcidApiConstants.ORCID_JSON, OrcidApiConstants.VND_ORCID_JSON })
    public @ResponseBody String viewRecord(HttpServletRequest request, @PathVariable("orcid") String orcid) throws JsonProcessingException {
        orcidSecurityManager.checkProfile(orcid);
        request.setAttribute(SourceEntityUtils.DO_NOT_POPULATE_SOURCES, true);
        Record publicRecord = recordManagerReadOnly.getPublicRecord(orcid, filterVersionOfIdentifiers);
        return mapper.writeValueAsString(publicRecord);
    }
}
