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

import org.apache.commons.validator.routines.DomainValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.TextParseException;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidDomainValidator {

    private DomainValidator standardDomainValidator = DomainValidator.getInstance();

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidDomainValidator.class);

    public boolean isValid(String hostLocation) {
        try {
            Lookup lookup = new Lookup(hostLocation);
            lookup.run();
            int result = lookup.getResult();
            if (Lookup.SUCCESSFUL == result) {
                return true;
            }
            if (Lookup.HOST_NOT_FOUND == result || Lookup.TYPE_NOT_FOUND == result) {
                return false;
            }
            LOGGER.warn("DNS is not OK, so validating in offline mode");
            return standardDomainValidator.isValid(hostLocation);
        } catch (TextParseException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        String host = args[0];
        System.out.println("Host: " + host + ", Valid=" + new OrcidDomainValidator().isValid(host));
    }

}
