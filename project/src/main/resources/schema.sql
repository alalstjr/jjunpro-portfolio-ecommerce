DROP TABLE IF EXISTS ID_SEQUENCE;
CREATE TABLE ID_SEQUENCE
(
    colname SERIAL
);

/* ACCONT */
DROP TABLE IF EXISTS ACCOUNT;
CREATE TABLE ACCOUNT
(
    id           SERIAL PRIMARY KEY,
    ip           VARCHAR   NOT NULL,
    createdDate  TIMESTAMP NOT NULL,
    modifiedDate TIMESTAMP NOT NULL,
    enabled      BOOLEAN   NOT NULL,

    email        VARCHAR   NOT NULL UNIQUE,
    password     VARCHAR,
    username     VARCHAR,

    userRole     VARCHAR   NOT NULL,
    agerange     VARCHAR,
    birthday     VARCHAR,
    gender       SMALLINT,
    postcode     VARCHAR,
    addr1        VARCHAR,
    addr2        VARCHAR,
    phoneNumber  VARCHAR   NOT NULL UNIQUE
);

INSERT INTO ACCOUNT (username, email, enabled, userRole, password, phoneNumber, createdDate,
                     modifiedDate, IP)
VALUES ('김민석', 'mimine02@naver.com', true, 'USER',
        '{bcrypt}$2a$10$RsRh35h1MBRI4s37g/UopeqkQ8Th6rxt8q6bq.fhdr1CWDSfguUg.', '01040211220',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '0.0.0.0');

/* SHOP_GROUP */
DROP TABLE IF EXISTS SHOP_GROUP;
CREATE TABLE SHOP_GROUP
(
    id                SERIAL PRIMARY KEY,
    ip                VARCHAR   NOT NULL,
    createdDate       TIMESTAMP NOT NULL,
    modifiedDate      TIMESTAMP NOT NULL,
    enabled           BOOLEAN   NOT NULL,

    name              VARCHAR   NOT NULL,
    viewOrder         INTEGER,
    parentShopGroupId BIGINT
);

/* ITEM */
DROP TABLE IF EXISTS ITEM;
CREATE TABLE ITEM
(
    id                  SERIAL PRIMARY KEY,
    ip                  VARCHAR   NOT NULL,
    createdDate         TIMESTAMP NOT NULL,
    modifiedDate        TIMESTAMP NOT NULL,
    enabled             BOOLEAN   NOT NULL,

    explanation         VARCHAR,
    tag                 VARCHAR,
    type                VARCHAR,
    maker               VARCHAR,
    origin              VARCHAR,
    brand               VARCHAR,
    model               VARCHAR,
    callQuestion        VARCHAR,
    cuponEnabled        VARCHAR,
    pointEnabled        VARCHAR,
    contentPc           VARCHAR,
    contentM            VARCHAR,
    summaryInfo         VARCHAR,
    price               INTEGER   NOT NULL,
    discount            SMALLINT,
    pointType           SMALLINT,
    point               INTEGER,
    quantity            INTEGER,
    buyMinQuantity      SMALLINT,
    buyMaxQuantity      SMALLINT,
    deliveryType        SMALLINT,
    deliveryPayType     SMALLINT,
    deliveryDefaultPay  SMALLINT,
    deliveryIfPay       SMALLINT,
    deliveryQuantityPay SMALLINT,
    reviewState         BOOLEAN,
    reservationSale     TIMESTAMP,
    endSale             TIMESTAMP
);

/* SUBITEM */
DROP TABLE IF EXISTS SUB_ITEM;
CREATE TABLE SUB_ITEM
(
    id           SERIAL PRIMARY KEY,
    ip           VARCHAR   NOT NULL,
    createdDate  TIMESTAMP NOT NULL,
    modifiedDate TIMESTAMP NOT NULL,
    enabled      BOOLEAN   NOT NULL,

    price        INTEGER   NOT NULL,
    quantity     INTEGER   NOT NULL
);