DROP TABLE IF EXISTS ID_SEQUENCE;
CREATE TABLE ID_SEQUENCE
(
    colname SERIAL
);

DROP TABLE IF EXISTS ACCOUNT;
CREATE TABLE ACCOUNT
(
    ID           SERIAL PRIMARY KEY,
    EMAIL        VARCHAR NOT NULL UNIQUE,
    PASSWORD     VARCHAR,
    USERNAME     VARCHAR,
    ENABLED      BOOLEAN NOT NULL,

    USERROLE     VARCHAR NOT NULL,
    AGERANGE     VARCHAR,
    BIRTHDAY     VARCHAR,
    GENDER       SMALLINT,
    POSTCODE     VARCHAR,
    ADDR1        VARCHAR,
    ADDR2        VARCHAR,
    PHONENUMBER  VARCHAR NOT NULL UNIQUE,

    CREATEDDATE  TIMESTAMP NOT NULL,
    MODIFIEDDATE TIMESTAMP
);

INSERT INTO ACCOUNT (USERNAME, EMAIL, ENABLED, USERROLE, PASSWORD, PHONENUMBER, CREATEDDATE)
VALUES ('김민석', 'mimine02@naver.com', true, 'USER',
        '{bcrypt}$2a$10$RsRh35h1MBRI4s37g/UopeqkQ8Th6rxt8q6bq.fhdr1CWDSfguUg.', '01040211220', CURRENT_TIMESTAMP);