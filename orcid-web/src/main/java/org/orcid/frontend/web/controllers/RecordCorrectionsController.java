package org.orcid.frontend.web.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RecordCorrectionsController extends BaseController{

    @RequestMapping("/record-corrections")
    public ModelAndView recordCorrections() {
        ModelAndView mav = new ModelAndView("record-corrections");
        return mav;
    }

}
