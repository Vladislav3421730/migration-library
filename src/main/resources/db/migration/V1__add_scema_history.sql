CREATE TABLE schema_history
(
    id                SERIAL PRIMARY KEY,
    version           VARCHAR(50)  NOT NULL,
    script_name       VARCHAR(255) NOT NULL,
    executed_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status            VARCHAR(255)
    UNIQUE (version)
);

CREATE TABLE migration_lock
(
   is_locked lock BOOLEAN NOT NULL DEFAULT FALSE,
);

INSERT INTO migration_lock VALUES (FALSE)
