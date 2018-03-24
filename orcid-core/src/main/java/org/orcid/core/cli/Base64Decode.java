package org.orcid.core.cli;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

/**
 * 
 * @author Will Simpson
 * 
 */
public class Base64Decode {

    public static void main(String[] args) {
        try {
            System.out.println(new String(Base64.decodeBase64(args[0]), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
