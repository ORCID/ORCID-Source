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
package org.orcid.core.security.visibility.filter.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.orcid.core.security.visibility.filter.VisibilityFilter;
import org.orcid.core.tree.TreeCleaner;
import org.orcid.core.tree.TreeCleaningDecision;
import org.orcid.core.tree.TreeCleaningStrategy;
import org.orcid.jaxb.model.message.Orcid;
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResults;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.VisibilityType;
import org.orcid.jaxb.model.message.WorkContributors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * I would imagine the first time you see this class, it may be a bit confusing.
 * <p/>
 * The goal of this class is to insure that no elements that are marked with a
 * {@link org.orcid.jaxb.model.message .Visibility} are displayed to the caller
 * unless they have sufficient permissions.
 * <p/>
 * In addition, we need to make sure that the actual visibility attributes are
 * removed (if requested with the removeAttribute argument) are nulled in order
 * for callers to be unaware of elements that have been requested to be made
 * unavailable.
 * <p/>
 * This is a very important step in the security model and thus, reflection was
 * chosen to scan the JaxB objects and locate any classes implementing the
 * {@link org.orcid.jaxb.model.message.VisibilityType} interface and check on
 * the visibility level requested.
 * <p/>
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 16/03/2012
 */
@Component("visibilityFilter")
public class VisibilityFilterImpl implements VisibilityFilter {

    private final static Logger LOGGER = LoggerFactory.getLogger(VisibilityFilterImpl.class);

    /**
     * Remove the elements that are not present in the list of set of
     * {@link org.orcid.jaxb.model.message .Visibility}s present in the array
     * passed in By default the remaining visibility elements will not be
     * removed from the object.
     * 
     * @param messageToBeFiltered
     *            the {@link org.orcid.jaxb.model.message.OrcidMessage} that
     *            will be traversed looking for
     *            {@link org .orcid.jaxb.model.message.VisibilityType}
     * @param visibilities
     *            What {@link org.orcid.jaxb.model.message.Visibility} elements
     *            should be allowed.
     * @return the cleansed {@link org.orcid.jaxb.model.message.OrcidMessage}
     */
    @Override
    public OrcidMessage filter(OrcidMessage messageToBeFiltered, Visibility... visibilities) {
        return filter(messageToBeFiltered, false, visibilities);
    }

    /**
     * Remove the elements that are not present in the list of set of
     * {@link org.orcid.jaxb.model.message .Visibility}s present in the array
     * passed in.
     * 
     * @param messageToBeFiltered
     *            the {@link org.orcid.jaxb.model.message.OrcidMessage} that
     *            will be traversed looking for
     *            {@link org .orcid.jaxb.model.message.VisibilityType} elements.
     * @param removeAttribute
     *            should all {@link org.orcid.jaxb.model.message.Visibility}
     *            elements be removed from the object graph. This has the effect
     *            that they will not be present in the resulting JAXB
     *            serialisation.
     * @param visibilities
     *            What {@link org.orcid.jaxb.model.message.Visibility} elements
     *            should be allowed.
     * @return the cleansed {@link org.orcid.jaxb.model.message.OrcidMessage}
     */
    @Override
    public OrcidMessage filter(OrcidMessage messageToBeFiltered, final boolean removeAttribute, Visibility... visibilities) {
        if (messageToBeFiltered == null || visibilities == null || visibilities.length == 0) {
            return null;
        }
        String messageIdForLog = getMessageIdForLog(messageToBeFiltered);
        LOGGER.debug("About to filter message: " + messageIdForLog);
        final Set<Visibility> visibilitySet = new HashSet<Visibility>(Arrays.asList(visibilities));
        if (visibilitySet.contains(Visibility.SYSTEM)) {
            return messageToBeFiltered;
        } else {
            TreeCleaner treeCleaner = new TreeCleaner();
            //If the object to filter is the orcid-search-results, dont clean it if it is empty
            if(messageIdForLog.equalsIgnoreCase("orcid-search-results"))
            	treeCleaner.setRemoveEmptyObjects(false);
            treeCleaner.clean(messageToBeFiltered, new TreeCleaningStrategy() {
                public TreeCleaningDecision needsStripping(Object obj) {
                    TreeCleaningDecision decision = TreeCleaningDecision.DEFAULT;
                    if (obj != null) {
                        Class<?> clazz = obj.getClass();
                        if (WorkContributors.class.isAssignableFrom(clazz)) {
                            decision = TreeCleaningDecision.IGNORE;
                        } else if (VisibilityType.class.isAssignableFrom(clazz)) {
                            VisibilityType visibilityType = (VisibilityType) obj;
                            if ((visibilityType.getVisibility() == null || !visibilitySet.contains(visibilityType.getVisibility()))) {
                                decision = TreeCleaningDecision.CLEANING_REQUIRED;
                            }
                            if (removeAttribute) {
                                visibilityType.setVisibility(null);
                            }
                        }
                    }
                    return decision;
                }
            });
            OrcidProfile orcidProfile = messageToBeFiltered.getOrcidProfile();
            if (orcidProfile != null) {
                orcidProfile.setOrcidInternal(null);
            }
            LOGGER.debug("Finished filtering message: " + messageIdForLog);
            return messageToBeFiltered;
        }
    }

    private String getMessageIdForLog(OrcidMessage messageToBeFiltered) {
        String messageIdForLog = "unknown";
        OrcidSearchResults orcidSearchResults = messageToBeFiltered.getOrcidSearchResults();
        OrcidProfile orcidProfile = messageToBeFiltered.getOrcidProfile();
        if (orcidSearchResults != null) {
            messageIdForLog = "orcid-search-results";
        } else if (orcidProfile != null) {
            OrcidIdentifier orcidIdentifier = orcidProfile.getOrcidIdentifier();
            if (orcidIdentifier != null) {
                messageIdForLog = orcidIdentifier.getPath();
            }
            Orcid orcid = orcidProfile.getOrcid();
            if (orcid != null) {
                messageIdForLog = orcid.getValue();
            }
        }
        return messageIdForLog;
    }

}
