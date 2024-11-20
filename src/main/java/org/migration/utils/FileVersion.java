package org.migration.utils;

import org.migration.migration.MigrationTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileVersion {

    private static final Logger logger = LoggerFactory.getLogger(MigrationTool.class);

    public static int extractVersionFromFileName(String fileName) {

        logger.info("Trying getting version from  file {}",fileName);
        Pattern pattern = Pattern.compile("V(\\d+)__");
        Matcher matcher = pattern.matcher(fileName);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        logger.error("Something wrong with file {}",fileName);
        throw new RuntimeException();
    }
}
