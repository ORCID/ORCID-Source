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
package org.orcid.core.cli.logs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.persistence.NoResultException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.utils.JsonUtils;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.springframework.test.util.ReflectionTestUtils;

public class ApiAccessLogsAnalyserTest {

    private static final String TOKEN_1 = "01234567-1234-1234-1234-012345678910";

    private static final String TOKEN_2 = "76543210-4321-4321-4321-019876543210";

    private static final String BAD_TOKEN = "00000000-0000-0000-0000-000000000000";

    private static final String CLIENT_DETAILS_1 = "client-details-1";

    private static final String CLIENT_DETAILS_2 = "client-details-2";

    private ApiAccessLogsAnalyser analyser = new ApiAccessLogsAnalyser();

    @Mock
    private OrcidOauth2TokenDetailDao tokenDao;

    @Mock
    private LogReader logReader;

    private ByteArrayOutputStream output;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        
        output = new ByteArrayOutputStream();
        AnalysisResults results = new AnalysisResults();
        results.setOutputStream(output);

        ReflectionTestUtils.setField(analyser, "tokenDao", tokenDao);
        ReflectionTestUtils.setField(analyser, "results", results);
        ReflectionTestUtils.setField(analyser, "logReader", logReader);
        ReflectionTestUtils.setField(analyser, "logsDir", new File("Not a real file"));

