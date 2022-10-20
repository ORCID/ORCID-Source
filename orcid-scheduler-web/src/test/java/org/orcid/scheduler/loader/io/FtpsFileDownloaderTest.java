package org.orcid.scheduler.loader.io;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPSClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class FtpsFileDownloaderTest {
    
    @Mock
    private FTPSClient client;
    
    @InjectMocks
    private FtpsFileDownloader fileDownloader;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDownloadFileNoPropsSet() {
        fileDownloader.downloadFile();                                                                                                                                  
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDownloadFileNoPassword() {
        fileDownloader.setHost("host");
        fileDownloader.setPort(1);
        fileDownloader.setRemoteFilePath("remote");
        fileDownloader.setLocalFilePath("local");
        fileDownloader.setUsername("username");
        fileDownloader.downloadFile();                                                                                                                                  
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDownloadFileNoUsername() {
        fileDownloader.setHost("host");
        fileDownloader.setPort(1);
        fileDownloader.setRemoteFilePath("remote");
        fileDownloader.setLocalFilePath("local");
        fileDownloader.setPassword("password");
        fileDownloader.downloadFile();                                                                                                                                  
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDownloadFileNoHost() {
        fileDownloader.setPort(1);
        fileDownloader.setRemoteFilePath("remote");
        fileDownloader.setLocalFilePath("local");
        fileDownloader.setUsername("username");
        fileDownloader.setPassword("password");
        fileDownloader.downloadFile();                                                                                                                                  
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDownloadFileNoPort() {
        fileDownloader.setHost("host");
        fileDownloader.setRemoteFilePath("remote");
        fileDownloader.setLocalFilePath("local");
        fileDownloader.setUsername("username");
        fileDownloader.setPassword("password");
        fileDownloader.downloadFile();                                                                                                                                  
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDownloadFileNoLocalFilePath() {
        fileDownloader.setHost("host");
        fileDownloader.setPort(1);
        fileDownloader.setRemoteFilePath("remote");
        fileDownloader.setUsername("username");
        fileDownloader.setPassword("password");
        fileDownloader.downloadFile();                                                                                                                                  
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDownloadFileNoRemoteFilePath() {
        fileDownloader.setHost("host");
        fileDownloader.setPort(1);
        fileDownloader.setLocalFilePath("local");
        fileDownloader.setUsername("username");
        fileDownloader.setPassword("password");
        fileDownloader.downloadFile();                                                                                                                                  
    }
    
    @Test
    public void testDownloadFileErrorOccurs() throws SocketException, IOException {
        fileDownloader.setHost("host");
        fileDownloader.setPort(1);
        fileDownloader.setLocalFilePath("local");
        fileDownloader.setRemoteFilePath("remote");
        fileDownloader.setUsername("username");
        fileDownloader.setPassword("password");
        fileDownloader.downloadFile();        
        
        Mockito.doThrow(new IOException()).when(client).connect(Mockito.eq("host"), Mockito.eq(1));
        assertFalse(fileDownloader.downloadFile());
    }
    
    @Test
    public void testDownloadFileSuccess() throws SocketException, IOException {
        fileDownloader.setHost("host");
        fileDownloader.setPort(1);
        fileDownloader.setLocalFilePath("local");
        fileDownloader.setRemoteFilePath("remote");
        fileDownloader.setUsername("username");
        fileDownloader.setPassword("password");
        fileDownloader.downloadFile();        
        
        Mockito.when(client.retrieveFile(Mockito.eq("remote"), Mockito.any(OutputStream.class))).thenReturn(Boolean.TRUE);
        
        assertTrue(fileDownloader.downloadFile());
    }

}
