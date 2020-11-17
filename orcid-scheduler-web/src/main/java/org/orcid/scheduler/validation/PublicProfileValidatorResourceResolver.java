package org.orcid.scheduler.validation;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

public class PublicProfileValidatorResourceResolver implements LSResourceResolver {

    @SuppressWarnings("resource")
    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        PublicProfileValidatorLsInput lsInput = new PublicProfileValidatorLsInput(publicId, systemId, baseURI);
        lsInput.setByteStream(getClass().getResourceAsStream("/record_3.0/" + systemId));
        return lsInput;
    }

}
