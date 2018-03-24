package org.orcid.core.security.visibility.filter.impl;

import java.security.AccessControlException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.orcid.core.security.PermissionChecker;
import org.orcid.core.security.visibility.filter.VisibilityFilter;
import org.orcid.core.tree.TreeCleaner;
import org.orcid.core.tree.TreeCleaningDecision;
import org.orcid.core.tree.TreeCleaningStrategy;
import org.orcid.jaxb.model.message.Address;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.Country;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.Orcid;
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResults;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.PrivateVisibleToSource;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.VisibilityType;
import org.orcid.jaxb.model.message.WorkContributors;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
 * 
 * @author Declan Newman (declan) Date: 16/03/2012
 */
@Component("visibilityFilter")
public class VisibilityFilterImpl implements VisibilityFilter {

    private final static Logger LOGGER = LoggerFactory.getLogger(VisibilityFilterImpl.class);

    @Resource
    private PermissionChecker permissionChecker;

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
        return filter(messageToBeFiltered, null, false, false, false, visibilities);
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
     * @param source
     *            The orcid source that is executing the request
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
    public OrcidMessage filter(OrcidMessage messageToBeFiltered, final String sourceId, final boolean allowPrivateWorks, final boolean allowPrivateFunding,
            final boolean allowPrivateAffiliations, Visibility... visibilities) {
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
            treeCleaner.clean(messageToBeFiltered, new TreeCleaningStrategy() {
                public TreeCleaningDecision needsStripping(Object obj) {
                    TreeCleaningDecision decision = TreeCleaningDecision.DEFAULT;
                    if (obj != null) {
                        Class<?> clazz = obj.getClass();

                        if (!PojoUtil.isEmpty(sourceId)) {
                            if (allowPrivateAffiliations && Affiliation.class.isAssignableFrom(clazz)) {
                                Affiliation affiliation = (Affiliation) obj;
                                Source source = affiliation.getSource();
                                if (source != null) {
                                    String sourcePath = source.retrieveSourcePath();
                                    if (sourcePath != null) {
                                        if (sourceId.equals(sourcePath)) {
                                            decision = TreeCleaningDecision.IGNORE;
                                        }
                                    }
                                }
                            } else if (allowPrivateFunding && Funding.class.isAssignableFrom(clazz)) {
                                Funding funding = (Funding) obj;
                                Source source = funding.getSource();
                                if (source != null) {
                                    String sourcePath = source.retrieveSourcePath();
                                    if (sourcePath != null) {
                                        if (sourceId.equals(sourcePath)) {
                                            decision = TreeCleaningDecision.IGNORE;
                                        }
                                    }
                                }
                            } else if (allowPrivateWorks && OrcidWork.class.isAssignableFrom(clazz)) {
                                OrcidWork work = (OrcidWork) obj;
                                Source source = work.getSource();
                                if (source != null) {
                                    if (sourceId.equals(source.retrieveSourcePath())) {
                                        decision = TreeCleaningDecision.IGNORE;
                                    }
                                }
                            }
                        }

                        // If it is the address field, the visibility and source
                        // fields are inside the country element
                        if (Address.class.isAssignableFrom(clazz)) {
                            Address address = (Address) obj;
                            // Remove empty addresses
                            if (address.getCountry() == null) {
                                decision = TreeCleaningDecision.CLEANING_REQUIRED;
                            } else {
                                Country country = address.getCountry();
                                // Allow public addresses
                                if (Visibility.PUBLIC.equals(country.getVisibility())) {
                                    decision = TreeCleaningDecision.IGNORE;
                                } else if (visibilitySet.contains(Visibility.LIMITED)) {
                                    // Allow limited visibility when possible
                                    if (Visibility.LIMITED.equals(country.getVisibility())) {
                                        decision = TreeCleaningDecision.IGNORE;
                                    } else {
                                        // As last resource, check the source
                                        Source source = country.getSource();
                                        if (source != null && sourceId != null && sourceId.equals(source.retrieveSourcePath())) {
                                            decision = TreeCleaningDecision.IGNORE;
                                        } else {
                                            decision = TreeCleaningDecision.CLEANING_REQUIRED;
                                        }
                                    }
                                }
                            }
                        }

                        if (Email.class.isAssignableFrom(clazz)) {
                            // check for /email/read-private permissions,
                            // include all emails if present
                            try {
                                Authentication authentication = getAuthentication();
                                if (authentication != null && messageToBeFiltered.getOrcidProfile() != null) {
                                    permissionChecker.checkPermissions(getAuthentication(), ScopePathType.EMAIL_READ_PRIVATE, messageToBeFiltered.getOrcidProfile()
                                            .retrieveOrcidPath());
                                    decision = TreeCleaningDecision.IGNORE;
                                }
                            } catch (AccessControlException e) {
                                // private email can't be read, do nothing here
                            }
                        }

                        // if we have a source, and that source can read
                        // limited, also return the private things they own
                        // Applies to ExternalIdentifier, Keyword,
                        // ResearcherUrl, OtherName and anything in the future
                        // that implements PrivateVisibleToSource
                        if (sourceId != null)
                            if (PrivateVisibleToSource.class.isAssignableFrom(clazz) && visibilitySet.contains(Visibility.LIMITED)) {
                                Source source = ((PrivateVisibleToSource) obj).getSource();
                                if (source != null) {
                                    if (sourceId.equals(source.retrieveSourcePath())) {
                                        decision = TreeCleaningDecision.IGNORE;
                                    }
                                }
                            }

                        if (TreeCleaningDecision.DEFAULT.equals(decision)) {
                            if (WorkContributors.class.isAssignableFrom(clazz)) {
                                decision = TreeCleaningDecision.IGNORE;
                            } else if (VisibilityType.class.isAssignableFrom(clazz)) {
                                VisibilityType visibilityType = (VisibilityType) obj;
                                if ((visibilityType.getVisibility() == null || !visibilitySet.contains(visibilityType.getVisibility()))) {
                                    decision = TreeCleaningDecision.CLEANING_REQUIRED;
                                }
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

    private Authentication getAuthentication() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null && context.getAuthentication() != null) {
            return context.getAuthentication();
        }
        return null;
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
