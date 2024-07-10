DROP TABLE IF EXISTS app_user;

CREATE TABLE app_user (
    ID UUID PRIMARY KEY,
    NAME VARCHAR(250) NOT NULL,
    USERNAME VARCHAR(250) NOT NULL,
    SURNAME VARCHAR(250) NOT NULL,
    PASSWORD VARCHAR(250) NOT NULL,
    UNIQUE (USERNAME)
);

CREATE INDEX idx_username ON app_user(USERNAME);

DROP TABLE IF EXISTS interest_rate;

CREATE TABLE interest_rate (
                               id UUID PRIMARY KEY,
                               interest_rate DECIMAL(4, 2) NOT NULL,
                               created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_interest_rate ON interest_rate(interest_rate);

DROP TABLE IF EXISTS config_setting;
CREATE TABLE config_setting (
                                 id UUID PRIMARY KEY,
                                 key_name VARCHAR(255) NOT NULL,
                                 "value" VARCHAR(255) NOT NULL,
                                 description TEXT
);
