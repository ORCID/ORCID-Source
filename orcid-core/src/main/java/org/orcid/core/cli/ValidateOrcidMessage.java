/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.cli;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.orcid.jaxb.model.message.OrcidMessage;
import org.xml.sax.SAXException;

/**
 * 
 * @author Will Simpson
 * 
 */
public class ValidateOrcidMessage {

    public static void main(String[] args) {
        validAgainstSchema(new File(args[0]));
    }

    private static boolean validAgainstSchema(File fileToValidate) {
        Validator validator = createValidator();
        Source source = new StreamSource(fileToValidate);
        try {
            validator.validate(source);
            System.out.println(fileToValidate + " is valid");
            return true;
        } catch (SAXException e) {
            System.out.println(fileToValidate + " is invalid");
            System.out.println(e);
        } catch (IOException e) {
            System.out.println("Unable to read file " + fileToValidate);
        }
        return false;
    }

    private static Validator createValidator() {
        try {
            SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            Schema schema = factory.newSchema(ValidateOrcidMessage.class.getResource("/orcid-message-" + OrcidMessage.DEFAULT_VERSION + ".xsd"));
            return schema.newValidator();
        } catch (SAXException e) {
            throw new RuntimeException("Error reading ORCID client group schema", e);
        }
    }

}
