package org.orcid.core.profileEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author rcpeters
 * 
 */
public class ProfileEventManager {

	static ApplicationContext context;

	@Resource
	private ProfileDao profileDao;

	@Resource
	private OrcidProfileManager orcidProfileManager;

	@Resource
	private GenericDao<ProfileEventEntity, Long> profileEventDao;

	@Resource
	private TransactionTemplate transactionTemplate;

	private static Logger LOG = LoggerFactory.getLogger(ProfileEventManager.class);

	private static final int CHUNK_SIZE = 1000;

	@Option(name = "-testSendToOrcids", usage = "Call only on passed ORCID Ids")
	private String orcs;

	@Option(name = "-callOnAll", usage = "Calls on all orcids")
	private String callOnAll;

	@Option(name = "-bean", usage = "ProfileEvent class to instantiate", required = true)
	private String bean;

	ExecutorService pool = Executors.newFixedThreadPool(4);

	public static void main(String... args) {
		context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
		ProfileEventManager pem = (ProfileEventManager) context.getBean("profileEventManager");

		CmdLineParser parser = new CmdLineParser(pem);
		if (args == null) {
			parser.printUsage(System.err);
		}
		try {
			parser.parseArgument(args);
			pem.execute(pem);
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			parser.printUsage(System.err);
		}
		System.exit(0);
	}

	private void execute(ProfileEventManager pem) {
		if (callOnAll != null) {
			try {
				callOnceOnAll(bean);
			} catch (InterruptedException e) {
				LOG.error("InterruptedException ", e);
			}
		} else if (orcs != null) {
			for (String orc : orcs.split(" ")) {
				OrcidProfile orcidProfile = getOrcidProfileManager().retrieveOrcidProfile(orc);
				ProfileEvent pe = (ProfileEvent) context.getBean(bean, orcidProfile);
				try {
					pe.call();
				} catch (Exception e) {
					LOG.error("Error calling ", e);
				}
			}
		}
	}

	private void callOnceOnAll(final String classStr) throws InterruptedException {
		ProfileEvent dummyPe = (ProfileEvent) context.getBean(classStr, (ProfileEvent) null);
		long startTime = System.currentTimeMillis();
		@SuppressWarnings("unchecked")
		List<String> orcids = Collections.EMPTY_LIST;
		int doneCount = 0;
		do {
			orcids = getProfileDao().findByMissingEventTypes(CHUNK_SIZE, dummyPe.outcomes(), null, true);
			Set<ProfileEvent> callables = new HashSet<ProfileEvent>();
			for (final String orcid : orcids) {
				LOG.info("Calling bean " + classStr + " for " + orcid);
				// TODO: parameterize load options.
				OrcidProfile orcidProfile = getOrcidProfileManager().retrieveOrcidProfile(orcid,new LoadOptions(true,false,true));
				callables.add((ProfileEvent) context.getBean(classStr, orcidProfile));
				doneCount++;
			}
			List<Future<ProfileEventResult>> futures = pool.invokeAll(callables);
			for (Future<ProfileEventResult> future : futures) {
				ProfileEventResult per = null;
				try {
					per = future.get();
				} catch (ExecutionException e) {
					LOG.error("failed calling task ", e);
				}
				getProfileEventDao().persist(new ProfileEventEntity(per.getOrcidId(), per.getOutcome()));
			}
			LOG.info("Current done count: {}", doneCount);
		} while (!orcids.isEmpty());
		long endTime = System.currentTimeMillis();
		String timeTaken = DurationFormatUtils.formatDurationHMS(endTime - startTime);
		LOG.info("Profile Event " + classStr + ": doneCount={}, timeTaken={} (H:m:s.S)", doneCount, timeTaken);
	}

	public ProfileDao getProfileDao() {
		return profileDao;
	}

	public void setProfileDao(ProfileDao profileDao) {
		this.profileDao = profileDao;
	}

	public OrcidProfileManager getOrcidProfileManager() {
		return orcidProfileManager;
	}

	public void setOrcidProfileManager(OrcidProfileManager orcidProfileManager) {
		this.orcidProfileManager = orcidProfileManager;
	}

	public GenericDao<ProfileEventEntity, Long> getProfileEventDao() {
		return profileEventDao;
	}

	public void setProfileEventDao(GenericDao<ProfileEventEntity, Long> profileEventDao) {
		this.profileEventDao = profileEventDao;
	}

}
