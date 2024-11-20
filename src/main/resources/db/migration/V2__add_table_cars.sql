CREATE TABLE car
(
    id         SERIAL PRIMARY KEY,
    make       VARCHAR(50) NOT NULL,
    model      VARCHAR(50) NOT NULL,
    year       INT,
    color      VARCHAR(30),
    owner_id   INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES people (id)
);
