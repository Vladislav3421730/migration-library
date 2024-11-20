package org.migration.migration;

import org.flywaydb.core.Flyway;
import org.migration.utils.PropertiesUtils;


public class MigrationManager {

    private final static Flyway flyway = Flyway.configure()
            .dataSource(PropertiesUtils.getJdbcUrl(),PropertiesUtils.getUsername(),PropertiesUtils.getPassword())
            .baselineOnMigrate(true)
            .load();


}
