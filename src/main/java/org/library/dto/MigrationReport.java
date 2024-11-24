package org.library.dto;

import lombok.*;

import java.sql.Timestamp;

/**
 * Simple DTO for creating reports and getting information from migration_history table
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class MigrationReport {

    private String version;
    private String script_name;
    private Timestamp executed_at;
    private String status;


}
