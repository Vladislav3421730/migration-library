package org.migration.migration;

import org.migration.connection.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.sql.SQLException;

public class MigrationExecutor {

    private static final MigrationExecutor INSTANCE=new MigrationExecutor();
    public static MigrationExecutor getInstance() {
        return INSTANCE;
    }
    private MigrationExecutor(){}

    private static final Logger logger = LoggerFactory.getLogger(MigrationExecutor.class);


    public void executedSqlScript(String sqlScript){

        ConnectionManager.connect();

        try (var connection= ConnectionManager.getConnection()){
             var statement=connection.createStatement();
             logger.info("Trying execute query {}",sqlScript);
             statement.execute(sqlScript);

             logger.info("Script successfully executed {}",sqlScript);

        } catch (SQLException e) {
            logger.error("Something wrong with query {}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
