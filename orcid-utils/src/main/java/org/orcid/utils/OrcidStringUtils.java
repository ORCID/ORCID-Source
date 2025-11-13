package org.orcid.utils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.safety.Safelist;

public class OrcidStringUtils {

    public static String ORCID_STRING = "(\\d{4}-){3}\\d{3}[\\dX]";
    public static String ORCID_URI_STRING = "http://([^/]*orcid\\.org|localhost.*/orcid-web)/(\\d{4}-){3,}\\d{3}[\\dX]";
    public static String ORCID_URI_2_1_STRING = "https://([^/]*orcid\\.org|localhost.*/orcid-web)/(\\d{4}-){3,}\\d{3}[\\dX]";
    public static String EMAIL_REGEXP = "^([^@\\s]|(\".+\"))+@([^@\\s\\.\"'\\(\\)\\[\\]\\{\\}\\\\/,:;]+\\.)+([^@\\s\\.\"'\\(\\)\\[\\]\\{\\}\\\\/,:;]{2,})+$";
    public static String URL_REGEXP = "^((https?):\\/\\/)[\\w.-]+(?:\\.[\\w\\.-]+)+[\\w\\-\\._~:/?#%\\[\\]@!\\$&'\\(\\)\\*\\+\\\\,;=.><\\ ]+$";
    public static String CONTAINS_DOMAIN_REGEX= "\\b([a-zA-Z0-9\\-]+\\.)+[a-zA-Z]{2,}\\b";
    /**
     * Regex to find an IPv4 address. It checks for four blocks of numbers from 0-255, separated by dots.
     */
    public static String IPV4_ADDRESS_REGEX = "\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b";
    // A comprehensive regex for validating a string that is a complete IPv6 address.
    private static String IPV6_VALIDATION_STRING = "(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|:([0-9a-fA-F]{1,4}:){1,7}|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:(:(:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))";
    // A simpler pattern to find potential IPv6 candidates in a larger string.
    private static String IPV6_CANDIDATE_REGEX = "[0-9a-fA-F:%\\.]+";

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

    public static final Pattern orcidPattern = Pattern.compile(ORCID_STRING);
    private static final Pattern orcidUriPattern = Pattern.compile(ORCID_URI_STRING);
    private static final Pattern orcidUri2_1Pattern = Pattern.compile(ORCID_URI_2_1_STRING);
    private static final Pattern clientIdPattern = Pattern.compile("APP-[\\dA-Z]{16}");
    private static final Pattern emailPattern = Pattern.compile(EMAIL_REGEXP);
    private static final Pattern urlPattern = Pattern.compile(URL_REGEXP, Pattern.CASE_INSENSITIVE);
    private static final Pattern containsDomainPattern = Pattern.compile(CONTAINS_DOMAIN_REGEX, Pattern.CASE_INSENSITIVE);
    private static final Pattern ipv4AddressPattern = Pattern.compile(IPV4_ADDRESS_REGEX);
    private static final Pattern ipv6ValidationPattern = Pattern.compile(IPV6_VALIDATION_STRING, Pattern.CASE_INSENSITIVE);
    private static final Pattern ipv6CandidatePattern = Pattern.compile(IPV6_CANDIDATE_REGEX, Pattern.CASE_INSENSITIVE);

    private static final Pattern invalidXMLCharactersPattern = Pattern.compile("(\u0000|\uFFFE|\uFFFF)");

    private static final Document.OutputSettings outputSettings = new Document.OutputSettings().prettyPrint(false).charset("UTF-8").escapeMode(EscapeMode.xhtml);

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

    public static boolean isValidOrcid2_1Uri(String orcidUri) {
        if (StringUtils.isNotBlank(orcidUri)) {
            return orcidUri2_1Pattern.matcher(orcidUri).matches();
        } else {
            return false;
        }
    }

    public static boolean isValidURL(String url) {
        if (StringUtils.isNotBlank(url) && url.length() <= 2000) {
            return urlPattern.matcher(url).matches();
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

    public static Map<String, String> resourceBundleToMap(ResourceBundle resource) {
        Map<String, String> map = new HashMap<String, String>();

        Enumeration<String> keys = resource.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            map.put(key, resource.getString(key));
        }

        return map;
    }

    public static String stripHtml(String s) {
        String output = Jsoup.clean(s, "", Safelist.none(), outputSettings);
        output = output.replace(GT, DECODED_GT);
        output = output.replace(AMP, DECODED_AMP);
        return output;
    }

    public static String simpleHtml(String s) {
        String output = Jsoup.clean(s, "", Safelist.simpleText(), outputSettings);
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
     */
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

    public static String filterInvalidXMLCharacters(String string) {
        if (string == null || string.isEmpty()) {
            return string;
        }

        return invalidXMLCharactersPattern.matcher(string).replaceAll("");
    }

    public static String filterEmailAddress(String email) {
        if (StringUtils.isBlank(email)) {
            throw new RuntimeException("Unable to filter empty email address");
        }

        return email.trim().replaceAll("((\\h)|(\\s))", "");
    }

    public static boolean isEmailValid(String email) {
        if (StringUtils.isBlank(email)) {
            throw new RuntimeException("Unable to valid email address");
        }

        return emailPattern.matcher(email).matches();
    }
    
    
    public static boolean containsDomain(String string) {
        if (string == null || string.trim().isEmpty()) {
            return false;
        }

        return containsDomainPattern.matcher(string).find();
    }
    
    public static boolean containsIPv4Address(String string) {
        if (string == null || string.trim().isEmpty()) {
            return false;
        }

        return ipv4AddressPattern.matcher(string).find();
    }
    
    /**
     * Checks if the given string contains a valid IPv6 address.
     * This method first finds potential candidates and then uses a strict
     * regex to validate them completely. This avoids false positives from
     * partial matches on invalid addresses.
     *
     * @param string The string to check.
     * @return true if the string contains a valid IPv6 address, false otherwise.
     */
    public static boolean containsIPv6Address(String string) {
        if (StringUtils.isBlank(string)) {
            return false;
        }
        // Use the simpler regex to find potential matches (candidates).
        Matcher candidateMatcher = ipv6CandidatePattern.matcher(string);
        while (candidateMatcher.find()) {
            String candidate = candidateMatcher.group();
            // Use the comprehensive regex with .matches() to validate the ENTIRE candidate.
            if (ipv6ValidationPattern.matcher(candidate).matches()) {
                // We found a valid IPv6 address within the string.
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if a string is a valid "friendly name" for an email, meaning it does
     * not contain any domains or IP addresses, which could be used for phishing.
     *
     * @param name The name to validate.
     * @return true only if the name does not contain a domain, IPv4, or IPv6 address.
     */
    public static boolean isValidEmailFriendlyName(String name) {
        if (StringUtils.isBlank(name)) {
            return true;
        }
        // The name is valid if it does NOT contain a domain AND does NOT contain an IPv4 AND does NOT contain an IPv6.
        return !containsDomain(name) && !containsIPv4Address(name) && !containsIPv6Address(name);
    }
    
}
