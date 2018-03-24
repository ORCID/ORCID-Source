package org.orcid.persistence.dao;

import java.util.List;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface FundingSubTypeToIndexDao {
    void addSubTypes(String subtype, String orcid);
    void removeSubTypes(String subtype);
    void removeSubTypes(String subtype, String orcid);
    List<String> getSubTypes();
}
