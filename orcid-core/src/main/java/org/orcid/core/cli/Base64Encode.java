package org.orcid.core.cli;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

/**
 * 
 * @author Will Simpson
 * 
 */
public class Base64Encode {

    public static void main(String[] args) {
        try {
            System.out.println(Base64.encodeBase64URLSafeString(args[0].getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
