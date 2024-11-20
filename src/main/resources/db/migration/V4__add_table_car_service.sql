CREATE TABLE car_service
(
    id                  SERIAL PRIMARY KEY,
    service_date        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    service_type        VARCHAR(100) NOT NULL,
    service_description TEXT,
    car_id              INT,
    person_id           INT,
    FOREIGN KEY (car_id) REFERENCES car (id) ON DELETE CASCADE,
    FOREIGN KEY (person_id) REFERENCES people (id) ON DELETE CASCADE,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
