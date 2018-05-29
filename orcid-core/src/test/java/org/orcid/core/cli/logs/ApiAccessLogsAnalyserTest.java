package org.orcid.core.cli.logs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.persistence.NoResultException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.utils.JsonUtils;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.springframework.test.util.ReflectionTestUtils;

public class ApiAccessLogsAnalyserTest {

    private static final String TOKEN_1 = "01234567-1234-1234-1234-012345678910";

    private static final String TOKEN_2 = "76543210-4321-4321-4321-019876543210";

    private static final String BAD_TOKEN = "00000000-0000-0000-0000-000000000000";

    private static final String CLIENT_DETAILS_1 = "client-details-1";

    private static final String CLIENT_DETAILS_2 = "client-details-2";
    
    private static final String CLIENT_DETAILS_NAME_1 = "client-details-name-1";
    
    private static final String CLIENT_DETAILS_NAME_2 = "client-details-name-2";

    private ApiAccessLogsAnalyser analyser = new ApiAccessLogsAnalyser();

    @Mock
    private OrcidOauth2TokenDetailDao tokenDao;
    
    @Mock
    private ClientDetailsDao clientDetailsDao;

    @Mock
    private LogReader logReader;

    private ByteArrayOutputStream output;
    
    private ByteArrayOutputStream summary;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        
        output = new ByteArrayOutputStream();
        summary = new ByteArrayOutputStream();
        AnalysisResults results = new AnalysisResults();
        results.setOutputStream(output);
        results.setSummaryOutputStream(summary);
        results.setClientDetailsDao(clientDetailsDao);

        ReflectionTestUtils.setField(analyser, "tokenDao", tokenDao);
        ReflectionTestUtils.setField(analyser, "results", results);
        ReflectionTestUtils.setField(analyser, "logReader", logReader);
        ReflectionTestUtils.setField(analyser, "logDirs", Arrays.asList(new File("Not a real file")));

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
                        "127.0.0.1 - - [17/Oct/2017:00:00:04 +0000] \"GET /orcid-api-web/v3.0_rc1/0000-0001-8120-7596/record HTTP/1.0\" 200 3695 0.046 \"-\" \"-\" \"-\" - \"application/vnd.orcid+xml;charset=UTF-8\" \"-\" \"10.183.248.6\" \"http-nio-8080-exec-85\" \"bearer "
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
        Mockito.when(clientDetailsDao.getMemberName(Mockito.eq(CLIENT_DETAILS_1))).thenReturn(CLIENT_DETAILS_NAME_1);
        Mockito.when(clientDetailsDao.getMemberName(Mockito.eq(CLIENT_DETAILS_2))).thenReturn(CLIENT_DETAILS_NAME_2);
    }

    @Test
    public void test() {
        analyser.analyse();
        String outputText = output.toString();
        assertNotNull(outputText);
        assertFalse(outputText.isEmpty());
        AnalysisResults output = JsonUtils.readObjectFromJsonString(outputText, AnalysisResults.class);
        assertEquals(8, output.getHitsAnalysed());
        assertEquals(2, output.getClientResults().size());
        
        for (ClientStats clientStats : output.getClientResults()) {
            if (CLIENT_DETAILS_1.equals(clientStats.getClientDetailsId())) {
                assertEquals(3, clientStats.getVersionsHit().size());
                assertEquals(4, clientStats.getTotalHits());
                assertEquals(CLIENT_DETAILS_NAME_1, clientStats.getClientName());
            }
            if (CLIENT_DETAILS_2.equals(clientStats.getClientDetailsId())) {
                assertEquals(1, clientStats.getVersionsHit().size());
                assertEquals(4, clientStats.getTotalHits());
                assertEquals(CLIENT_DETAILS_NAME_2, clientStats.getClientName());
            }
        }
        
        outputText = summary.toString();
        AnalysisSummary summary = JsonUtils.readObjectFromJsonString(outputText, AnalysisSummary.class);
        assertEquals(0, summary.getNumV1Clients());
        assertEquals(2, summary.getNumV2Clients());
        assertEquals(1, summary.getNumV3Clients());
        assertEquals(1, summary.getNumClientsUsingMultipleVersions());
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
