package org.orcid.frontend.web.controllers;

import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import net.glxn.qrgen.QRCode;

@Controller
public class QrCodeController extends BaseController {

    @RequestMapping(value = { "/qr-code" }, method = RequestMethod.GET)
    public ModelAndView myOrcidQrCode() {
        ModelAndView mav = new ModelAndView("my_orcid_qr_code");
        return mav;
    }

    @RequestMapping(value = "/qr-code.png", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] generateQrCode() {
        UserDetails currentUser = getCurrentUser();
        if (currentUser == null) {
            return new byte[0];
        }
        String orcid = currentUser.getUsername();
        return QRCode.from(getBaseUri() + "/" + orcid).stream().toByteArray();
    }
    
    @RequestMapping(value = "/ORCID.png", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] qrCodeForDownload() {
        UserDetails currentUser = getCurrentUser();
        if (currentUser == null) {
            return new byte[0];
        }
        String orcid = currentUser.getUsername();
        return QRCode.from(getBaseUri() + "/" + orcid).withSize(500, 500).stream().toByteArray();
    }

}
