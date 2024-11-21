package org.migration.utils;

import org.migration.migration.MigrationExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileVersion {

    private static final Logger logger = LoggerFactory.getLogger(MigrationExecutor.class);

    public static String extractVersionFromFileName(String fileName) {

        logger.info("Trying getting version from  file {}",fileName);
        Pattern pattern = Pattern.compile("^[UV](.+)__.+.sql$");
        Matcher matcher = pattern.matcher(fileName);
        if (matcher.find()) {
            return matcher.group(1);
        }
        logger.error("Something wrong with file {}",fileName);
        throw new RuntimeException();
    }
}
