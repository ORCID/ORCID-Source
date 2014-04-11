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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Date;
import java.util.UUID;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;
import org.orcid.utils.DateUtils;
import org.orcid.utils.NullUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Will Simpson
 * 
 */
public class CreateNewClientSecrets {

    private Date now = new Date();

    private String dateString = DateUtils.convertToXMLGregorianCalendar(now).toXMLFormat();

    @Option(name = "-c", usage = "Client details id for which to create new secret")
    private String clientDetailsId;

    @Option(name = "-a", usage = "Create new client secret for all clients")
    private Boolean doAll;

    @Option(name = "-f", usage = "File from which to read client ids to create new secrets for (one per line)")
    private File clientIdsFile;

    @Option(name = "-o", usage = "File to write the results to (default is client_secrets_DATETIME)")
    private File outputFile = new File("client_secrets_" + dateString);

    private BufferedWriter outputWriter;

    private EncryptionManager encryptionManager;

    private ClientDetailsDao clientDetailsDao;

    private TransactionTemplate transactionTemplate;

    public static void main(String[] args) {
        CreateNewClientSecrets createNewClientSecrets = new CreateNewClientSecrets();
        CmdLineParser parser = new CmdLineParser(createNewClientSecrets);
        try {
            parser.parseArgument(args);
            createNewClientSecrets.validateArgs(parser);
            createNewClientSecrets.execute();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        } finally {
            System.exit(0);
        }
    }

    private void validateArgs(CmdLineParser parser) throws CmdLineException {
        if (NullUtils.allNull(clientDetailsId, doAll, clientIdsFile)) {
            throw new CmdLineException(parser, "At least one of -c | -a | -f must be specificed");
        }
    }

    public void execute() {
        init();
        openOutputFileAndCreateNewSecrets();
        finish();
    }

    private void openOutputFileAndCreateNewSecrets() {
        try (FileWriter fr = new FileWriter(outputFile); BufferedWriter br = new BufferedWriter(fr)) {
            outputWriter = br;
            createNewSecrets();
        } catch (IOException e) {
            throw new RuntimeException("Problem opening output file " + outputFile.getAbsolutePath(), e);
        }
    }

    private void createNewSecrets() {
        if (Boolean.TRUE.equals(doAll)) {
            createForAll();
        } else {
            if (clientDetailsId != null) {
                createForOne(clientDetailsId);
            } else if (clientIdsFile != null) {
                createFromFile();
            }
        }

    }

    private void createForOne(final String clientDetailsId) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                ClientDetailsEntity clientDetails = clientDetailsDao.find(clientDetailsId);
                createNewClientSecret(clientDetails);
            }
        });

    }

    private void createForAll() {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                for (ClientDetailsEntity clientDetails : clientDetailsDao.getAll()) {
                    createNewClientSecret(clientDetails);
                }
            }
        });
    }

    private void createFromFile() {
        try (Reader fr = new FileReader(clientIdsFile); BufferedReader br = new BufferedReader(fr);) {
            String line = null;
            while ((line = br.readLine()) != null) {
                String clientDetailsId = line.trim();
                createForOne(clientDetailsId);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Client IDs file " + clientIdsFile.getAbsolutePath() + " not found", e);
        } catch (IOException e) {
            throw new RuntimeException("Error reading client IDs file " + clientIdsFile.getAbsolutePath() + " not found", e);
        }

    }

    private void createNewClientSecret(ClientDetailsEntity clientDetails) {
        String clientSecret = UUID.randomUUID().toString();
        clientDetails.getClientSecrets().add(new ClientSecretEntity(encryptionManager.encryptForInternalUse(clientSecret), clientDetails));
        clientDetails.setLastModified(now);
        clientDetailsDao.merge(clientDetails);
        String output = String.format("%s\t%s\t%s\n", clientDetails.getId(), clientDetails.getClientName(), clientSecret);
        output(output);
    }

    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        encryptionManager = (EncryptionManager) context.getBean("encryptionManager");
        clientDetailsDao = (ClientDetailsDao) context.getBean("clientDetailsDao");
        transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
    }

    private void output(String output) {
        System.out.print(output);
        try {
            outputWriter.append(output);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to output file", e);
        }
    }

    private void finish() {
        System.out.println(">>>>>>>> Results output to " + outputFile.getAbsolutePath());
    }

}
