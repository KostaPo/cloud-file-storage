CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    role VARCHAR(255) CHECK (role IN ('ROLE_GUEST', 'ROLE_USER', 'ROLE_ADMIN')),
    PRIMARY KEY (id)
);

INSERT INTO users (password, role, username)
VALUES ('$2a$10$KXxaOGb7DFws7fMBEUW0ounkSDzWzlDcWP03JtPzsMb9y/eGdCE/G', 'ROLE_USER', 'NoUniqUser');