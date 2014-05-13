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

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidDomainValidator {

    public boolean isValid(String hostLocation) {
        try {
            InetAddress.getByName(hostLocation);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        String host = args[0];
        System.out.println("Host: " + host + ", Valid=" + new OrcidDomainValidator().isValid(host));
    }

}
