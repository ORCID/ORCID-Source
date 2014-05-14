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
package org.orcid.frontend.web.forms.validate;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.validator.routines.DomainValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidDomainValidator {

    private DomainValidator standardDomainValidator = DomainValidator.getInstance();

    private static final String DNS_TEST_DOMAIN = "google.com";

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidDomainValidator.class);

    public boolean isValid(String hostLocation) {
        if (isDnsOk()) {
            return isValidInDns(hostLocation);
        } else {
            LOGGER.warn("DNS is not OK, so validating in offline mode");
            return standardDomainValidator.isValid(hostLocation);
        }
    }

    private boolean isValidInDns(String hostLocation) {
        try {
            InetAddress.getByName(hostLocation);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    private boolean isDnsOk() {
        return isValidInDns(DNS_TEST_DOMAIN);
    }

    public static void main(String[] args) {
        String host = args[0];
        System.out.println("Host: " + host + ", Valid=" + new OrcidDomainValidator().isValid(host));
    }

}
