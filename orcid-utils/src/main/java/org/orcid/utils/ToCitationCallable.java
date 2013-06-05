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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.Key;
import org.jbibtex.Value;

public class ToCitationCallable implements Callable<String> {
    String bibtex = null;

    public ToCitationCallable(String bibtex) {
        this.bibtex = bibtex;
    }

    @Override
    public String call() throws Exception {
        StringBuilder citation = new StringBuilder();
        Map<Key, BibTeXEntry> entries = BibtexUtils.getBibTeXEntries(bibtex);
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
                citation.append(author.toUserString()).append(BibtexUtils.COMMA_AND_WHITESPACE);
            }
            if (editor != null && StringUtils.isNotBlank(editor.toUserString())) {
                citation.append(editor.toUserString()).append(BibtexUtils.COMMA_AND_WHITESPACE);
            }
            if (year != null && StringUtils.isNotBlank(year.toUserString())) {
                citation.append("(").append(year.toUserString()).append("). ");
            }
            if (title != null && StringUtils.isNotBlank(title.toUserString())) {
                citation.append('"').append(title.toUserString()).append('"').append(BibtexUtils.COMMA_AND_WHITESPACE);
            }
            if (journal != null && StringUtils.isNotBlank(journal.toUserString())) {
                citation.append(journal.toUserString()).append(BibtexUtils.COMMA_AND_WHITESPACE);
            }
            if (volume != null && StringUtils.isNotBlank(volume.toUserString())) {
                citation.append("vol. ").append(volume.toUserString()).append(BibtexUtils.COMMA_AND_WHITESPACE);
            }
            if (number != null && StringUtils.isNotBlank(number.toUserString())) {
                citation.append("no. ").append(number.toUserString()).append(BibtexUtils.COMMA_AND_WHITESPACE);
            }
            if (pages != null && StringUtils.isNotBlank(pages.toUserString())) {
                citation.append("pp. ").append(pages.toUserString()).append(BibtexUtils.COMMA_AND_WHITESPACE);
            }
        }

        String trimmed = citation.toString().trim();
        if (trimmed.endsWith(",")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

}
