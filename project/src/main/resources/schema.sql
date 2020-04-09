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
    phoneNumber  VARCHAR   NOT NULL UNIQUE,

    point        INTEGER
);

INSERT INTO ACCOUNT (username, email, enabled, userRole, password, phoneNumber, createdDate,
                     modifiedDate, ip, point)
VALUES ('김민석', 'alalstjr@naver.com', true, 'USER',
        '{bcrypt}$2a$10$LeNw1of.2HNeTi8/8Xp/IO0D9JMcK/Wq.LzvnlOtNzCP3EwESEKFe', '01040211220',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '0.0.0.0', '1000');

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
    point           SMALLINT,
    quantity        INTEGER,
    buyMinQuantity  INTEGER,
    buyMaxQuantity  INTEGER,
    reviewState     BOOLEAN,
    reservationSale TIMESTAMP,
    endSale         TIMESTAMP,
    priority        INTEGER,
    shopGroupIds    VARCHAR   NOT NULL,
    fileStorageIds  VARCHAR
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

/* FILESTORAGE */
DROP TABLE IF EXISTS FILESTORAGE;
CREATE TABLE FILESTORAGE
(
    id              SERIAL PRIMARY KEY,
    createdDate     TIMESTAMP NOT NULL,
    modifiedDate    TIMESTAMP NOT NULL,

    fileName        VARCHAR   NOT NULL,
    fileDownloadUri VARCHAR   NOT NULL,
    fileType        VARCHAR   NOT NULL,
    fileSize        INTEGER   NOT NULL
);

/* PRODUCT_ORDER */
DROP TABLE IF EXISTS PRODUCT_ORDER;
CREATE TABLE PRODUCT_ORDER
(
    id               SERIAL PRIMARY KEY,
    ip               VARCHAR   NOT NULL,
    createdDate      TIMESTAMP NOT NULL,
    modifiedDate     TIMESTAMP NOT NULL,
    enabled          BOOLEAN   NOT NULL,

    orderName        VARCHAR   NOT NULL,
    orderEmail       VARCHAR,
    orderPhone       VARCHAR   NOT NULL,
    postcode         VARCHAR   NOT NULL,
    addr1            VARCHAR   NOT NULL,
    addr2            VARCHAR   NOT NULL,
    memo             VARCHAR   NOT NULL,

    payment          SMALLINT  NOT NULL,
    cupon            VARCHAR,
    point            INTEGER,
    totalAmount      INTEGER,
    productIds       VARCHAR,
    productQuantitys VARCHAR,
    productAmounts   VARCHAR,
    accountId        VARCHAR,
    orderState       SMALLINT
);
