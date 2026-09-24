package org.orcid.admin.cli;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.frontend.spring.session.redis.OrcidRedisIndexedSessionRepository;
import org.orcid.persistence.dao.ProfileDao;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class SetForcePasswordResetTest {

    private static final String CONFIG_FILE_KEY = "org.orcid.config.file";

    @Mock
    private ProfileDao profileDao;

    @Mock
    private OrcidRedisIndexedSessionRepository sessionRepository;

    @Mock
    private ConfigurableEnvironment environment;

    private SetForcePasswordReset setForcePasswordReset;

    @Before
    public void setUp() {
        setForcePasswordReset = new SetForcePasswordReset();
        ReflectionTestUtils.setField(setForcePasswordReset, "profileDao", profileDao);
        ReflectionTestUtils.setField(setForcePasswordReset, "sessionRepository", sessionRepository);
    }

    @Test
    public void loadSessionRedisPropertiesShouldReadSystemEnvironmentValueAndStripFilePrefix() throws IOException {
        File propertiesFile = createTempPropertiesFile("redis.host=localhost");
        setContextWithConfigFilePath("file:" + propertiesFile.getAbsolutePath(), null);

        Properties properties = ReflectionTestUtils.invokeMethod(setForcePasswordReset, "loadSessionRedisProperties");

        assertEquals("localhost", properties.getProperty("redis.host"));
    }

    @Test
    public void loadSessionRedisPropertiesShouldFallbackToEnvironmentPropertyAndStripFilePrefix() throws IOException {
        File propertiesFile = createTempPropertiesFile("redis.host=localhost");
        setContextWithConfigFilePath(null, "file:" + propertiesFile.getAbsolutePath());

        Properties properties = ReflectionTestUtils.invokeMethod(setForcePasswordReset, "loadSessionRedisProperties");

        assertEquals("localhost", properties.getProperty("redis.host"));
    }

    @Test
    public void executeShouldUpdateForcePasswordResetInTenThousandSizedBatches() {
        List<String> orcidIds = new ArrayList<>();
        for (int i = 0; i < 25001; i++) {
            orcidIds.add(String.format("0000-0000-0000-%04d", i));
        }

        ReflectionTestUtils.setField(setForcePasswordReset, "orcidIds", String.join(",", orcidIds));

        when(profileDao.orcidExists(anyString())).thenReturn(true);
        when(profileDao.updateForcePasswordReset(any(List.class), any(Date.class))).thenAnswer(invocation -> ((List<?>) invocation.getArgument(0)).size());
        when(sessionRepository.findByIndexNameAndIndexValue(eq(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME), anyString()))
                .thenReturn(Collections.emptyMap());

        setForcePasswordReset.execute();

        ArgumentCaptor<List> batchCaptor = ArgumentCaptor.forClass(List.class);
        verify(profileDao, times(3)).updateForcePasswordReset(batchCaptor.capture(), any(Date.class));
        verify(sessionRepository, times(25001)).findByIndexNameAndIndexValue(eq(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME), anyString());

        List<List> batches = batchCaptor.getAllValues();
        assertEquals(10000, batches.get(0).size());
        assertEquals(10000, batches.get(1).size());
        assertEquals(5001, batches.get(2).size());
    }

    private void setContextWithConfigFilePath(String systemEnvironmentValue, String propertyValue) {
        org.springframework.context.support.ClassPathXmlApplicationContext context = org.mockito.Mockito.mock(org.springframework.context.support.ClassPathXmlApplicationContext.class);
        when(context.getEnvironment()).thenReturn(environment);
        HashMap<String, Object> systemEnvironment = new HashMap<>();
        if (systemEnvironmentValue != null) {
            systemEnvironment.put(CONFIG_FILE_KEY, systemEnvironmentValue);
        }
        when(environment.getSystemEnvironment()).thenReturn(systemEnvironment);
        when(environment.getProperty(CONFIG_FILE_KEY)).thenReturn(propertyValue);
        ReflectionTestUtils.setField(setForcePasswordReset, "context", context);
    }

    private File createTempPropertiesFile(String content) throws IOException {
        File file = File.createTempFile("set-force-password-reset", ".properties");
        file.deleteOnExit();
        Files.write(file.toPath(), Collections.singletonList(content), StandardCharsets.UTF_8);
        return file;
    }
}
