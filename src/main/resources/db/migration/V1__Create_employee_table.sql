CREATE TABLE IF NOT EXISTS employee (
    id          SERIAL PRIMARY KEY,
    firstname   VARCHAR(255) NOT NULL,
    lastname    VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    department  VARCHAR(255) NOT NULL,
    salary      INTEGER NOT NULL,
    active      BOOLEAN NOT NULL DEFAULT TRUE
);
