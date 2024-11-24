package org.library.migration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static org.library.utils.RollbackUtils.rollbackToPreviousVersion;
import static org.library.utils.RollbackUtils.rollbackToSomeVersion;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MigrationRollBackManager {

    private final static MigrationRollBackManager INSTANCE=new MigrationRollBackManager();
    public static MigrationRollBackManager getInstance(){
        return INSTANCE;
    }

    private final MigrationFileReader migrationFileReader  = MigrationFileReader.getInstance();

    /**
     * rollback to previous version by undo files in resources
     */
    public void rollback() {
        log.info("Trying to get all undo files from resources");
        rollbackToPreviousVersion(migrationFileReader.readFilesFromResources('U'));
    }

    /**
     * rollback to chosen version by undo files in resources
     * @param version The version that the user enters
     */
    public void rollbackToVersionByResources(String version){
        log.info("Trying to get all undo files from resources and roll back to version {}",version);
        rollbackToSomeVersion(migrationFileReader.readFilesFromResources('U'),version);
    }

    /**
     * rollback to previous version by undo files in chosen path
     * @param path path, which contains undo files
     */
    public void rollback(String path){
        log.info("Trying to get all undo files from external directory {}",path);
        rollbackToPreviousVersion(migrationFileReader.readFilesFromExternalDirectory(path,'U'));
    }

    /**
     * rollback to chosen version by undo files in chosen path
     * @param path path, which contains undo files
     * @param version The version that the user enters
     */
    public void rollback(String path,String version){
        log.info("Trying to get all undo files from resources and roll back to version {}",version);
        rollbackToSomeVersion(migrationFileReader.readFilesFromExternalDirectory(path,'U'),version);
    }

}
