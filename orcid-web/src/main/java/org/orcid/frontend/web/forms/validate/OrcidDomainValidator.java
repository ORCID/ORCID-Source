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
