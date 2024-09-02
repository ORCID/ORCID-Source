package org.orcid.scheduler.autospam.cli;

import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.admin.LockReason;
import org.orcid.core.manager.ProfileEntityCacheManager;

import org.orcid.core.manager.v3.BiographyManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.ResearcherUrlManager;
import org.orcid.core.togglz.OrcidTogglzConfiguration;
import org.orcid.jaxb.model.v3.release.record.Biography;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.scheduler.autospam.AutospamEmailSender;
import org.orcid.utils.OrcidStringUtils;
import org.orcid.utils.alerting.SlackManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.togglz.core.context.ContextClassLoaderFeatureManagerProvider;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.manager.FeatureManagerBuilder;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;

@Service
public class AutoLockSpamRecords {

    private static final Logger LOG = LoggerFactory.getLogger(AutoLockSpamRecords.class);

    @Resource
    private SlackManager slackManager;

    @Value("${org.orcid.core.autospam.slackChannel:collab-spam-reports}")
    private String slackChannel;
    
    @Value("${org.orcid.core.autospam.webhookUrl}")
    private String webhookUrl;

    @Value("${org.orcid.core.orgs.load.slackUser}")
    private String slackUser;

    @Value("${org.orcid.message-listener.s3.accessKey}")
    private String S3_ACCESS_KEY;

    @Value("${org.orcid.message-listener.s3.secretKey}")
    private String S3_SECRET_KEY;

    @Value("${org.orcid.scheduler.aws.bucket:auto-spam-folder}")
    private String SPAM_BUCKET;

    @Value("${org.orcid.scheduler.aws.file:orcidspam.csv}")
    private String ORCID_S3_SPAM_FILE;

    @Value("${org.orcid.scheduler.autospam.enabled:false}")
    private boolean AUTOSPAM_ENABLED;

    @Value("${org.orcid.scheduler.autospam.file:orcidspam.csv}")
    private String ORCID_SPAM_FILE;

    @Value("${org.orcid.scheduler.autospam.daily.batch:20000}")
    private int DAILY_BATCH_SIZE;

    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;

    @Resource(name = "orcidOauth2TokenDetailDao")
    private OrcidOauth2TokenDetailDao orcidOauthDao;

    private static int ONE_DAY = 86400000;

    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private AutospamEmailSender autospamEmailSender;
    
   
    @Resource(name = "biographyManagerV3")
    private BiographyManager biographyManager;
    
    @Resource(name = "researcherUrlManagerV3")
    private ResearcherUrlManager researcherUrlManager;
    
   

    // for running spam manually
    public static void main(String[] args) {
        AutoLockSpamRecords autolockSpamRecords = new AutoLockSpamRecords();
        try {
            autolockSpamRecords.init();
            autolockSpamRecords.process(false);
        } catch (Exception e) {
            LOG.error("Exception when locking spam records", e);
            System.err.println(e.getMessage());
        } finally {
            System.exit(0);
        }

    }

    private void autolockRecords(List<String> toLock) {
        String lastOrcidProcessed = "";
        slackManager.sendAlert("Start time for batch: " + System.currentTimeMillis() + " the batch size is: " + toLock.size(), slackChannel, slackUser, webhookUrl);
        System.out.println("Start for batch: " + System.currentTimeMillis() + " to lock batch is: " + toLock.size());
        int accountsLocked = 0;
        for (String orcidId : toLock) {
            try {
                LOG.info("Processing orcidId: " + orcidId);
                if (OrcidStringUtils.isValidOrcid(orcidId)) {
                    ProfileEntity profileEntity = profileEntityManager.findByOrcid(orcidId);
                    // only lock account was not reviewed and not already locked
                    // and not have an auth token

                    if (profileEntity != null && !profileEntity.isReviewed() && profileEntity.isAccountNonLocked() && !orcidOauthDao.hasToken(orcidId)) {
                        //check if it has biography
                    	Biography bio= biographyManager.getBiography(orcidId);
                    	ResearcherUrls researcherUrls = researcherUrlManager.getResearcherUrls(orcidId);
                  	
                    	if(( bio !=null && StringUtils.isNotBlank(bio.getContent()) || (researcherUrls != null && researcherUrls.getResearcherUrls() != null && researcherUrls.getResearcherUrls().size() > 0))){
                    		boolean wasLocked = profileEntityManager.lockProfile(orcidId, LockReason.SPAM_AUTO.getLabel(), "ML Detected", "");
                            if (wasLocked) {
                                autospamEmailSender.sendOrcidLockedEmail(orcidId);
                                accountsLocked++;
                            }
                    	}
                    }
                    lastOrcidProcessed = orcidId;
                }
            } catch (Exception e) {
                LOG.error("Exception when locking spam record " + orcidId, e);
                LOG.info("LastOrcid processed is: " + lastOrcidProcessed);
                e.printStackTrace();
            }
        }
        System.out.println("Spam locking for the batch processed on the day: " + System.currentTimeMillis() + " lastOrcid processed is: " + lastOrcidProcessed
                + " acccounts locked in DB: " + accountsLocked);
        LOG.info("Spam locking for the batch processed on the day: " + System.currentTimeMillis() + " lastOrcid processed is: " + lastOrcidProcessed
                + " acccounts locked in DB: " + accountsLocked);
        slackManager.sendAlert(
                "Spam locking for the batch processed on the day ended. LastOrcid processed is: " + lastOrcidProcessed + " acccounts locked in DB: " + accountsLocked,
                slackChannel, slackUser, webhookUrl);
    }

