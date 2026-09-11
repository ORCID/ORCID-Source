package org.orcid.core.manager.impl;

import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.orcid.core.manager.AppIdGenerationManager;

/**
 * 
 * @author Will Simpson
 * 
 */
@Transactional(value = "transactionManager")
public class AppIdGenerationManagerImpl implements AppIdGenerationManager {

    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Override
    public String createNewAppId() {
        return "APP-" + RandomStringUtils.random(16, CHARS);
    }

}
