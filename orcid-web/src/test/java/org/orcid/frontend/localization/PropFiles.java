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
package org.orcid.frontend.localization;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipException;

import org.junit.Test;

import org.junit.Before;
import org.orcid.core.cli.FindOrcidWorkDuplicates;
import org.orcid.core.utils.Classpath;
import org.orcid.core.utils.Classpath.FileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropFiles {
	ArrayList<Properties> pList = new ArrayList<>();

	private static final Logger LOG = LoggerFactory.getLogger(PropFiles.class);

	@Before
	public void loadProFiles() {
		// ResourceBundle resources = ResourceBundle.
		FileFilter fF = new FileFilter() {
			public boolean accept(String fileName) {
				return fileName
						.matches(".*messages[a-zA-Z\\_\\.]*\\.properties$");
			}
		};

		try {
			String[] propfiles = Classpath.getClasspathFileNames(fF, true);
			for (String profile : propfiles) {
				Properties prop = new Properties();
				prop.load(new FileInputStream(profile));
				pList.add(prop);
			}
		} catch (ZipException e) {
			LOG.error("ZipException", e);
		} catch (IOException e) {
			LOG.error("IOException", e);
		}
	}

	@Test
	public void PropNamesmatch() throws Exception {
		Properties pA = pList.get(0);
		for (int i = 1; i < pList.size(); i++) {
			Set<Object> aSet = pA.keySet();
			Properties pB = pList.get(i);
			for (Object key : pB.keySet()) {
				if (!aSet.contains(key)) {
					LOG.equals("missing key" + key);
					throw new Exception("missing key" + key);
				}
				aSet.remove(key);
			}
			if (aSet.size() != 0) {
				LOG.equals("missing key/keys" + aSet.toString());
				throw new Exception("missing key/keys" + aSet.toString());
			}
			assertTrue(aSet.size() == 0);
		}
	}

}
