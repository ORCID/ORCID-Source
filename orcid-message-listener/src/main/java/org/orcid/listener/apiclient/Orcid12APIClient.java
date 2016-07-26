package org.orcid.listener.apiclient;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.orcid.jaxb.model.message.LastModifiedDate;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.springframework.stereotype.Component;

@Component
public class Orcid12APIClient {

    /** Fetches the profile from the ORCID public API
     * 
     * @param orcid
     * @return
     */
    public OrcidProfile fetchPublicProfile(String orcid){
        OrcidProfile p = new OrcidProfile();
        p.setOrcid(orcid);
        p.setOrcidHistory(new OrcidHistory());
        p.getOrcidHistory().setLastModifiedDate(new LastModifiedDate());
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(new Date("1/1/1999"));
        XMLGregorianCalendar date2;
        try {
            date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
        p.getOrcidHistory().getLastModifiedDate().setValue(date2);
        return p;
    }
}
