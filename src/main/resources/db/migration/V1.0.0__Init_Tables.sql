CREATE SCHEMA IF NOT EXISTS person;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE person.countries
(
    id      SERIAL PRIMARY KEY,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    name    VARCHAR(32),
    alpha2  VARCHAR(2),
    alpha3  VARCHAR(3)
);

CREATE TABLE person.addresses
(
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created    TIMESTAMP NOT NULL,
    updated    TIMESTAMP NOT NULL,
    country_id INTEGER REFERENCES person.countries (id),
    address    VARCHAR(128),
    zip_code   VARCHAR(32),
    archived   TIMESTAMP,
    city       VARCHAR(32),
    state      VARCHAR(32)
);

CREATE TABLE person.users
(
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    secret_key  VARCHAR(64),
    username    VARCHAR(128) NOT NULL,
    created     TIMESTAMP    NOT NULL,
    updated     TIMESTAMP    NOT NULL,
    first_name  VARCHAR(64),
    last_name   VARCHAR(64),
    verified_at TIMESTAMP,
    archived_at TIMESTAMP,
    status      VARCHAR(64),
    filled      BOOLEAN,
    address_id  UUID REFERENCES person.addresses (id)
);

CREATE TABLE person.individuals
(
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID UNIQUE REFERENCES person.users (id),
    passport_number VARCHAR(32),
    phone_number    VARCHAR(32),
    email           VARCHAR(32),
    status          VARCHAR(32)
);

CREATE TABLE person.profile_history
(
    id             UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created        TIMESTAMP NOT NULL,
    profile_id     UUID REFERENCES person.users (id),
    profile_type   VARCHAR(32),
    reason         VARCHAR(255),
    comment        VARCHAR(255),
    changed_values JSONB
);