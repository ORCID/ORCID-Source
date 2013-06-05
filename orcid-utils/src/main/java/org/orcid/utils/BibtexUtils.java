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
package org.orcid.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.BibTeXFormatter;
import org.jbibtex.BibTeXParser;
import org.jbibtex.Key;
import org.jbibtex.ParseException;
import org.jbibtex.TokenMgrError;
import org.jbibtex.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 2011-2012 - Semantico Ltd.
 * 
 * @author Declan Newman (declan) Date: 05/10/2012
 */
public class BibtexUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(BibtexUtils.class);

    public static final String COMMA_AND_WHITESPACE = ", ";
    private static final Pattern BIBTEX_PATTERN = Pattern.compile(".*}\\s*", Pattern.DOTALL);

    private BibtexUtils() {
        // Don't allow instantiation
    }

    /**
     * Parse the BibTeX and return the {@link Map<Key, BibTeXEntry>}
     * representing the keys and values of the BibTeX
     * 
     * @param bibtex
     *            the BibTeX string
     * @return the {@link Map<Key, BibTeXEntry>}
     * @throws ParseException
     *             if the BibteX string is invalid
     */
    public static Map<Key, BibTeXEntry> getBibTeXEntries(String bibtex) throws ParseException {
        BibTeXDatabase bibTeXDatabase = getBibTeXDatabase(bibtex);
        return bibTeXDatabase.getEntries();
    }

    /**
     * Returns the BibTeX as a string. This is likely to be the same or similar
     * to the input
     * 
     * @param bibtex
     *            the string representing the BibTeX
     * @return the string representing the parsed BibTeX
     * @throws ParseException
     *             if the BibteX string is invalid
     */
    public static String formatBibTeX(String bibtex) throws ParseException {
        BibTeXDatabase bibTeXDatabase = getBibTeXDatabase(bibtex);
        BibTeXFormatter formatter = new BibTeXFormatter();
        StringWriter writer = new StringWriter();
        try {
            formatter.format(bibTeXDatabase, writer);
        } catch (IOException e) {
            LOGGER.warn("Problem with reading the string.", e);
            throw new IllegalStateException("Problem writing to a StringWriter!", e);
        }
        return writer.toString();
    }

    /**
     * Simplistic view of the BibTeX as a string conforming to the Harvard
     * referencing (Parenthetical referencing) style
     * 
     * @param bibtex
     *            the string representing the BibTeX
     * @return the citation as a String
     * @throws ParseException
     */
    // Having had a bit of a think about this, it would be fairly trivial to
    // create string templates for various citation styles and simply replace
    // variable placeholders
    // For example -
    public static String toCitation(String bibtex) throws ParseException {
        StringBuilder citation = new StringBuilder();
        Map<Key, BibTeXEntry> entries = getBibTeXEntries(bibtex);
        Set<Key> keys = entries.keySet();
        for (Key key : keys) {
            BibTeXEntry bibTeXEntry = entries.get(key);
            Value author = bibTeXEntry.getField(BibTeXEntry.KEY_AUTHOR);
            Value editor = bibTeXEntry.getField(BibTeXEntry.KEY_EDITOR);
            Value year = bibTeXEntry.getField(BibTeXEntry.KEY_YEAR);
            Value title = bibTeXEntry.getField(BibTeXEntry.KEY_TITLE);
            Value journal = bibTeXEntry.getField(BibTeXEntry.KEY_JOURNAL);
            Value volume = bibTeXEntry.getField(BibTeXEntry.KEY_VOLUME);
            Value number = bibTeXEntry.getField(BibTeXEntry.KEY_NUMBER);
            Value pages = bibTeXEntry.getField(BibTeXEntry.KEY_PAGES);

            if (author != null && StringUtils.isNotBlank(author.toUserString())) {
                citation.append(author.toUserString()).append(COMMA_AND_WHITESPACE);
            }
            if (editor != null && StringUtils.isNotBlank(editor.toUserString())) {
                citation.append(editor.toUserString()).append(COMMA_AND_WHITESPACE);
            }
            if (year != null && StringUtils.isNotBlank(year.toUserString())) {
                citation.append("(").append(year.toUserString()).append("). ");
            }
            if (title != null && StringUtils.isNotBlank(title.toUserString())) {
                citation.append('"').append(title.toUserString()).append('"').append(COMMA_AND_WHITESPACE);
            }
            if (journal != null && StringUtils.isNotBlank(journal.toUserString())) {
                citation.append(journal.toUserString()).append(COMMA_AND_WHITESPACE);
            }
            if (volume != null && StringUtils.isNotBlank(volume.toUserString())) {
                citation.append("vol. ").append(volume.toUserString()).append(COMMA_AND_WHITESPACE);
            }
            if (number != null && StringUtils.isNotBlank(number.toUserString())) {
                citation.append("no. ").append(number.toUserString()).append(COMMA_AND_WHITESPACE);
            }
            if (pages != null && StringUtils.isNotBlank(pages.toUserString())) {
                citation.append("pp. ").append(pages.toUserString()).append(COMMA_AND_WHITESPACE);
            }
        }

        String trimmed = citation.toString().trim();
        if (trimmed.endsWith(",")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

    /**
     * Utility method to obtain the underlying {@link BibTeXDatabase} object
     * 
     * @param bibtex
     *            the BinTeX as a string
     * @return the {@link BibTeXDatabase} object
     * @throws ParseException
     *             if the bibtex string is invalid
     */
    public static synchronized BibTeXDatabase getBibTeXDatabase(String bibtex) throws ParseException {
        StringReader reader = null;
        try {
            reader = new StringReader(bibtex);
            return new BibTeXParser().parse(reader);
        } catch (IOException e) {
            LOGGER.warn("Problem with reading the string.", e);
            throw new IllegalStateException("Problem reading from a StringReader!", e);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public static boolean isValid(String citation) {
        try {
            validate(citation);
        } catch (BibtexException e) {
            return false;
        }
        return true;
    }

    public static void validate(String citation) throws BibtexException {
        if (StringUtils.isBlank(citation)) {
            throw new BibtexException("BibTeX is blank.");
        }
        if (!BIBTEX_PATTERN.matcher(citation).matches()) {
            throw new BibtexException("BibTeX is not closed properly");
        }
        try {
            getBibTeXDatabase(citation);
        } catch (ParseException e) {
            throw new BibtexException(e);
        } catch (TokenMgrError e) {
            throw new BibtexException(e);
        } catch (RuntimeException e) {
            throw new BibtexException(e);
        }
    }

    public static void main(String[] args) throws ParseException {
        System.out.println("Valid: " + isValid(args[0]));
        System.out.println(toCitation(args[0]));
    }

}
