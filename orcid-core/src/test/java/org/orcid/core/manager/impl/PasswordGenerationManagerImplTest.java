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
package org.orcid.core.manager.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.PasswordGenerationManager;
import org.orcid.password.constants.OrcidPasswordConstants;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class PasswordGenerationManagerImplTest {

	@Resource
	private PasswordGenerationManager passwordGenerationManager;	
	
	@Test
	public void testCreateNewPassword() {
		char[] newPassword=passwordGenerationManager.createNewPassword();
		assertNotNull(newPassword);
		assertTrue(newPassword.length==12);
		Pattern passwordPattern = Pattern.compile(OrcidPasswordConstants.ORCID_PASSWORD_REGEX);
		Matcher matcher = passwordPattern.matcher(new String(newPassword));
		assertTrue(matcher.matches());
		
	}

}
