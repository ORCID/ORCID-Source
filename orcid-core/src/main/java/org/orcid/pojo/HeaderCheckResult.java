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
package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Will Simpson
 *
 */
public class HeaderCheckResult {

    private List<HeaderMismatch> mismatches = new ArrayList<>(20);

    public boolean isSuccess() {
        return mismatches.isEmpty();
    }

    public List<HeaderMismatch> getMismatches() {
        return mismatches;
    }

    public void addMismatch(HeaderMismatch mismatch) {
        mismatches.add(mismatch);
    }

    @Override
    public String toString() {
        return "HeaderCheckResult [mismatches=" + mismatches + "]";
    }

}
