package org.library.utils;

import lombok.extern.slf4j.Slf4j;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class FileVersion {

    /**
     * Method for extracting migration and undo file versions
     * @param fileName Name of .sql file
     * @return .sql file's version
     */
    public static String extractVersionFromFileName(String fileName) {
        log.info("Trying getting version from  file {}",fileName);
        Pattern pattern = Pattern.compile("^[UV](.+)__.+.sql$");
        Matcher matcher = pattern.matcher(fileName);
        if (matcher.find()) {
            return matcher.group(1);
        }
        log.error("Something wrong with file {}",fileName);
        throw new RuntimeException();
    }
}
