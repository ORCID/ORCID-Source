package org.orcid.core.utils.v3.identifiers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.core.utils.v3.identifiers.resolvers.LinkResolver;
import org.orcid.core.utils.v3.identifiers.resolvers.MetadataResolver;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.pojo.IdentifierType;
import org.orcid.pojo.PIDResolutionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

@Component
public class PIDResolverService {

    @Autowired
    private List<LinkResolver> linkResolvers = new ArrayList<>();

    @Autowired
    private List<MetadataResolver> metaResolvers = new ArrayList<>();

    @Autowired
    @Lazy
    private IdentifierTypeManager idman;

    private Map<String, LinkedList<LinkResolver>> linkResolverMap = new HashMap<>();
    private Map<String, LinkedList<MetadataResolver>> metaResolverMap = new HashMap<>();

    private boolean initialized = false;

    @PostConstruct
    public void init() {
        Collections.sort(linkResolvers, AnnotationAwareOrderComparator.INSTANCE);
        Collections.sort(metaResolvers, AnnotationAwareOrderComparator.INSTANCE);
    }

    private synchronized void ensureInitialized() {
        if (!initialized && idman != null) {
            Map<String, IdentifierType> types = idman.fetchIdentifierTypesByAPITypeName(Locale.ENGLISH);
            if (types != null) {
                for (String type : types.keySet()) {
                    linkResolverMap.put(type, new LinkedList<>());
                    metaResolverMap.put(type, new LinkedList<>());
                }
            }

            for (LinkResolver n : linkResolvers) {
                List<String> supported = n.canHandle();
                if (supported.equals(LinkResolver.CAN_HANDLE_EVERYTHING)) {
                    for (String type : linkResolverMap.keySet()) {
                        linkResolverMap.get(type).add(n);
                    }
                } else {
                    for (String type : supported) {
                        linkResolverMap.computeIfAbsent(type, k -> new LinkedList<>()).add(n);
                    }
                }
            }

            for (MetadataResolver n : metaResolvers) {
                List<String> supported = n.canHandle();
                if (supported.equals(MetadataResolver.CAN_HANDLE_EVERYTHING)) {
                    for (String type : metaResolverMap.keySet()) {
                        metaResolverMap.get(type).add(n);
                    }
                } else {
                    for (String type : supported) {
                        metaResolverMap.computeIfAbsent(type, k -> new LinkedList<>()).add(n);
                    }
                }
            }

            initialized = true;
        }
    }

    public PIDResolutionResult resolve(String apiTypeName, String value) {
        PIDResolutionResult result = PIDResolutionResult.NOT_ATTEMPTED;
        if (apiTypeName == null || value == null)
            return result;

        ensureInitialized();

        List<LinkResolver> resolvers = linkResolverMap.get(apiTypeName);
        if (resolvers != null) {
            for (LinkResolver r : resolvers) {
                result = r.resolve(apiTypeName, value);
                if (result.isResolved()) {
                    return result;
                }
            }
        }
        return result;
    }

    public Work resolveMetadata(String apiTypeName, String value) {
        if (apiTypeName == null || value == null) {
            return null;
        }

        ensureInitialized();

        List<MetadataResolver> resolvers = metaResolverMap.get(apiTypeName);
        if (resolvers != null) {
            for (MetadataResolver r : resolvers) {
                Work result = r.resolveMetadata(apiTypeName, value);
                if (result != null) {
                    return checkWorkAndIdentifierTypes(result);
                }
            }
        }
        return null;
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