package org.orcid.core.cli;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 
 * @author Will Simpson
 * 
 */
public class UrlDecode {
    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println(URLDecoder.decode(args[0], "UTF-8"));
    }
}
