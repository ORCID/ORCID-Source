package org.orcid.scheduler.loader.source.issn;

import java.io.IOException;
import java.net.*;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.orcid.core.exception.BannedException;
import org.orcid.core.exception.TooManyRequestsException;
import org.orcid.core.exception.UnexpectedResponseCodeException;
import org.orcid.core.groupIds.issn.IssnClient;
import org.orcid.core.groupIds.issn.IssnData;
import org.orcid.core.groupIds.issn.IssnValidator;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.GroupIdRecordDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.GroupIdRecordEntity;
import org.orcid.utils.alerting.SlackManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IssnLoadSource {

    private static final Logger LOG = LoggerFactory.getLogger(IssnLoadSource.class);

    private static Pattern issnGroupTypePattern = Pattern.compile("^issn:(\\d{4}-\\d{3}[\\dXx])$");

    @Value("${org.orcid.scheduler.issnLoadSource.batchSize:5000}")
    private int batchSize;

    @Value("${org.orcid.scheduler.issnLoadSource.waitBetweenBatches:10000}")
    private int waitBetweenBatches;

    @Value("${org.orcid.scheduler.issnLoadSource.rateLimit.pause:600000}")
    private int pause;

    @Resource
    private GroupIdRecordDao groupIdRecordDao;

    @Resource(name="groupIdRecordDaoReadOnly")
    private GroupIdRecordDao groupIdRecordDaoReadOnly;

    @Resource(name="clientDetailsDaoReadOnly")
    private ClientDetailsDao clientDetailsDaoReadOnly;
    
    private ClientDetailsEntity orcidSource;

    @Resource
    private IssnValidator issnValidator;

    @Resource
    private IssnClient issnClient;

    @Resource
    private SlackManager slackManager;
    
    public void loadIssn(String issnSource) {
        
        if (issnSource == null) {
            throw new RuntimeException("Missing the  ORCID source client details id");
        }
        orcidSource = clientDetailsDaoReadOnly.find(issnSource);
        if (orcidSource == null) {
            throw new RuntimeException("Client details entity not found for id " + issnSource);
        }
        LOG.debug("Loading ISSN for ORCID source client details id: " + issnSource);
        this.updateIssnGroupIdRecords();
    }

    private void updateIssnGroupIdRecords() {
        Date startTime = new Date();
        // Get the first batch of issn's
        LOG.info("Running the process to load ISSN info, starting time: " + startTime + " batch size: " + batchSize);
        List<GroupIdRecordEntity> issnEntities = groupIdRecordDaoReadOnly.getIssnRecordsSortedBySyncDate(batchSize, startTime);
        int batchCount = 0;
        int total = 0;

        while (!issnEntities.isEmpty()) {
            for (GroupIdRecordEntity issnEntity : issnEntities) {
                LOG.info("Processing entity {}", new Object[]{ issnEntity.getId() });
                String issn = getIssn(issnEntity);
                if (issn != null && issnValidator.issnValid(issn)) {
                    batchCount++;
                    total++;
                    try {
                        IssnData issnData = issnClient.getIssnData(issn);
                        if (issnData != null) {
                            updateIssnEntity(issnEntity, issnData);
                            LOG.info("Updated group id record {} - {}, processed count now {}",
                                    new Object[]{issnEntity.getId(), issnEntity.getGroupId(), Integer.toString(total)});
                        }
                    } catch(TooManyRequestsException tmre) {
                        //We are being rate limited, we have to pause for 'pause' minutes
                        LOG.warn("We are being rate limited by the issn portal");
                        recordFailure(issnEntity, "RATE_LIMIT reached");
                        if(pause() != 1) {
                            LOG.warn("Unable to pause, finishing the process");
                            return;
                        }
                    } catch(BannedException be) {
                        LOG.error("We have been banned from the issn portal, the sync process will finish now");
                        try {
                            InetAddress id = InetAddress.getLocalHost();
                            slackManager.sendSystemAlert("We have bee banned from the issn portal on " + id.getHostName());
                        } catch(UnknownHostException uhe) {
                            // Lets try to get the IP address
                            try(final DatagramSocket socket = new DatagramSocket()){
                                socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                                String ip = socket.getLocalAddress().getHostAddress();
                                slackManager.sendSystemAlert("We have bee banned from the issn portal on " + ip);
                            } catch(SocketException | UnknownHostException se) {
                                slackManager.sendSystemAlert("We have bee banned from the issn portal on - Couldn't identify the machine");
                            }
                        }
                        return;
                    } catch(UnexpectedResponseCodeException urce) {
                        LOG.warn("Unexpected response code {} for issn {}", urce.getReceivedCode(), issn);
                        recordFailure(issnEntity, "Unexpected response code " + urce.getReceivedCode());
                    } catch (IOException e) {
                        LOG.warn("IOException for issn {}", issn);
                        recordFailure(issnEntity, "IOException");
                    } catch (URISyntaxException e) {
                        LOG.warn("URISyntaxException for issn {}", issn);
                        recordFailure(issnEntity, "URISyntaxException");
                    } catch (InterruptedException e) {
                        LOG.warn("InterruptedException for issn {}", issn);
                        recordFailure(issnEntity, "InterruptedException");
                    } catch(JSONException e) {
                        LOG.warn("InterruptedException for issn {}", issn);
                        recordFailure(issnEntity, "InterruptedException");
                    }
                } else {
                    LOG.info("Issn for group record {} not valid: {}", issnEntity.getId(), issnEntity.getGroupId());
                    recordFailure(issnEntity, "Invalid record");
                }                
                try {
                    // Lets sleep for 30 secs after processing one batch
                    if(batchCount >= batchSize) {
                        LOG.info("Pausing the process");
                        Thread.sleep(waitBetweenBatches);
                        // Reset the count
                        batchCount = 0;
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    LOG.warn("Exception while pausing the issn loader", e);                    
                }
            }
            LOG.info("Loading next batch of ISSN's");
            issnEntities = groupIdRecordDaoReadOnly.getIssnRecordsSortedBySyncDate(batchSize, startTime);
        }
        LOG.info("All ISSN records processed");
    }

    private void recordFailure(GroupIdRecordEntity issnEntity, String notes) {
        issnEntity.setFailReason(notes);
        issnEntity.setSyncDate(new Date());
        if(issnEntity.getIssnLoaderFailCount() == null) {
            issnEntity.setIssnLoaderFailCount(1);
        } else {
            issnEntity.setIssnLoaderFailCount(issnEntity.getIssnLoaderFailCount() + 1);
        }
        groupIdRecordDao.merge(issnEntity);
    }

    private void updateIssnEntity(GroupIdRecordEntity issnEntity, IssnData issnData) {
        String currentGroupName = issnEntity.getGroupName();
        String updatedGroupName = issnData.getMainTitle();

        // Clear the fail count and reason
        issnEntity.setIssnLoaderFailCount(0);
        issnEntity.setFailReason(null);

        if(!StringUtils.equals(currentGroupName, updatedGroupName)) {
            issnEntity.setGroupName(updatedGroupName);            
            issnEntity.setClientSourceId(orcidSource.getId());
            issnEntity.setSyncDate(new Date());
            LOG.info("Updating Group id: " + issnEntity.getGroupId() +  " | current group name: " + currentGroupName +  " | group name  to be updated: " + issnEntity.getGroupName());
            groupIdRecordDao.merge(issnEntity);
        } else {
            issnEntity.setSyncDate(new Date());
            groupIdRecordDao.merge(issnEntity);
            LOG.debug("Group id: " + issnEntity.getGroupId() + " is up to date");
        }
    }

    private String getIssn(GroupIdRecordEntity issnEntity) {
        Matcher matcher = issnGroupTypePattern.matcher(issnEntity.getGroupId());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private int pause() {
        try {
            LOG.warn("Pause do to rate limit");
            Thread.sleep(pause);
            return 1;
        } catch (InterruptedException e) {
            LOG.warn("Unable to pause", e);
            return -1;
        }
    }

}
