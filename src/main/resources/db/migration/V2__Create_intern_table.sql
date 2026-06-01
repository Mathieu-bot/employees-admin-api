CREATE TABLE IF NOT EXISTS intern (
    id          SERIAL PRIMARY KEY,
    firstname   VARCHAR(255) NOT NULL,
    lastname    VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    department  VARCHAR(255) NOT NULL,
    remunerated BOOLEAN NOT NULL DEFAULT FALSE,
    salary      INTEGER,
    manager_id  INTEGER REFERENCES employee(id),
    start_date  DATE NOT NULL,
    end_date    DATE NOT NULL
);
