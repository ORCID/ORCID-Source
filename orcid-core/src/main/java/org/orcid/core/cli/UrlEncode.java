package org.orcid.core.cli;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 
 * @author Will Simpson
 * 
 */
public class UrlEncode {
    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println(URLEncoder.encode(args[0], "UTF-8"));
    }
}
