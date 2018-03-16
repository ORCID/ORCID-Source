package org.orcid.core.cli.logs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogReader.class);

    private BufferedReader reader;

    private List<File> fileQueue = new ArrayList<>();

    public void init(File logsDir) {
        queueDir(logsDir);
        File nextFile = getNextFile();
        if (nextFile != null) {
            initReaderWithFile(nextFile);
        }
    }

    private void initReaderWithFile(File file) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException("Couldn't close buffered reader", e);
            }
        }
        LOGGER.info("Initialising reader with {}", file.getAbsolutePath());
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void queueDir(File dir) {
        LOGGER.info("Queueing directory {}", dir.getAbsolutePath());
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                queueDir(file);
            } else {
                LOGGER.info("Queueing file {}", file.getAbsolutePath());
                fileQueue.add(file);
            }
        }
    }

    public String getNextLine() {
        if (reader != null) {
            try {
                String line = reader.readLine();
                while (line == null) {
                    File nextFile = getNextFile();
                    if (nextFile == null) {
                        reader.close();
                        return null;
                    } else {
                        initReaderWithFile(nextFile);
                    }
                    line = reader.readLine();
                }
                return line;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }

    private File getNextFile() {
        if (fileQueue.size() > 0) {
            return fileQueue.remove(0);
        } else {
            return null;
        }
    }

}
