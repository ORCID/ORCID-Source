package org.orcid.frontend.web.controllers;

import jakarta.annotation.Resource;

import org.orcid.frontend.sms.SmsPocRequest;
import org.orcid.frontend.sms.SmsPocResponse;
import org.orcid.frontend.sms.SmsPocService;
import org.orcid.frontend.sms.SmsVerificationCheckRequest;
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

    /** Starts a verification: generates a code and delivers it through the managed provider. */
    @RequestMapping(value = "/send.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody SmsPocResponse send(@RequestBody SmsPocRequest request) {
        return smsPocService.startVerification(request);
    }

    /** Confirms a code previously sent to a phone number. */
    @RequestMapping(value = "/verify.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody SmsPocResponse verify(@RequestBody SmsVerificationCheckRequest request) {
        return smsPocService.checkVerification(request);
    }
}
