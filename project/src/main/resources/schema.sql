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

    shopName          VARCHAR   NOT NULL,
    priority          INTEGER,
    parentShopGroupId BIGINT
);

/* PRODUCT */
DROP TABLE IF EXISTS PRODUCT;
CREATE TABLE PRODUCT
(
    id              SERIAL PRIMARY KEY,
    ip              VARCHAR   NOT NULL,
    createdDate     TIMESTAMP NOT NULL,
    modifiedDate    TIMESTAMP NOT NULL,
    enabled         BOOLEAN   NOT NULL,

    productName     VARCHAR,
    explanation     VARCHAR,
    tag             VARCHAR,
    productType     VARCHAR,
    callQuestion    BOOLEAN,
    cuponEnabled    BOOLEAN,
    pointEnabled    BOOLEAN,
    content         VARCHAR,
    summaryInfo     VARCHAR,
    price           INTEGER   NOT NULL,
    discount        SMALLINT,
    point           INTEGER,
    quantity        INTEGER,
    buyMinQuantity  INTEGER,
    buyMaxQuantity  INTEGER,
    reviewState     BOOLEAN,
    reservationSale TIMESTAMP,
    endSale         TIMESTAMP,
    priority        INTEGER,
    shopGroupIds    VARCHAR   NOT NULL
);

/* SUB_PRODUCT */
DROP TABLE IF EXISTS SUB_PRODUCT;
CREATE TABLE SUB_PRODUCT
(
    id           SERIAL PRIMARY KEY,
    ip           VARCHAR   NOT NULL,
    createdDate  TIMESTAMP NOT NULL,
    modifiedDate TIMESTAMP NOT NULL,
    enabled      BOOLEAN   NOT NULL,

    productName  VARCHAR   NOT NULL,
    price        INTEGER   NOT NULL,
    quantity     INTEGER   NOT NULL
);

/* DELIVERY */
DROP TABLE IF EXISTS DELIVERY;
CREATE TABLE DELIVERY
(
    id                  SERIAL PRIMARY KEY,
    ip                  VARCHAR   NOT NULL,
    createdDate         TIMESTAMP NOT NULL,
    modifiedDate        TIMESTAMP NOT NULL,
    enabled             BOOLEAN   NOT NULL,

    deliveryName        VARCHAR   NOT NULL,
    deliveryType        SMALLINT,
    deliveryPayType     SMALLINT,
    deliveryDefaultPay  SMALLINT,
    deliveryIfPay       SMALLINT,
    deliveryQuantityPay SMALLINT
);