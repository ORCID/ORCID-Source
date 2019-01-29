package org.orcid.core.utils;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * Class to build up a solr query to perform simple field searches on a solr
 * instance. Not yet able to do facet searches, highlighting etc, only for
 * building a simple string with optional wildcards
 * 
 * @author jamesb
 * 
 */
public class SolrQueryBuilder {

    private StringBuilder queryString;
    private static final String AND_CONDITION = " AND ";
    private static final String OR_CONDITION = " OR ";
    private static final String NOT_CONDITION = " -";
    private static final String OPEN_BRACKET = "(";
    private static final String CLOSE_BRACKET = ")";
    
    // Need to escape + - && || ! ( ) { } [ ] ^ " ~ : \ and any white space
    private static final Pattern SPECIAL_CHARACTER_PATTERN = Pattern.compile("([+\\-&|!(){}\\[\\]^\"~:\\\\])");

    public SolrQueryBuilder(String firstQuery) {
        queryString = new StringBuilder(firstQuery);
    }

    public SolrQueryBuilder() {
        queryString = new StringBuilder();
    }

    public void appendFieldValuePair(String field, String value) {
        queryString.append(safelyFormatFieldValue(field, value));
    }

    /**
     * Method to add wildcard searching to a field by converting it lowercase
     * <b>This may change in line with the workings of the SOLR config as
     * different configurations are used so use with caution, knowing this
     * method may change</b> *
     * 
     * @param field
     * @param value
     */
    public void appendFieldValuePairAsLowercaseWildcard(String field, String value) {

        String basicQueryExpression = safelyFormatFieldValue(field, value);
        if (!StringUtils.isBlank(basicQueryExpression)) {
            queryString.append(basicQueryExpression.toLowerCase());
        }
    }

    public void appendLowercaseWildcardANDCondition(String field, String value) {

        String basicQueryExpression = safelyFormatFieldValue(field, value);
        if (!StringUtils.isBlank(basicQueryExpression)) {
            queryString.append(AND_CONDITION).append(basicQueryExpression.toLowerCase());
        }

    }

    public void appendANDCondition(String field, String value) {
        queryString.append(safelyFormatConditionFieldValue(AND_CONDITION, field, value));
    }

    public void appendORCondition(String field, String value) {
        queryString.append(safelyFormatConditionFieldValue(OR_CONDITION, field, value));
    }

    public void appendLowercaseWildcardORCondition(String field, String value) {
        String basicQueryExpression = safelyFormatFieldValue(field, value);
        if (!StringUtils.isBlank(basicQueryExpression)) {
            queryString.append(OR_CONDITION).append(basicQueryExpression.toLowerCase());
        }
    }

    public void appendNOTCondition(String field, Collection<String> values) {
        String basicQueryExpression = safelyFormatFieldValue(field, values);
        if (!StringUtils.isBlank(basicQueryExpression)) {
            queryString.append(NOT_CONDITION).append(basicQueryExpression.toLowerCase());
        }
    }

    public void appendValue(String value) {
        queryString.append(escapeValue(value));
    }    

    public void openNestedANDOperation() {
        queryString.append(AND_CONDITION).append(OPEN_BRACKET);
    }

    public void openNestedOROperation() {
        queryString.append(OR_CONDITION).append(OPEN_BRACKET);
    }

    public void closeNestedOROperation() {
        queryString.append(CLOSE_BRACKET);
    }

    public void closeNestedANDOperation() {
        queryString.append(CLOSE_BRACKET);
    }

    public String retrieveQuery() {
        return queryString.toString();
    }

    private String safelyFormatConditionFieldValue(String condition, String field, String value) {
        if (!StringUtils.isBlank(condition)) {
            String basicQueryExpression = safelyFormatFieldValue(field, value);
            if (!StringUtils.isBlank(basicQueryExpression)) {
                return condition + basicQueryExpression;
            }
        }
        return "";
    }

    private String safelyFormatFieldValue(String field, String value) {
        if (!StringUtils.isBlank(field) && !StringUtils.isBlank(value)) {
            return MessageFormat.format("{0}:{1}", new Object[] { field, escapeValue(value) });

        }
        return "";
    }

    private String safelyFormatFieldValue(String field, Collection<String> values) {
        if (values != null) {
            String joined = StringUtils.join(values, " ");
            if (!StringUtils.isBlank(joined)) {
                return MessageFormat.format("{0}:({1})", new Object[] { field, escapeValue(joined) });
            }
        }
        return "";
    }

    private Object escapeValue(String value) {
        if (value == null) {
            return null;
        }
        Matcher matcher = SPECIAL_CHARACTER_PATTERN.matcher(value);
        return matcher.replaceAll("\\\\$1");
    }

    public boolean isEmpty() {
        return StringUtils.trimToEmpty(queryString.toString()).length() == 0;
    }

}
