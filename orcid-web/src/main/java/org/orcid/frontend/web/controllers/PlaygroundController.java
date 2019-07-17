package org.orcid.frontend.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author Will Simpson
 * 
 */
@Controller
@RequestMapping(value = "/oauth/playground")
public class PlaygroundController extends BaseController {

    @RequestMapping
    public ModelAndView playgroundHandler() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("playground");
        mav.addObject("noIndex", true);
        return mav;
    }

}
