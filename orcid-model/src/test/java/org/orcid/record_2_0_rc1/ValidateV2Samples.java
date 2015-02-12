/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.record_2_0_rc1;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.Test;
import org.orcid.jaxb.model.notification.custom.MarshallingTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class ValidateV2Samples {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateV2Samples.class);

    String[] sampleNames = { "activities", "deprecated", "education", "employment", "error", "funding", "history", "person", "preferences", "record", "search", "work" };

    @Test
    public void Test() throws SAXException, IOException {
        for (String name : sampleNames) {
            LOGGER.debug("validating sample: " + sampleNames);
            validateSampleXML(name);
        }
    }

    public void validateSampleXML(String name) throws SAXException, IOException {
        Source source = getInputStream("/record_2.0_rc1/samples/" + name + "-2.0_rc1.xml");
        Validator validator = getValidator(name);
        validator.validate(source);
    }

    private Source getInputStream(String loc) {
        InputStream inputStream = MarshallingTest.class.getResourceAsStream(loc);
        Source source = new StreamSource(inputStream);
        return source;
    }

    public Validator getValidator(String name) throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = factory.newSchema(getClass().getResource("/record_2.0_rc1/" + name + "-2.0_rc1.xsd"));
        Validator validator = schema.newValidator();
        return validator;
    }

}
