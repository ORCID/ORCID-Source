/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.utils;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.safety.Whitelist;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * User: Declan Newman (declan) Date: 10/02/2012
 * </p>
 */
public class OrcidStringUtils {

        public static String ORCID_STRING = "(\\d{4}-){3}\\d{3}[\\dX]";
        public static String ORCID_URI_STRING = "http://([^/]*orcid\\.org|localhost.*/orcid-web)/(\\d{4}-){3,}\\d{3}[\\dX]";
        
	private static String LT = "&lt;";
	private static String GT = "&gt;";
	private static String AMP = "&amp;";
	private static String APOS = "&apos;";
	private static String QUOT = "&quot;";

	private static String DECODED_LT = "<";
	private static String DECODED_GT = ">";
	private static String DECODED_AMP = "&";
	private static String DECODED_APOS = "'";
	private static String DECODED_QUOT = "\"";

	public static final Pattern orcidPattern = Pattern
			.compile(ORCID_STRING);
	private static final Pattern orcidUriPattern = Pattern
                .compile(ORCID_URI_STRING);
	private static final Pattern clientIdPattern = Pattern
			.compile("APP-[\\dA-Z]{16}");

	private static final Document.OutputSettings outputSettings = new Document.OutputSettings()
			.prettyPrint(false).charset("UTF-8").escapeMode(EscapeMode.xhtml);

	public static boolean isValidOrcid(String orcid) {
		if (StringUtils.isNotBlank(orcid)) {
			return orcidPattern.matcher(orcid).matches();
		} else {
			return false;
		}
	}
	
	public static boolean isValidOrcidUri(String orcidUri) {
	    if (StringUtils.isNotBlank(orcidUri)) {
                return orcidUriPattern.matcher(orcidUri).matches();
        } else {
                return false;
        }
	}

	public static String getOrcidNumber(String orcid) {
		Matcher matcher = orcidPattern.matcher(orcid);
		if (matcher.find()) {
			return matcher.group(0);
		}
		return null;
	}

	public static boolean isClientId(String clientId) {
		if (StringUtils.isNotBlank(clientId)) {
			return clientIdPattern.matcher(clientId).matches();
		} else {
			return false;
		}
	}

	public static Map<String, String> resourceBundleToMap(
			ResourceBundle resource) {
		Map<String, String> map = new HashMap<String, String>();

		Enumeration<String> keys = resource.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			map.put(key, resource.getString(key));
		}

		return map;
	}

	public static String stripHtml(String s) {
	    String output = Jsoup.clean(s, "", Whitelist.none(), outputSettings);
	    output = output.replace(GT, DECODED_GT);
            output = output.replace(AMP, DECODED_AMP);            
            return output;
	}
	
	public static String simpleHtml(String s) {
	    String output = Jsoup.clean(s, "", Whitelist.simpleText(), outputSettings);
	    // According to
            // http://jsoup.org/apidocs/org/jsoup/nodes/Entities.EscapeMode.html#xhtml
            // jsoup scape lt, gt, amp, apos, and quot for xhtml
            // So we want to restore them
            output = output.replace(LT, DECODED_LT);
            output = output.replace(GT, DECODED_GT);
            output = output.replace(AMP, DECODED_AMP);
            output = output.replace(APOS, DECODED_APOS);
            output = output.replace(QUOT, DECODED_QUOT);
            return output;
	}	
	
	/**
	 * Strips html and restore the following characters: ' " & > < If the string
	 * resulting after that process doesnt match the given string, we can say it
	 * contains html
	 * 
	 * @param s
	 *            String to be cleared
	 * @return true if the give string has html tags in it
	 * */
	public static boolean hasHtml(String s) {
		String striped = simpleHtml(s);
		return !striped.equals(s);
	}

	public static int compareStrings(String string, String otherString) {
		if (NullUtils.anyNull(string, otherString)) {
			return NullUtils.compareNulls(string, otherString);
		}
		return string.compareTo(otherString);
	}
}
