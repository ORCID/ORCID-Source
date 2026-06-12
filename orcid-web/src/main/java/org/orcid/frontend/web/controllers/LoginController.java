package org.orcid.frontend.web.controllers;

import java.io.UnsupportedEncodingException;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Name;
import org.orcid.pojo.ajaxForm.Names;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller("loginController")
public class LoginController extends BaseController {

    @Resource(name = "recordNameManagerV3")
    private RecordNameManagerReadOnly recordNameManager;

    @RequestMapping(value = "/account/names/{type}", method = RequestMethod.GET)
    public @ResponseBody Names getAccountNames(@PathVariable String type) {
        String currentOrcid = getCurrentUserOrcid();
        Name currentName = recordNameManager.getRecordName(currentOrcid);
        if (type.equals("public") && !currentName.getVisibility().equals(Visibility.PUBLIC)) {
            currentName = null;
        }
        String currentRealOrcid = getRealUserOrcid();
        Name realName = recordNameManager.getRecordName(currentRealOrcid);
        if (type.equals("public") && !realName.getVisibility().equals(Visibility.PUBLIC)) {
            realName = null;
        }
        return Names.valueOf(currentName, realName);
    }

    @RequestMapping(value = { "/signin", "/login" }, method = RequestMethod.GET)
    public ModelAndView loginGetHandler(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        String query = request.getQueryString();
        ModelAndView mav = new ModelAndView("login");
        if (!domainsAllowingRobots.contains(orcidUrlManager.getBaseDomainRmProtocall())) {
            mav.addObject("noIndex", true);
        }
        return mav;
    }

    // We should go back to regular spring sign out with CSRF protection
    @RequestMapping(value = { "/signout" }, method = RequestMethod.GET)
    public ModelAndView signout(HttpServletRequest request, HttpServletResponse response) {
        String query = request.getQueryString();
        
        String redirectString = "redirect:" + calculateRedirectUrl("/login");
        Boolean isOauth2ScreensRequest = (Boolean) request.getSession().getAttribute(OrcidOauth2Constants.OAUTH_2SCREENS);
        
        if(isOauth2ScreensRequest != null && isOauth2ScreensRequest) {
            // Just redirect to the authorization screen
            String queryString = (String) request.getSession().getAttribute(OrcidOauth2Constants.OAUTH_QUERY_STRING);
            redirectString += '?' + queryString; 
        }
        
        logoutCurrentUser(request, response);         
        return new ModelAndView(redirectString);
    }

    @RequestMapping("wrong-user")
    public String wrongUserHandler() {
        return "wrong_user";
    }

    @RequestMapping("/session-expired")
    public String sessionExpiredHandler() {
        return "session_expired";
    }

}
