package org.orcid.core.manager;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.orcid.core.BaseTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Will Simpson
 * 
 */
public class AppIdGenerationManagerTest extends BaseTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppIdGenerationManager.class);

    @Resource
    private AppIdGenerationManager appIdGenerationManager;

    @Test
    public void testGenerateAppId() {
        List<String> appIds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String appId = appIdGenerationManager.createNewAppId();
            LOGGER.info("Got app ID: {}", appId);
            assertNotNull(appId);
            assertTrue(appId.matches("APP-[a-zA-Z0-9]{16}"));
            assertFalse(appIds.contains(appId));
            appIds.add(appId);
        }
    }

}