        Mockito.when(logReader.getNextLine())
                .thenReturn(
                        "127.0.0.1 - - [17/Oct/2017:00:00:01 +0000] \"GET /orcid-api-web/v2.1/0000-0001-8120-7596/record HTTP/1.0\" 200 3695 0.046 \"-\" \"-\" \"-\" - \"application/vnd.orcid+xml;charset=UTF-8\" \"-\" \"10.183.248.6\" \"http-nio-8080-exec-85\" \"bearer "
                                + TOKEN_1 + "\" \"-\" \"-\" \"-\"\n")
                .thenReturn(
                        "127.0.0.1 - - [17/Oct/2017:00:00:02 +0000] \"GET /orcid-api-web/v2.1/0000-0001-8120-7596/record HTTP/1.0\" 200 3695 0.046 \"-\" \"-\" \"-\" - \"application/vnd.orcid+xml;charset=UTF-8\" \"-\" \"10.183.248.6\" \"http-nio-8080-exec-85\" \"bearer "
                                + TOKEN_1 + "\" \"-\" \"-\" \"-\"\n")
                .thenReturn(
                        "127.0.0.1 - - [17/Oct/2017:00:00:03 +0000] \"GET /orcid-api-web/v2.0/0000-0001-8120-7596/record HTTP/1.0\" 401 3695 0.046 \"-\" \"-\" \"-\" - \"application/vnd.orcid+xml;charset=UTF-8\" \"-\" \"10.183.248.6\" \"http-nio-8080-exec-85\" \"bearer "
                                + BAD_TOKEN + "\" \"-\" \"-\" \"-\"\n")
                .thenReturn(
                        "127.0.0.1 - - [17/Oct/2017:00:00:04 +0000] \"GET /orcid-api-web/v3.0_dev1/0000-0001-8120-7596/record HTTP/1.0\" 200 3695 0.046 \"-\" \"-\" \"-\" - \"application/vnd.orcid+xml;charset=UTF-8\" \"-\" \"10.183.248.6\" \"http-nio-8080-exec-85\" \"bearer "
                                + TOKEN_1 + "\" \"-\" \"-\" \"-\"\n")
                .thenReturn(
                        "127.0.0.1 - - [17/Oct/2017:00:00:05 +0000] \"GET /orcid-api-web/v2.0/0000-0001-8120-7596/record HTTP/1.0\" 200 3695 0.046 \"-\" \"-\" \"-\" - \"application/vnd.orcid+xml;charset=UTF-8\" \"-\" \"10.183.248.6\" \"http-nio-8080-exec-85\" \"bearer "
                                + TOKEN_1 + "\" \"-\" \"-\" \"-\"\n")
                .thenReturn(
                        "127.0.0.1 - - [17/Oct/2017:00:00:06 +0000] \"GET /orcid-api-web/v2.0/0000-0001-8120-7596/record HTTP/1.0\" 200 3695 0.046 \"-\" \"-\" \"-\" - \"application/vnd.orcid+xml;charset=UTF-8\" \"-\" \"10.183.248.6\" \"http-nio-8080-exec-85\" \"bearer "
                                + TOKEN_2 + "\" \"-\" \"-\" \"-\"\n")
                .thenReturn(
                        "127.0.0.1 - - [17/Oct/2017:00:00:07 +0000] \"GET /orcid-api-web/v2.0/0000-0001-8120-7596/record HTTP/1.0\" 200 3695 0.046 \"-\" \"-\" \"-\" - \"application/vnd.orcid+xml;charset=UTF-8\" \"-\" \"10.183.248.6\" \"http-nio-8080-exec-85\" \"bearer "
                                + TOKEN_2 + "\" \"-\" \"-\" \"-\"\n")
                .thenReturn(
                        "127.0.0.1 - - [17/Oct/2017:00:00:08 +0000] \"GET /orcid-api-web/v2.0/0000-0001-8120-7596/record HTTP/1.0\" 200 3695 0.046 \"-\" \"-\" \"-\" - \"application/vnd.orcid+xml;charset=UTF-8\" \"-\" \"10.183.248.6\" \"http-nio-8080-exec-85\" \"bearer "
                                + TOKEN_2 + "\" \"-\" \"-\" \"-\"\n")
                .thenReturn(
                        "???d??? 127.0.0.1 - - 10.183.248.6 Thu 10-17-2017 00:00:08.238 UTC \"GET /orcid-api-web/v2.0/0000-0001-8120-7596/record HTTP/1.0\" 200 3695 0.046 \"-\" \"-\" \"-\" - \"application/vnd.orcid+xml;charset=UTF-8\" \"-\" \"10.183.248.6\" \"http-nio-8080-exec-85\" \"bearer "
                                + TOKEN_2 + "\" \"-\" \"-\" \"-\"\n")
                .thenReturn(null);
        Mockito.when(tokenDao.findByTokenValue(Mockito.eq(TOKEN_1))).thenReturn(getOrcidOauth2TokenDetailClientA());
        Mockito.when(tokenDao.findByTokenValue(Mockito.eq(TOKEN_2))).thenReturn(getOrcidOauth2TokenDetailClientB());
        Mockito.when(tokenDao.findByTokenValue(Mockito.eq(BAD_TOKEN))).thenThrow(new NoResultException());
    }

    @Test
    public void test() {
        analyser.analyse();
        String outputText = output.toString();
        assertNotNull(outputText);
        assertFalse(outputText.isEmpty());
        AnalysisResults output = JsonUtils.readObjectFromJsonString(outputText, AnalysisResults.class);
        assertEquals(9, output.getHitsAnalysed());
        assertEquals(3, output.getClientResults().size());
        
        for (ClientStats clientStats : output.getClientResults()) {
            if (CLIENT_DETAILS_1.equals(clientStats.getClientDetailsId())) {
                assertEquals(3, clientStats.getVersionsHit().size());
                assertEquals(4, clientStats.getTotalHits());
            }
            if (CLIENT_DETAILS_2.equals(clientStats.getClientDetailsId())) {
                assertEquals(1, clientStats.getVersionsHit().size());
                assertEquals(4, clientStats.getTotalHits());
            }
            if (ApiAccessLogsAnalyser.UNKNOWN_CLIENT.equals(clientStats.getClientDetailsId())) {
                assertEquals(1, clientStats.getVersionsHit().size());
                assertEquals(1, clientStats.getTotalHits());
            }
        }
    }

    private OrcidOauth2TokenDetail getOrcidOauth2TokenDetailClientB() {
        OrcidOauth2TokenDetail token = new OrcidOauth2TokenDetail();
        token.setClientDetailsId(CLIENT_DETAILS_2);
        token.setTokenValue(TOKEN_2);
        return token;
    }

    private OrcidOauth2TokenDetail getOrcidOauth2TokenDetailClientA() {
        OrcidOauth2TokenDetail token = new OrcidOauth2TokenDetail();
        token.setClientDetailsId(CLIENT_DETAILS_1);
        token.setTokenValue(TOKEN_1);
        return token;
    }

}
