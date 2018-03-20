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
