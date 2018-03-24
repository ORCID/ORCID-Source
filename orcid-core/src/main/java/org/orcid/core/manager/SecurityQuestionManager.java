package org.orcid.core.manager;

import java.util.Map;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface SecurityQuestionManager {

    Map<String, String> retrieveSecurityQuestionsAsMap();
    
    Map<String, String> retrieveSecurityQuestionsAsInternationalizedMap();

}
