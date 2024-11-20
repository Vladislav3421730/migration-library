CREATE TABLE house
(
    id            SERIAL PRIMARY KEY,
    address       VARCHAR(255)   NOT NULL,
    house_type    VARCHAR(50)    NOT NULL,
    area          DECIMAL(10, 2) NOT NULL,
    price         DECIMAL(15, 2) NOT NULL,
    owner_id      INT,
    is_rented     BOOLEAN   DEFAULT FALSE,
    rent_price    DECIMAL(15, 2),
    purchase_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES people (id) ON DELETE CASCADE
);
