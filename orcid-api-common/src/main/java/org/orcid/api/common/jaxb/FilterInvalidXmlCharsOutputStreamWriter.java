package org.orcid.api.common.jaxb;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * 
 * @author Will Simpson
 *
 */
public class FilterInvalidXmlCharsOutputStreamWriter extends OutputStreamWriter {

    public FilterInvalidXmlCharsOutputStreamWriter(OutputStream out) {
        super(out);
    }

    public FilterInvalidXmlCharsOutputStreamWriter(OutputStream out, String charsetName) throws UnsupportedEncodingException {
        super(out, charsetName);
    }

    @Override
    public void write(int c) throws IOException {
        // Don't write chars from ASCII controls that are not allowed in XML
        // 1.0!
        if ((c != 65535 && c != 65534) && (c > 31 || c == 9 || c == 10 || c == 13)) {
            super.write(c);
        }
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        for (int i = off; i < len + off; i++) {
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
