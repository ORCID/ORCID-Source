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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 * @author Will Simpson
 * 
 */
public class CrossRefContextTest {

    @Test
    public void testParseJournalArticle() {
        String coins = "ctx_ver=Z39.88-2004&rft_val_fmt=info:ofi\\/fmt:kev:mtx:journal&rft_id=info:doi\\/10.1029\\/2002JD002436&rtf.genre=journal-article&rtf.spage=1255&rtf.date=2003&rtf.aulast=Riemer&rtf.aufirst=N.&rtf.auinit=N&rtf.atitle=5 on chemistry and nitrate aerosol formation in the lower troposphere under photosmog conditions&rtf.jtitle=Journal of Geophysical Research&rtf.volume=30&rtf.issue=D4";
        CrossRefContext result = new CrossRefContext(coins);
        assertNotNull(result);
        result.parse();
        assertEquals("Riemer N.", result.getAuthor());
        assertEquals("Journal of Geophysical Research", result.getJTitle());
        assertEquals("30", result.getVolume());
        assertEquals("D4", result.getIssue());
        assertEquals("1255", result.getSPage());
        assertEquals("2003", result.getDate());
    }

    @Test
    public void testParseBookPrefaceWithRomanNumerals() {
        String coins = "ctx_ver=Z39.88-2004&rft_val_fmt=info:ofi\\/fmt:kev:mtx:journal&rft_id=info:doi\\/10.1017\\/CBO9780511494185.002&rtf.genre=book&rtf.spage=ix&rtf.epage=xv&rtf.date=2009&rtf.aulast=Simpson&rtf.aufirst=Gerry&rtf.auinit=G&rtf.btitle=null&rtf.isbn=9780521534901";
        CrossRefContext result = new CrossRefContext(coins);
        assertNotNull(result);
        result.parse();
        assertEquals("Simpson Gerry", result.getAuthor());
        assertNull(result.getJTitle());
        assertNull(result.getVolume());
        assertNull(result.getIssue());
        assertEquals("2009", result.getDate());
        assertEquals("ix", result.getSPage());
        assertEquals("xv", result.getEPage());
    }

}
