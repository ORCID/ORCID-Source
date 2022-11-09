package org.orcid.core.cli.anonymize;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;

public class XMLDeserializer {

    private final String encoding;

    public XMLDeserializer() {
        this.encoding = "UTF-8";
    }

    /**
     * Deserializes a XML file to Object
     */
    public <T> T fromXml(File xml, Class<T> clazz) {
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setAdapter(new NormalizedStringAdapter());

            try (Reader reader = new InputStreamReader(new FileInputStream(xml), this.encoding);) {
                Object o = unmarshaller.unmarshal(reader);
                return clazz.cast(o);
            }

        } catch (Exception e) {
            throw new IllegalStateException("Error while deserializing a XML file to Object of type " + clazz, e);
        }
    }

}