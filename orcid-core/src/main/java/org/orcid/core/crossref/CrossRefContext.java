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
package org.orcid.core.crossref;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for parsing COinS
 */
public class CrossRefContext implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(CrossRefContext.class);

    private static final long serialVersionUID = 1L;

    /** String Constant for Context Version. */
    private static final String CTX_VER = "ctx_ver";
    /** String Constant for Value Format. */
    private static final String VAL_FMT = "rft_val_fmt";
    /** String Constant for Record ID. */
    private static final String ID = "rtf_id";
    /** String Constant for Genre type. */
    private static final String GENRE = "rtf.genre";
    /** String Constant for Start Page. */
    private static final String S_PAGE = "rtf.spage";
    /** String Constant for End Page. */
    private static final String E_PAGE = "rtf.epage";
    /** String Constant for Date. */
    private static final String DATE = "rtf.date";
    /** String Constant for Article Title. */
    private static final String A_TITLE = "rtf.atitle";
    /** String Constant for Journal Title. */
    private static final String J_TITLE = "rtf.jtitle";
    /** String Constant for Journal short Title. */
    private static final String S_TITLE = "rtf.stitle";
    /** String Constant for Volume. */
    private static final String VOLUME = "rtf.volume";
    /** String Constant for author last name. */
    private static final String AU_LAST = "rtf.aulast";
    /** String Constant for author first name. */
    private static final String AU_FIRST = "rtf.aufirst";
    /** String Constant for author initial name. */
    private static final String AU_INIT = "rtf.auinit";
    /** String Constant for author suffix name. */
    private static final String AU_SUFFIX = "rtf.ausuffix";
    /** String Constant for author full name. */
    private static final String AU_FULL = "rtf.au";
    /** String Constant for Issue. */
    private static final String ISSUE = "rtf.issue";
    /** String Constant for article isbn. */
    private static final String ISBN = "rft.isbn";
    /** String Constant for article corp authors. */
    private static final String AU_CORP = "rft.aucorp";
    /** String Constant for article number. */
    private static final String ART_NUM = "rft.artnum";

    /** String Constant for article type. */
    private static final String TYPE_ARTICLE = "article";
    /** String Constant for article type journal. */
    private static final String TYPE_JOURNAL = "journal-article";
    /** String Constant for article type proceeding. */
    private static final String TYPE_PROCEEDING = "proceeding";
    /** String Constant for undefined article. */
    private static final String TYPE_PREPRINT = "preprint";
    /** String Constant for undefined article. */
    private static final String TYPE_CONFERENCE = "conference";
    /** String Constant for undefined article. */
    private static final String TYPE_UNKNOWN = "unknown";

    /** list of types. */
    private static Map<String, String> refTypes = new HashMap<String, String>();

    /** boolean flag for parse status. */
    private boolean isParsed = false;

    static {
        refTypes.put(TYPE_JOURNAL, CrossRefReferenceTypes.JOURNAL_ARTICLE);
        refTypes.put(TYPE_PROCEEDING, CrossRefReferenceTypes.CONFERENCE_PROCEEDING);
        refTypes.put(TYPE_CONFERENCE, CrossRefReferenceTypes.CONFERENCE_PAPER);
        refTypes.put(TYPE_ARTICLE, CrossRefReferenceTypes.GENERIC);
        refTypes.put(TYPE_PREPRINT, CrossRefReferenceTypes.GENERIC);
        refTypes.put(TYPE_UNKNOWN, CrossRefReferenceTypes.GENERIC);
    }

    /** Variable for article type. */
    private String articleType = TYPE_UNKNOWN;

    /** Variable for coin text. */
    private String coinText;

    /** Map with artifact fields. */
    private Map<String, String> artifactFields = new HashMap<String, String>();

    /**
     * Default Constructor.
     */
    public CrossRefContext() {

    }

    /**
     * Parameterized Constructor.
     * 
     * @param coinTxt
     *            coin text
     */
    public CrossRefContext(final String coinTxt) {
        this.coinText = coinTxt;
    }

    /**
     * parses coin text.
     */
    public void parse() {
        if (null != coinText) {
            final String[] properties = StringUtils.split(StringEscapeUtils.unescapeHtml(coinText), "&");
            String lastIteratedKey = null;
            for (final String property : properties) {
                String[] fields = StringUtils.split(property, "=");
                if (fields.length == 1) {
                    LOG.error("Error occured while parsing COIN text");
                    String lastIteratedValue = artifactFields.get(lastIteratedKey);
                    if ("null".equals(lastIteratedValue)) {
                        lastIteratedValue = "";
                    }
                    // temp fix to recover from the error.
                    artifactFields.put(lastIteratedKey, lastIteratedValue + "&" + fields[0]);
                } else {
                    if ("null".equals(fields[1])) {
                        fields[1] = "";
                    }
                    artifactFields.put(fields[0], fields[1]);
                }
                lastIteratedKey = fields[0];
            }
            articleType = refTypes.get(artifactFields.get(GENRE));
        }
        isParsed = true;
    }

    /**
     * returns article type.
     * 
     * @return String
     */
    public String getArticleType() {
        return articleType;
    }

    /**
     * @return String coinText
     */
    public String getCoinText() {
        return this.coinText;
    }

    /**
     * @param newCoinText
     *            the coinText to set
     */
    public void setCoinText(final String newCoinText) {
        this.coinText = newCoinText;
    }

    /**
     * Gets Context Version.
     * 
     * @return String
     */
    public String getCtxVersion() {
        return artifactFields.get(CTX_VER);
    }

    /**
     * Gets format.
     * 
     * @return String
     */
    public String getFormat() {
        return artifactFields.get(VAL_FMT);
    }

    /**
     * Gets record id.
     * 
     * @return String
     */
    public String getRecordId() {
        return artifactFields.get(ID);
    }

    /**
     * Gets genre.
     * 
     * @return String
     */
    public String getGenre() {
        return artifactFields.get(GENRE);
    }

    /**
     * Gets start page.
     * 
     * @return String
     */
    public String getSPage() {
        return artifactFields.get(S_PAGE);
    }

    /**
     * Gets end page.
     * 
     * @return String
     */
    public String getEPage() {
        return artifactFields.get(E_PAGE);
    }

    /**
     * Gets date.
     * 
     * @return String
     */
    public String getDate() {
        return artifactFields.get(DATE);
    }

    /**
     * Gets article title.
     * 
     * @return String
     */
    public String getATitle() {
        return artifactFields.get(A_TITLE);
    }

    /**
     * Gets J Title.
     * 
     * @return String
     */
    public String getJTitle() {
        if (StringUtils.isNotBlank(artifactFields.get(J_TITLE))) {
            return artifactFields.get(J_TITLE);
        } else {
            return artifactFields.get(S_TITLE);
        }
    }

    /**
     * Gets volume.
     * 
     * @return String
     */
    public String getVolume() {
        return artifactFields.get(VOLUME);
    }

    /**
     * Gets Issue.
     * 
     * @return String
     */
    public String getIssue() {
        return artifactFields.get(ISSUE);
    }

    /**
     * Gets ISBN.
     * 
     * @return String
     */
    public String getIsbn() {
        return artifactFields.get(ISBN);
    }

    /**
     * Gets corp authors.
     * 
     * @return String
     */
    public String getCorpAuthor() {
        return artifactFields.get(AU_CORP);
    }

    /**
     * gets full author.
     * 
     * @return String author full name
     */
    public String getAuthor() {
        if (StringUtils.isNotEmpty(artifactFields.get(AU_FULL))) {
            return artifactFields.get(AU_FULL);
        }
        final String spaceChar = " ";
        StringBuilder author = new StringBuilder();
        if (StringUtils.isNotEmpty(artifactFields.get(AU_LAST))) {
            author.append(artifactFields.get(AU_LAST));
        }
        if (StringUtils.isNotEmpty(artifactFields.get(AU_FIRST))) {
            author.append(spaceChar);
            author.append(artifactFields.get(AU_FIRST));
        } else if (StringUtils.isNotEmpty(artifactFields.get(AU_INIT))) {
            author.append(spaceChar);
            author.append(artifactFields.get(AU_INIT));
        }
        if (StringUtils.isNotEmpty(artifactFields.get(AU_SUFFIX))) {
            author.append(spaceChar);
            author.append(artifactFields.get(AU_SUFFIX));
        }
        return author.toString().trim();
    }

    /**
     * Gets Issue.
     * 
     * @return String
     */
    public String getArticleNum() {
        return artifactFields.get(ART_NUM);
    }

    /**
     * @return the isParsed
     */
    public boolean isParsed() {
        return isParsed;
    }
}
