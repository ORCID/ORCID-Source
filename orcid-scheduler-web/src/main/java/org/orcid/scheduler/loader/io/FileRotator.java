package org.orcid.scheduler.loader.io;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Class for handling all file rotation logic for downloaded org data files.
 * 
 * Different org sources may want different behaviour.
 * 
 * Simplest approach is to remove existing file before downloading new data.
 * 
 * 
 * @author georgenash
 *
 */

@Component
public class FileRotator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FileRotator.class);
    
    public void removeFileIfExists(String path) {
        File file = new File(path);
        if (file.exists()) {
            LOGGER.info("Removing file {} ", path);
            boolean deleted = file.delete();
            if (!deleted) {
                LOGGER.error("Failed to remove file {}", path);
                throw new RuntimeException("Couldn't delete org data file");
            }
        }
    }    

}
