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
package org.orcid.jaxb.model.record_rc2;

import javax.xml.bind.annotation.XmlRootElement;

import org.orcid.jaxb.model.common.Url;

@XmlRootElement(name = "application-website")
public class ApplicationWebsite extends Url {
    private static final long serialVersionUID = 1204251226767139535L;
}
