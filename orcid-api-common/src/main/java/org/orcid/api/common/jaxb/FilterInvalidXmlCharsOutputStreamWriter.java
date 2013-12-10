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
package org.orcid.api.common.jaxb;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * 
 * @author Will Simpson
 *
 */
public class FilterInvalidXmlCharsOutputStreamWriter extends OutputStreamWriter {

    public FilterInvalidXmlCharsOutputStreamWriter(OutputStream out) {
        super(out);
    }

    @Override
    public void write(int c) throws IOException {
        // Don't write chars from ASCII controls that are not allowed in XML
        // 1.0!
        if (c > 31 || c == 9 || c == 10 || c == 13) {
            super.write(c);
        }
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        for (int i = off; i < len; i++) {
            write(cbuf[i]);
        }
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        if (str != null) {
            write(str.toCharArray(), off, len);
        }
    }

}
