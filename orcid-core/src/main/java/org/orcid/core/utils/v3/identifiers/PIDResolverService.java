package org.orcid.core.utils.v3.identifiers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.core.utils.v3.identifiers.resolvers.LinkResolver;
import org.orcid.core.utils.v3.identifiers.resolvers.MetadataResolver;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.v3.rc2.record.ExternalID;
import org.orcid.jaxb.model.v3.rc2.record.Work;
import org.orcid.pojo.PIDResolutionResult;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

@Component
public class PIDResolverService {

    @Resource
    List<LinkResolver> linkResolvers = new ArrayList<LinkResolver>();

    @Resource
    List<MetadataResolver> metaResolvers = new ArrayList<MetadataResolver>();

    @Resource
    IdentifierTypeManager idman;

    Map<String, LinkedList<LinkResolver>> linkResolverMap = new HashMap<String, LinkedList<LinkResolver>>();
    Map<String, LinkedList<MetadataResolver>> metaResolverMap = new HashMap<String, LinkedList<MetadataResolver>>();

    @PostConstruct
    public void init() {
        Collections.sort(linkResolvers, AnnotationAwareOrderComparator.INSTANCE);
        
        //initialise lookup
        for (String type : idman.fetchIdentifierTypesByAPITypeName(Locale.ENGLISH).keySet()) {
            linkResolverMap.put(type, new LinkedList<LinkResolver>());
            metaResolverMap.put(type, new LinkedList<MetadataResolver>());
        }
        
        //populate lookup maps for link checking
        for (LinkResolver n : linkResolvers) {
            List<String> supported = n.canHandle();
            if (supported.equals(LinkResolver.CAN_HANDLE_EVERYTHING)) {
                for (String type : linkResolverMap.keySet())
                    linkResolverMap.get(type).add(n);
            } else {
                for (String type : supported) {
                    linkResolverMap.get(type).add(n);
                }
            }
        }
        
        //populate lookup maps for metadata resolution
        for (MetadataResolver n : metaResolvers) {
            List<String> supported = n.canHandle();
            if (supported.equals(MetadataResolver.CAN_HANDLE_EVERYTHING)) {
                for (String type : metaResolverMap.keySet())
                    metaResolverMap.get(type).add(n);
            } else {
                for (String type : supported) {
                    metaResolverMap.get(type).add(n);
                }
            }
        }

    }

    /**
     * Ensure this is the API type name, not the DB type name.
     * 
     * @param type the api type name
     * @param value the url value
     * @return a resolution result containing the resolved URL (if successful), 
     * a flag indicating success and a flag indicating if resolution was attempted 
     * (i.e. there is a resolver that can handle the type)
     */
    public PIDResolutionResult resolve(String apiTypeName, String value) {
        PIDResolutionResult result = PIDResolutionResult.NOT_ATTEMPTED;
        if (apiTypeName == null || value == null)
            return result;
        
        for (LinkResolver r : linkResolverMap.get(apiTypeName)) {
            result = r.resolve(apiTypeName, value);
            if (result.isResolved()) {                
                return result;
            }
        } 
        return result;
    }
    
    /** Returns a Work populated with metadata attached to the PID
     * NOTE: may only be semi-populated.
     * Ensure this is the API type name, not the DB type name.
     * 
     * @param type the api type name
     * @param value the url value
     * @return a work containing all the available metadata we could find or NULL if none found.
     */
    public Work resolveMetadata(String apiTypeName, String value) {
        Work result = null;
        if (apiTypeName == null || value == null)
            return result;
        
        for (MetadataResolver r : metaResolverMap.get(apiTypeName)) {
            result = r.resolveMetadata(apiTypeName, value);
            if (result != null)
                return checkWorkAndIdentifierTypes(result);
        } 
        return result;
    }

    private Work checkWorkAndIdentifierTypes(Work work) {
        if (WorkType.BOOK_CHAPTER.equals(work.getWorkType())) {
            for (ExternalID externalID : work.getExternalIdentifiers().getExternalIdentifier()) {
                if ("isbn".equals(externalID.getType())) {
                    externalID.setRelationship(Relationship.PART_OF);
                }
            }
        }
        return work;
    }

}