    public void scheduledProcess() throws InterruptedException, IOException {
        if (AUTOSPAM_ENABLED) {
            process(true);
        }
    }

    public void process(boolean fromS3) throws InterruptedException, IOException {
        List<String> allIDs = getAllSpamIDs(fromS3);
        System.out.println("Found " + allIDs.size() + " profiles for autolocking. Starting the autolocking process");
        slackManager.sendAlert("Found " + allIDs.size() + " profiles for autolocking.", slackChannel, slackUser, webhookUrl);
        LOG.info("Found {} profiles for autolocking", allIDs.size());

        List<String> toLock = getNextIdSubset(allIDs);
        while (toLock != null && !toLock.isEmpty()) {
            autolockRecords(toLock);
            LOG.info("Locked {} profiles, {} remaining to lock", new Object[] { toLock.size(), allIDs.size() });
            LOG.info("Profiles autolocked");
            Thread.sleep(ONE_DAY);
            if (allIDs.size() - toLock.size() < 0) {
                break;
            } else {
                toLock = getNextIdSubset(allIDs);
            }
        }
    }

    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-scheduler-context.xml");
        profileEntityManager = (ProfileEntityManager) context.getBean("profileEntityManagerV3");
        profileEntityCacheManager = (ProfileEntityCacheManager) context.getBean("profileEntityCacheManager");
        notificationManager = (NotificationManager) context.getBean("notificationManagerV3");
        autospamEmailSender = (AutospamEmailSender) context.getBean("autospamEmailSender");
        orcidOauthDao = (OrcidOauth2TokenDetailDao) context.getBean("orcidOauth2TokenDetailDao");
        biographyManager = (BiographyManager) context.getBean("biographyManagerV3");
        researcherUrlManager = (ResearcherUrlManager) context.getBean("researcherUrlManagerV3");
        bootstrapTogglz(context.getBean(OrcidTogglzConfiguration.class));
    }

    private List<String> getNextIdSubset(List<String> ids) {
        List<String> subset = new ArrayList<>();
        for (int i = 0; i < DAILY_BATCH_SIZE && !ids.isEmpty(); i++) {
            subset.add(ids.remove(0));
        }
        return subset;
    }

    private ArrayList<String> getAllSpamIDs(boolean fromS3) throws IOException {
        Reader reader;
        if (fromS3) {
            BasicAWSCredentials creds = new BasicAWSCredentials(S3_ACCESS_KEY, S3_SECRET_KEY);
            AmazonS3 s3 = AmazonS3Client.builder().withRegion(Regions.US_EAST_2).withCredentials(new AWSStaticCredentialsProvider(creds)).build();

            S3Object response = s3.getObject(new GetObjectRequest(SPAM_BUCKET, ORCID_S3_SPAM_FILE));
            byte[] byteArray = IOUtils.toByteArray(response.getObjectContent());
            reader = new InputStreamReader(new ByteArrayInputStream(byteArray));

        } else {
            reader = new FileReader(ORCID_SPAM_FILE);
        }

        Iterator<Map<String, String>> iterator = new CsvMapper().readerFor(Map.class)
                .with(CsvSchema.emptySchema().withHeader().withColumnSeparator(',').withoutQuoteChar()).readValues(reader);
        ArrayList<String> spamList = new ArrayList<String>();
        Map<String, String> keyVals = null;
        while (iterator.hasNext()) {
            keyVals = iterator.next();
            Object[] keys = keyVals.keySet().toArray();
            spamList.add(keyVals.get(keys[0]));
        }
        return spamList;
    }

    private static void bootstrapTogglz(OrcidTogglzConfiguration togglzConfig) {
        FeatureManager featureManager = new FeatureManagerBuilder().togglzConfig(togglzConfig).build();
        ContextClassLoaderFeatureManagerProvider.bind(featureManager);
    }

}
