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

    email        VARCHAR   NOT NULL UNIQUE,-- '이메일',
    password     VARCHAR,-- '비밀번호',
    username     VARCHAR,-- '이름',

    userRole     VARCHAR   NOT NULL,-- '권한',
    agerange     VARCHAR,-- '나이',
    birthday     VARCHAR,-- '생일',
    gender       SMALLINT,-- '성별',
    postcode     VARCHAR,-- '우편번호',
    addr1        VARCHAR,-- '일반 주소',
    addr2        VARCHAR,-- '상세 주소',
    phoneNumber  VARCHAR,-- '핸드폰 번호',

    point        INTEGER-- '상품 구매 포인트'
);

/* 기본 관리자 유저 생성 */
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

    shopName          VARCHAR   NOT NULL,-- '카테고리 이름',
    priority          INTEGER,-- '출력 순서',
    parentShopGroupId BIGINT-- '해당 카테고리의 부모 id 값'
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

    productName     VARCHAR,-- '상품 이름',
    explanation     VARCHAR,-- '상품 설명',
    tag             VARCHAR,-- '상품 태그',
    productType     VARCHAR,-- '상품 타입',
    callQuestion    BOOLEAN,-- '상품 구매상태 설정 그리고 전화요청 문구 출력 여부',
    cuponEnabled    BOOLEAN,-- '쿠폰 사용 여부',
    pointEnabled    BOOLEAN,-- '포인트 사용 여부',
    content         VARCHAR,-- '상품 내용',
    summaryInfo     VARCHAR,-- '상품 상세 정보',
    price           INTEGER   NOT NULL,-- '가격',
    discount        SMALLINT  NOT NULL,-- '할인',
    point           SMALLINT  NOT NULL,-- '적립금',
    quantity        INTEGER,-- '수량',
    buyMinQuantity  INTEGER,-- '최소 구매 수량',
    buyMaxQuantity  INTEGER,-- '최대 구매 수량',
    reviewState     BOOLEAN,-- '상품 리뷰 작성 상태 여부',
    reservationSale TIMESTAMP,-- '상품 구매 가능 날짜 설정',
    endSale         TIMESTAMP,-- '상품 구매 불가 날짜 설정',
    priority        INTEGER,-- '출력 순서',
    shopGroupIds    VARCHAR   NOT NULL,-- '상품의 카테고리 id 값 들',
    fileStorageIds  VARCHAR-- '상품의 파일 id 값 들'
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

    productName  VARCHAR   NOT NULL,-- '서브 상품 이름',
    price        INTEGER   NOT NULL,-- '서브 상품 가격',
    quantity     INTEGER   NOT NULL-- '서브 상품 수량'
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

    deliveryName        VARCHAR   NOT NULL,-- '배달 이름',
    deliveryType        SMALLINT,-- '배달 타입',
    deliveryPayType     SMALLINT,-- '배달 비용 ex) 선불, 후불..',
    deliveryDefaultPay  SMALLINT,-- '기본 배달 비용',
    deliveryIfPay       SMALLINT,-- '배달시 지불 조건 ex) ~ 원 구매시 무료...',
    deliveryQuantityPay SMALLINT
);

/* FILESTORAGE */
DROP TABLE IF EXISTS FILESTORAGE;
CREATE TABLE FILESTORAGE
(
    id              SERIAL PRIMARY KEY,
    createdDate     TIMESTAMP NOT NULL,
    modifiedDate    TIMESTAMP NOT NULL,

    fileName        VARCHAR   NOT NULL,-- '업로드 파일의 기본 이름',
    fileDownloadUri VARCHAR   NOT NULL,-- '불러오는 파일의 주소값',
    fileType        VARCHAR   NOT NULL,-- '파일 형식',
    fileSize        INTEGER   NOT NULL-- '파일 사이즈'
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

    orderName        VARCHAR   NOT NULL,-- '구매한 상품을 나열하여 주문서 제목으로 생성',
    orderEmail       VARCHAR,-- '구매하는 유저의 이메일',
    orderPhone       VARCHAR   NOT NULL,-- '구매하는 유저의 연락처',
    postcode         VARCHAR   NOT NULL,-- '구매하는 유저의 우편번호',
    addr1            VARCHAR   NOT NULL,-- '구매하는 유저의 기본 주소',
    addr2            VARCHAR   NOT NULL,-- '구매하는 유저의 상세 주소',
    memo             VARCHAR   NOT NULL,-- '구매하는 유저의 메모',

    payment          SMALLINT  NOT NULL,-- '구매하는 유저의 구매지불 방법 ex) 무통장, 네이버페이..',
    useCupon         VARCHAR,-- '상품에 사용한 쿠폰 정보',
    usePoint         INTEGER,-- '상품에 사용한 적립금',
    totalAmount      INTEGER,-- '총 구매금액',
    productNames     VARCHAR,-- '상품 이름 들',
    productIds       VARCHAR,-- '상품 id값 들',
    productQuantitys VARCHAR,-- '상품 구매 수량 들',
    productAmounts   VARCHAR,-- '구매 당시 상품 가격 들',
    productThumbs    VARCHAR,-- '구매 당시 상품 썸네일 파일 들',
    accountId        BIGINT,-- '구매하는 유저의 id 정보',
    orderState       SMALLINT,-- '구매 상태',
    receivePoint     INTEGER-- '구매시 적립받는 포인트'
);

/* PRODUCT_ACCESS */
DROP TABLE IF EXISTS PRODUCT_ACCESS;
CREATE TABLE PRODUCT_ACCESS
(
    id           SERIAL PRIMARY KEY,
    ip           VARCHAR   NOT NULL,
    createdDate  TIMESTAMP NOT NULL,
    modifiedDate TIMESTAMP NOT NULL,

    accountId    BIGINT,-- '상품을 본 유저 id',
    ageRange     SMALLINT,-- '상품을 본 유저 나이',
    birthday     VARCHAR,-- '상품을 본 유저 생일',
    gender       INTEGER,-- '상품을 본 유저 성별',
    addr         VARCHAR,-- '상품을 본 유저 주소',

    productId    BIGINT,-- '유저가 본 상품 id',
    price        INTEGER,-- '유저가 본 상품 가격',
    discount     BOOLEAN,-- '유저가 본 상품 할인율',
    point        BOOLEAN-- '유저가 본 상품 적립율'
);