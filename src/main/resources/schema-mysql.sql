-- ------------------------------------------------------------
-- Starter schema for MySQL 8.0
-- charset / engine
-- ------------------------------------------------------------
CREATE DATABASE IF NOT EXISTS starterpack
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;
USE starterpack;

-- ------------------------------------------------------------
-- 1) 멤버 (Member)
-- ------------------------------------------------------------
CREATE TABLE member (
  user_id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  email             VARCHAR(100)    NOT NULL,
  password          VARCHAR(255)    NULL,
  name              VARCHAR(50)     NOT NULL,
  nickname          VARCHAR(50)     NOT NULL,
  provider          VARCHAR(20)     NULL,
  provider_id       VARCHAR(100)    NULL,
  profile_image_url VARCHAR(500)    NULL,
  is_active         BOOLEAN         NOT NULL DEFAULT TRUE,
  role              VARCHAR(20)     NOT NULL DEFAULT 'USER',
  birth_date        DATE,
  gender            VARCHAR(10),
  phone_number      VARCHAR(20),
  refresh_token     VARCHAR(500),
  created_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id),
  UNIQUE KEY uk_member_email (email),
  UNIQUE KEY uk_member_nickname (nickname),
  UNIQUE KEY uk_member_provider_id (provider, provider_id),
  KEY idx_member_provider (provider),
  KEY idx_member_is_active (is_active)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 2) 카테고리 (Category)
-- ------------------------------------------------------------
CREATE TABLE category (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  name        VARCHAR(100)    NOT NULL,
  src         VARCHAR(500)    NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_category_name (name)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 2) 상품 (Product)
-- ------------------------------------------------------------
CREATE TABLE product (
  id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  name          VARCHAR(200)    NOT NULL,
  link          VARCHAR(500)    NULL,
  product_type  VARCHAR(50)     NULL,     -- ERD: type
  src           VARCHAR(500)    NULL,
  cost          INT UNSIGNED    NULL,     -- ERD: cost
  like_count    INT UNSIGNED    NOT NULL DEFAULT 0,  -- ERD: like
  category_id   BIGINT UNSIGNED NULL,     -- ERD: category
  PRIMARY KEY (id),
  KEY idx_product_name (name),
  KEY idx_product_category (category_id),
  CONSTRAINT fk_product_category
    FOREIGN KEY (category_id)
    REFERENCES category(id)
    ON UPDATE CASCADE
    ON DELETE SET NULL,
  CONSTRAINT chk_product_cost_nonneg CHECK (cost IS NULL OR cost >= 0),
  CONSTRAINT chk_product_like_nonneg CHECK (like_count >= 0)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 2.5) 링크 정책 (Link Policy - Blacklist)
-- ------------------------------------------------------------
CREATE TABLE link_policy (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  pattern     VARCHAR(500)    NOT NULL,
  description VARCHAR(1000),
  created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 3) 스타터팩 (Pack)
-- ------------------------------------------------------------
CREATE TABLE pack (
  id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  name             VARCHAR(100)    NOT NULL,
  category_id      BIGINT UNSIGNED NULL,
  total_cost       INT UNSIGNED    NULL,
  pack_like_count  INT UNSIGNED    NOT NULL DEFAULT 0,
  pack_bookmark_count INT UNSIGNED NOT NULL DEFAULT 0,
  src              VARCHAR(500)    NULL,
  description      TEXT            NULL,
  PRIMARY KEY (id),
  KEY idx_pack_category (category_id),
  CONSTRAINT fk_pack_category
    FOREIGN KEY (category_id)
    REFERENCES category(id)
    ON UPDATE CASCADE
    ON DELETE SET NULL,
  CONSTRAINT chk_pack_total_cost_nonneg CHECK (total_cost IS NULL OR total_cost >= 0),
  CONSTRAINT chk_pack_like_nonneg CHECK (pack_like_count >= 0),
  CONSTRAINT chk_pack_bookmark_nonneg CHECK (pack_bookmark_count >= 0)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 4) 상품_스타터팩 매핑 (PtoP)
