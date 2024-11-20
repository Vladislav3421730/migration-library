CREATE TABLE work
(
    id           SERIAL PRIMARY KEY,
    job_title    VARCHAR(100) NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    start_date   DATE,
    end_date     DATE,
    description  TEXT,
    employee_id  INT,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES people (id)
);
