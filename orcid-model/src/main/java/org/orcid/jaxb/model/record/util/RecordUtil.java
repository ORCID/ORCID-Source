package org.orcid.jaxb.model.record.util;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class RecordUtil {

    /**
     * @param args
     */
    public static String convertToString(Object obj) {
        try {
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            StringWriter writer = new StringWriter();
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(obj, writer);
            return writer.toString();
        } catch (JAXBException e) {
            return ("Unable to unmarshal because: " + e);
        }
    }

}
