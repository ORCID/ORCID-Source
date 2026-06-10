package org.orcid.frontend.web.controllers;

import jakarta.annotation.Resource;

import org.orcid.frontend.sms.SmsPocRequest;
import org.orcid.frontend.sms.SmsPocResponse;
import org.orcid.frontend.sms.SmsPocService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/sms-poc")
public class SmsPocController {

    @Resource
    private SmsPocService smsPocService;

    @RequestMapping(value = "/send.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody SmsPocResponse send(@RequestBody SmsPocRequest request) {
        return smsPocService.send(request);
    }
}