--    복합 PK + FK, 부모 삭제 시 매핑도 함께 삭제
-- ------------------------------------------------------------
CREATE TABLE pack_product (
  pack_id     BIGINT UNSIGNED NOT NULL,
  product_id  BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (pack_id, product_id),
  KEY idx_pp_product (product_id),
  CONSTRAINT fk_pp_pack
    FOREIGN KEY (pack_id)
    REFERENCES pack(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_pp_product
    FOREIGN KEY (product_id)
    REFERENCES product(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB;
-- ------------------------------------------------------------
-- 5) 사용자 피드 (Feed)
-- ------------------------------------------------------------
CREATE TABLE feed (
    id          BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT,
    user_id     BIGINT UNSIGNED     NOT NULL,
    description TEXT                NULL, -- 피드 설명
    image_url   VARCHAR(500)        NOT NULL,
    category_id BIGINT UNSIGNED     NULL,
    like_count  BIGINT     UNSIGNED    NOT NULL DEFAULT 0,
    bookmark_count  BIGINT UNSIGNED    NOT NULL DEFAULT 0,
    created_at  TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_feed_user (user_id),
    KEY idx_feed_category (category_id),
    KEY idx_feed_like_count (like_count),
    KEY idx_feed_bookmark_count (bookmark_count),
    CONSTRAINT fk_feed_user
        FOREIGN KEY (user_id)
            REFERENCES member(user_id)
            ON UPDATE CASCADE
            ON DELETE CASCADE,
    CONSTRAINT fk_feed_category
        FOREIGN KEY (category_id)
            REFERENCES category(id)
            ON UPDATE CASCADE
            ON DELETE SET NULL,
    CONSTRAINT chk_feed_like_count CHECK (like_count >= 0),
    CONSTRAINT chk_feed_bookmark_count CHECK (bookmark_count >= 0)
) ENGINE=InnoDB;
-- ------------------------------------------------------------
-- 7) 피드 좋아요 (Feed Like)
-- ------------------------------------------------------------
CREATE TABLE feed_like (
   id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
   feed_id     BIGINT UNSIGNED NOT NULL,
   member_id   BIGINT UNSIGNED NOT NULL,
   created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY (id),
   UNIQUE KEY uk_feed_like_member (feed_id, member_id),
   KEY idx_fl_member (member_id),
   CONSTRAINT fk_fl_feed
       FOREIGN KEY (feed_id)
           REFERENCES feed(id)
           ON UPDATE CASCADE
           ON DELETE CASCADE,
   CONSTRAINT fk_fl_member
       FOREIGN KEY (member_id)
           REFERENCES member(user_id)
           ON UPDATE CASCADE
           ON DELETE CASCADE
) ENGINE=InnoDB;
-- ------------------------------------------------------------
-- 8) 피드 북마크 (Feed Bookmark)
-- ------------------------------------------------------------
CREATE TABLE feed_bookmark (
   id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
   feed_id     BIGINT UNSIGNED NOT NULL,
   member_id   BIGINT UNSIGNED NOT NULL,
   created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY (id),
   UNIQUE KEY uk_feed_bookmark_member (feed_id, member_id),
   KEY idx_fb_member (member_id),
   CONSTRAINT fk_fb_feed
       FOREIGN KEY (feed_id)
           REFERENCES feed(id)
           ON UPDATE CASCADE
           ON DELETE CASCADE,
   CONSTRAINT fk_fb_member
       FOREIGN KEY (member_id)
           REFERENCES member(user_id)
           ON UPDATE CASCADE
           ON DELETE CASCADE
) ENGINE=InnoDB;
-- ------------------------------------------------------------
-- 9) 피드 댓글 (Feed Comment)
-- ------------------------------------------------------------
CREATE TABLE feed_comment (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  feed_id     BIGINT UNSIGNED NOT NULL,
  author_id   BIGINT UNSIGNED NOT NULL,
  content     VARCHAR(500)    NOT NULL,
  parent_id   BIGINT UNSIGNED NULL,
  depth       INT             NOT NULL DEFAULT 0,
  is_deleted  BOOLEAN         NOT NULL DEFAULT FALSE,
  deleted_by  ENUM('USER', 'ADMIN') NULL,
  deleted_at  TIMESTAMP       NULL,
  created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_comment_feed (feed_id),
  KEY idx_comment_author (author_id),
  KEY idx_comment_parent (parent_id),
  KEY idx_comment_created_at (created_at),
  CONSTRAINT fk_comment_feed
    FOREIGN KEY (feed_id)
    REFERENCES feed(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_comment_author
    FOREIGN KEY (author_id)
    REFERENCES member(user_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_comment_parent
    FOREIGN KEY (parent_id)
    REFERENCES feed_comment(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB;
-- ------------------------------------------------------------
-- 10) 팩 좋아요 (Pack Like)
-- ------------------------------------------------------------
CREATE TABLE pack_like (
   id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
   pack_id     BIGINT UNSIGNED NOT NULL,
   member_id   BIGINT UNSIGNED NOT NULL,
   created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY (id),
   UNIQUE KEY uk_pack_like_member (pack_id, member_id),
   KEY idx_pl_member (member_id),
   CONSTRAINT fk_pl_pack
       FOREIGN KEY (pack_id)
           REFERENCES pack(id)
           ON UPDATE CASCADE
           ON DELETE CASCADE,
   CONSTRAINT fk_pl_member
       FOREIGN KEY (member_id)
           REFERENCES member(user_id)
           ON UPDATE CASCADE
           ON DELETE CASCADE
) ENGINE=InnoDB;
-- ------------------------------------------------------------
-- 11) 팩 북마크 (Pack Bookmark)
-- ------------------------------------------------------------
CREATE TABLE pack_bookmark (
   id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
   pack_id     BIGINT UNSIGNED NOT NULL,
   member_id   BIGINT UNSIGNED NOT NULL,
   created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY (id),
   UNIQUE KEY uk_pack_bookmark_member (pack_id, member_id),
   KEY idx_pb_member (member_id),
   CONSTRAINT fk_pb_pack
       FOREIGN KEY (pack_id)
           REFERENCES pack(id)
           ON UPDATE CASCADE
           ON DELETE CASCADE,
   CONSTRAINT fk_pb_member
       FOREIGN KEY (member_id)
           REFERENCES member(user_id)
           ON UPDATE CASCADE
           ON DELETE CASCADE
) ENGINE=InnoDB;
-- ------------------------------------------------------------
-- (옵션) 조회 최적화용 인덱스 예시.
-- ------------------------------------------------------------
-- 좋아요 순 상품/팩 랭킹
CREATE INDEX idx_product_like ON product(like_count DESC);
CREATE INDEX idx_pack_like ON pack(pack_like_count DESC);
CREATE INDEX idx_pack_bookmark ON pack(pack_bookmark_count DESC);

-- 카테고리별 비용/정렬 조회가 많다면
CREATE INDEX idx_product_category_cost ON product(category_id, cost);
CREATE INDEX idx_pack_category_cost ON pack(category_id, total_cost);
