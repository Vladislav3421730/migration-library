package org.library.dto;

import lombok.*;

import java.sql.Timestamp;

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
