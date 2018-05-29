package org.orcid.core;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.orcid.jaxb.model.message.OrcidMessage;

/**
 * orcid-persistence - Dec 7, 2011 - JaxbUnitTest
 * 
 * @author Declan Newman (declan)
 **/
public class JaxbOrcidMessageUtil {

    public static final String PACKAGE = "org.orcid.jaxb.entities";
    public static final String ORCID_PROTECTED_FULL_XML = "/orcid-protected-full-message-latest.xml";
    public static final String ORCID_PUBLIC_FULL_XML = "/orcid-public-full-message-latest.xml";

    public static OrcidMessage getPublicOrcidMessage() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(PACKAGE);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (OrcidMessage) unmarshaller.unmarshal(JaxbOrcidMessageUtil.class.getResourceAsStream(ORCID_PUBLIC_FULL_XML));
    }

    public static OrcidMessage getProtectedOrcidMessage() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(PACKAGE);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (OrcidMessage) unmarshaller.unmarshal(JaxbOrcidMessageUtil.class.getResourceAsStream(ORCID_PROTECTED_FULL_XML));
    }
}
