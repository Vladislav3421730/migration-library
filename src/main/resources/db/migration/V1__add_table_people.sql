CREATE TABLE people
(
    id            SERIAL PRIMARY KEY,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    gender        VARCHAR(10),
    date_of_birth DATE,
    email         VARCHAR(255) UNIQUE,
    phone_number  VARCHAR(20),
    address       VARCHAR(255),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
