package org.migration.dto;

import java.sql.Timestamp;

public class MigrationReport {

    private int id;
    private String version;
    private String script_name;
    private Timestamp executed_at;
    private String status;

    public MigrationReport(int id, String version, String script_name, Timestamp executed_at, String status) {
        this.id = id;
        this.version = version;
        this.script_name = script_name;
        this.executed_at = executed_at;
        this.status = status;
    }

    public MigrationReport() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getScript_name() {
        return script_name;
    }

    public void setScript_name(String script_name) {
        this.script_name = script_name;
    }

    public Timestamp getExecuted_at() {
        return executed_at;
    }

    public void setExecuted_at(Timestamp executed_at) {
        this.executed_at = executed_at;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
