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
package org.orcid.core.cli;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.utils.NullUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FindOrcidWorkDuplicates {

    public static final String TAB = "\t";
    public static final String SEPERATOR = TAB;
    public static final String NEW_LINE = "\n";
    public static final String CARRIAGE_RETURN = "\r";
    public static final String MISSING_ENTRY = "Missing";
    public static final String HEADER = "ORCID"+SEPERATOR+"Definitive/Duplicate"+SEPERATOR+"Put Code"+SEPERATOR+"Title"+SEPERATOR+"Visibility";

    private static final Logger LOG = LoggerFactory.getLogger(FindOrcidWorkDuplicates.class);

    @Option(name = "-f", usage = "Path to write output results file to")
    private String outputFileName;

    @Option(name = "-i", usage = "Path to take input from")
    private String inputFileName;

    @Option(name = "-o", usage = "Standalone orcid identifier")
    private String orcid;

    private OrcidProfileManager orcidProfileManager;
    
    

    private List<String> orcidsToQuery;

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        FindOrcidWorkDuplicates findOrcidWorkDuplicates = new FindOrcidWorkDuplicates();
        CmdLineParser parser = new CmdLineParser(findOrcidWorkDuplicates);
        parser.parseArgument(args);
        findOrcidWorkDuplicates.validateArgs(parser);
        findOrcidWorkDuplicates.createOutputFile();
    }

    private void validateArgs(CmdLineParser parser) throws Exception {
        if (NullUtils.allNull(outputFileName)) {
            throw new CmdLineException(parser, "You must specify a file name");
        }

        // prefer processing single orcid if both are passed, don't accidentally hit DB needlessly
        if (orcid != null) {
            orcidsToQuery = new ArrayList<String>(Arrays.asList(new String[] { orcid }));
        }

        else if (inputFileName != null) {
            orcidsToQuery = FileUtils.readLines(new File(inputFileName));
        }

        else {
            throw new CmdLineException(parser, "You must specify either a single orcid or provide an input name");
        }

    }

    private void createOutputFile() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        orcidProfileManager = (OrcidProfileManager) context.getBean("orcidProfileManager");
        
        LOG.info(MessageFormat.format("Started building file {0} at {1}", new Object[]{outputFileName,new Date()}));
        File outputFile =  new File(outputFileName);
        FileUtils.writeStringToFile(outputFile, "\n"+HEADER+"\n",true);
       
        int counter =0;
        for (String orcidIdentifier : orcidsToQuery) {
            try {
                StringBuilder records = new StringBuilder();           
                OrcidProfile orcidProfileWorksOnly = orcidProfileManager.retrieveClaimedOrcidWorks(orcidIdentifier);
                // is there are less than 2 works there obv can't be duplicates
                if (!multipleWorks(orcidProfileWorksOnly))
                    continue;

                List<OrcidWorkDeduped> dedupedWorks =
                        dedupeWorksForOrcid(orcidProfileWorksOnly.getOrcidActivities().getOrcidWorks());

                if (dedupedWorks != null) {
                    LOG.debug("Found orcid with duplicate works: " + orcidIdentifier);
                   records.append(buildDuplicationString(dedupedWorks, orcidIdentifier));
                }
                
                
                FileUtils.writeStringToFile(outputFile, records.toString(),true);
                records.delete(0, records.length());
            }

            catch (Exception e) {
                LOG.error("exception processing ORCID: " + orcidIdentifier, e);
            }
            
            LOG.debug("iteration: "+counter++);
            
        }  
      
        // create file with tab seperated headers..
        LOG.info(MessageFormat.format("Finished building file {0} at {1}", new Object[]{outputFileName,new Date()}));
    }

    private List<OrcidWorkDeduped> dedupeWorksForOrcid(OrcidWorks orcidWorks) {
        
        
        Map<OrcidWorkMatcher, List<OrcidWork>> worksSplitByDuplicates = splitWorksIntoDuplicateSets(orcidWorks);
        
        List<OrcidWorkDeduped> orcidWorkDupes = new ArrayList<FindOrcidWorkDuplicates.OrcidWorkDeduped>();
        

        for (Map.Entry<OrcidWorkMatcher, List<OrcidWork>> entry : worksSplitByDuplicates.entrySet()) {

            List<OrcidWork> allOrcidWorks = entry.getValue();

            //there may have been more than one work on a profile, but may not be duplicates
            if (allOrcidWorks.size() < 2) {
                continue;
            }          

            // sort by desc put code in case we cant rely on visibility
            Collections.sort(allOrcidWorks, new Comparator<OrcidWork>() {

                public int compare(OrcidWork work1, OrcidWork work2) {
                    return Integer.valueOf(work2.getPutCode()).compareTo(Integer.valueOf(work1.getPutCode()));
                }
            });

            // yes
            // add to string
            // determine which is the dupe and which the definitive XML

            OrcidWork definitiveWork = null;
            OrcidWork definitivePublicWork = null;
            OrcidWork definitiveLimitedWork = null;

            // if there are varying visibilities then the definitive is the must public level of visibility
            for (OrcidWork orcidWork : allOrcidWorks) {
                if (Visibility.PUBLIC.equals(orcidWork.getVisibility())) {
                    definitivePublicWork = orcidWork;
                    break;
                }

                // keep looping around in case we find a public work, but don't override the most recent limited work
                // once set
                else if (Visibility.LIMITED.equals(orcidWork.getVisibility()) && definitiveLimitedWork == null) {
                    definitiveLimitedWork = orcidWork;
                }

            }

            // fallback onto limited work and if nothing else the max put code
            definitiveWork = definitivePublicWork != null ? definitivePublicWork : definitiveLimitedWork;
            // if they all match the definitive is the most recent date
            definitiveWork = definitiveWork != null ? definitiveWork : allOrcidWorks.get(0);
            allOrcidWorks.remove(definitiveWork);
            
            orcidWorkDupes.add(new OrcidWorkDeduped(definitiveWork, allOrcidWorks) );
        }

        return orcidWorkDupes;
    }

    private StringBuffer buildDuplicationString(List<OrcidWorkDeduped> dedupedWorks, String orcid) {        
        
        StringBuffer allDupes = new StringBuffer();
        for (OrcidWorkDeduped dedupedWork : dedupedWorks){
            allDupes.append(deriveOrcidData(orcid, true, Arrays.asList(new OrcidWork[]{dedupedWork.getDefinitive()})));
            allDupes.append(deriveOrcidData(orcid, false,dedupedWork.getDupes()));
        }
             
        return allDupes;
    }
    
    private StringBuffer deriveOrcidData(String orcid,boolean definitive, List<OrcidWork> orcidWorks)
    {
        
        StringBuffer duplicationString = new StringBuffer();
        String definitiveIdentifier = definitive ? "Definitive" : "Duplicate";
      
        for (OrcidWork duplicate : orcidWorks)
        {
            String putCode = duplicate.getPutCode();
            String title = duplicate.getWorkTitle()!=null &&  duplicate.getWorkTitle().getTitle()!=null && 
                    StringUtils.isNotBlank(duplicate.getWorkTitle().getTitle().getContent()) ? duplicate.getWorkTitle().getTitle().getContent() : MISSING_ENTRY;                    
            
        String visibility = duplicate.getVisibility()!=null ? duplicate.getVisibility().value() : MISSING_ENTRY;
        duplicationString.append(orcid).append(SEPERATOR);
        duplicationString.append(definitiveIdentifier).append(SEPERATOR);
        duplicationString.append(putCode).append(SEPERATOR);
        duplicationString.append(title).append(SEPERATOR);             
        duplicationString.append(visibility).append(SEPERATOR);
        duplicationString.append(NEW_LINE);
        
        }
        return duplicationString;
    }

    private Map<OrcidWorkMatcher, List<OrcidWork>> splitWorksIntoDuplicateSets(OrcidWorks orcidWorks) {
        // do any works match, bar the put code and visibility
        Map<OrcidWorkMatcher, List<OrcidWork>> orcidWorksAsDupes = new HashMap<OrcidWorkMatcher, List<OrcidWork>>();

        // for each work associated with a profile
        for (OrcidWork orcidWork : orcidWorks.getOrcidWork()) {

            OrcidWorkMatcher orcidMatcherKey = new OrcidWorkMatcher(orcidWork);

            // does anything exist in the map for that key
            if (orcidWorksAsDupes.containsKey(orcidMatcherKey)) {
                // if so get the map and add
                List<OrcidWork> existingDupesForWork = orcidWorksAsDupes.get(orcidMatcherKey);
                existingDupesForWork.add(orcidWork);
                orcidWorksAsDupes.put(orcidMatcherKey, existingDupesForWork);
            }

            else {
                // if not build a new list - may be adding duplicates for this work
                List<OrcidWork> orcidWorksForKey = new ArrayList<OrcidWork>();
                orcidWorksForKey.add(orcidWork);
                orcidWorksAsDupes.put(orcidMatcherKey, orcidWorksForKey);
            }
        }

        return orcidWorksAsDupes;
    }

    private boolean multipleWorks(OrcidProfile orcidProfile) {
        return orcidProfile != null && orcidProfile.getOrcidActivities() != null
                && orcidProfile.getOrcidActivities().getOrcidWorks() != null
                && orcidProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().size() > 1;
    }

    private class OrcidWorkDeduped {

        private OrcidWork definitive;
        private List<OrcidWork> dupes;

        public OrcidWorkDeduped(OrcidWork definitive, List<OrcidWork> dupes) {
            super();
            this.definitive = definitive;
            this.dupes = dupes;
        }

        public OrcidWork getDefinitive() {
            return definitive;
        }

        public List<OrcidWork> getDupes() {
            return dupes;
        }

    }

}

