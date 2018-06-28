package org.orcid.persistence.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.GroupingSuggestionDao;
import org.orcid.persistence.jpa.entities.GroupingSuggestionEntity;

public class GroupingSuggestionDaoImpl extends GenericDaoImpl<GroupingSuggestionEntity, Long> implements GroupingSuggestionDao {

    public GroupingSuggestionDaoImpl() {
        super(GroupingSuggestionEntity.class);
    }
    
    @Override
    public List<GroupingSuggestionEntity> getGroupingSuggestions(String orcid) {
        TypedQuery<GroupingSuggestionEntity> query = entityManager.createQuery("FROM GroupingSuggestionEntity WHERE orcid = :orcid and dismissedDate IS NULL", GroupingSuggestionEntity.class);
        query.setParameter("orcid", orcid);
        return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public GroupingSuggestionEntity findGroupingSuggestionIdByPutCodes(String orcid, Long workPutCode, List<Long> comparisonPutCodes) {
        Map<String, String> params = new HashMap<>();
        params.put("workPutCode", workPutCode.toString());
        
        StringBuilder queryString = new StringBuilder("SELECT * FROM grouping_suggestion JOIN LATERAL json_array_elements_text(work_put_codes_json->'workPutCodes') j1(join1) ON TRUE JOIN LATERAL json_array_elements_text(work_put_codes_json->'workPutCodes') j2(join2) ON TRUE WHERE orcid = :orcid AND join1 = :workPutCode");
        queryString.append(" AND ");
        if (comparisonPutCodes.size() > 1) {
            queryString.append("(");
            for (int i = 1; i < comparisonPutCodes.size(); i++) {
                String comparison = comparisonPutCodes.get(i).toString();
                queryString.append("join2 = ");
                queryString.append(":comparison").append(i);
                params.put("comparison" + i, comparison);
                if (i < comparisonPutCodes.size() - 1) {
                    queryString.append(" OR ");
                }
            }
            queryString.append(")");
        } else {
            queryString.append("join2 = :comparison");
            params.put("comparison", comparisonPutCodes.get(0).toString());
        }
        
        Query query = entityManager.createNativeQuery(queryString.toString(), GroupingSuggestionEntity.class);
        for (String key : params.keySet()) {
            query.setParameter(key, params.get(key));
        }
        query.setParameter("orcid", orcid);
        List<GroupingSuggestionEntity> result = query.getResultList();
        return result.size() > 0 ? result.get(0) : null;
    }

}
