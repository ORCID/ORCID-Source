package org.orcid.core.orgs.load.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FtpsFileDownloader {

    private static final Logger LOGGER = LoggerFactory.getLogger(FtpsFileDownloader.class);

    @Value("${org.orcid.core.orgs.ringgold.ftpHost:host}")
    private String host;

    @Value("${org.orcid.core.orgs.ringgold.fptPort:990}")
    private int port;

    @Value("${org.orcid.core.orgs.ringgold.fptUsername:username}")
    private String username;

    @Value("${org.orcid.core.orgs.ringgold.fptPassword:password}")
    private String password;

    @Value("${org.orcid.core.orgs.ringgold.ftpRemoteFilePath:/tmp/ringgold/ringold-import.zip}")
    private String remoteFilePath;

    @Value("${org.orcid.core.orgs.ringgold.fptLocalFilePath:/remote/filepath}")
    private String localFilePath;

    private FTPSClient client;

    public FtpsFileDownloader() {
        client = new FTPSClient("TLS", true);

        // if (debug) {
        // client.addProtocolCommandListener(new
        // PrintCommandListener(System.out));
        // }
    }

    public boolean downloadFile() {
        validateProps();

        OutputStream outputStream = null;
        try {
            LOGGER.info("Connecting to host {}, port {}...", host, port);
            client.connect(host, port);
            LOGGER.info("Connected");

            client.enterLocalPassiveMode();

            LOGGER.info("Logging in with user {}...", username);
            client.login(username, password);
            LOGGER.info("Logged in successfully");
            
            client.setFileType(FTP.BINARY_FILE_TYPE);

            LOGGER.info("Retrieving file {}", remoteFilePath);
            outputStream = new FileOutputStream(new File(localFilePath));
            boolean copied = client.retrieveFile(remoteFilePath, outputStream);

            if (copied) {
                LOGGER.info("File written successfully to  {}", localFilePath);
                return true;
            } else {
                LOGGER.warn("FTP download failed");
                return false;
            }
        } catch (IOException e) {
            LOGGER.error("Error downloading file from FTP server", e);
            logConfiguration();
        } finally {
            try {
                client.logout();
                client.disconnect();

                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException e) {
                LOGGER.error("Error cleaning up FTP download", e);
                logConfiguration();
            }

        }
        return false;
    }

    private void validateProps() {
        if (host == null || host.isEmpty()) {
            throw new IllegalArgumentException("Host must be set");
        }

        if (port == 0) {
            throw new IllegalArgumentException("Port must be set");
        }

        if (remoteFilePath == null || remoteFilePath.isEmpty()) {
            throw new IllegalArgumentException("RemoteFilePath must be set");
        }

        if (localFilePath == null || localFilePath.isEmpty()) {
            throw new IllegalArgumentException("LocalFilePath must be set");
        }

        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username must be set");
        }

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password must be set");
        }
    }

    private void logConfiguration() {
        LOGGER.warn("FTP configuration:\n{}\n{}\n{}\n<password>\nremote - {}\nlocal - {}", new Object[] { host, port, username, remoteFilePath, localFilePath });
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRemoteFilePath() {
        return remoteFilePath;
    }

    public void setRemoteFilePath(String remoteFilePath) {
        this.remoteFilePath = remoteFilePath;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

}
