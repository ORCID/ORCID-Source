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
package orcid.pojo.ajaxForm;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Properties;

import org.junit.Test;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.pojo.ajaxForm.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkTest {
	ArrayList<Properties> pList = new ArrayList<>();

	private static final Logger LOG = LoggerFactory.getLogger(WorkTest.class);

	@Test
	public void testSerilalize() {
	    OrcidWork op = new OrcidWork();
	    Work works = new Work(op);
	    OrcidWork op2  = works.toOrcidWork();
	    assertEquals(op.toString(), op2.toString());
	}

}
