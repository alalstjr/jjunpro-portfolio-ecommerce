/* https://brownbears.tistory.com/162 postgresql 문법 */

DROP TABLE IF EXISTS ID_SEQUENCE;
CREATE TABLE ID_SEQUENCE
(
    colname SERIAL
);

/* ALTER TABLE account MODIFY (phoneNumber VARCHAR UNIQUE); null 제거 */
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
    phoneNumber  VARCHAR,

    point        INTEGER
);

INSERT INTO ACCOUNT (username, email, enabled, userRole, password, createdDate,
                     modifiedDate, ip, point, agerange, birthday, gender, postcode, addr1)
VALUES ('관리자', 'admin', true, 'ADMIN',
        '{bcrypt}$2a$10$LeNw1of.2HNeTi8/8Xp/IO0D9JMcK/Wq.LzvnlOtNzCP3EwESEKFe',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '0.0.0.0', '100000', '27', '940721', '0', '000',
        '서울');

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
    discount        SMALLINT  NOT NULL,
    point           SMALLINT  NOT NULL,
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
    useCupon         VARCHAR,
    usePoint         INTEGER,
    totalAmount      INTEGER,
    productNames     VARCHAR,
    productIds       VARCHAR,
    productQuantitys VARCHAR,
    productAmounts   VARCHAR,
    productThumbs    VARCHAR,
    accountId        BIGINT,
    orderState       SMALLINT,
    receivePoint     INTEGER
);

/* PRODUCT_ACCESS */
DROP TABLE IF EXISTS PRODUCT_ACCESS;
CREATE TABLE PRODUCT_ACCESS
(
    id           SERIAL PRIMARY KEY,
    ip           VARCHAR   NOT NULL,
    createdDate  TIMESTAMP NOT NULL,
    modifiedDate TIMESTAMP NOT NULL,

    accountId    BIGINT,
    ageRange     SMALLINT,
    birthday     VARCHAR,
    gender       INTEGER,
    addr         VARCHAR,

    productId    BIGINT,
    price        INTEGER,
    discount     BOOLEAN,
    point        BOOLEAN
);